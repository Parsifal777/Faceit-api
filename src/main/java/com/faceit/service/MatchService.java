package com.faceit.service;

import com.faceit.dto.MatchRequest;
import com.faceit.entity.*;
import com.faceit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final PlayerMatchStatsRepository playerMatchStatsRepository;
    private final TeamRepository teamRepository;
    private final TeamStatisticsRepository teamStatisticsRepository;
    private final TeamStatsRepository teamStatsRepository;
    private final PlayerStatisticsRepository playerStatisticsRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final PlayerRepository playerRepository;
    private final RedisStatsService redisStatsService;

    private static final int MAX_ROUNDS = 13;

    @Transactional
    public void addMatch(MatchRequest request) {
        // Проверка: существуют ли команды
        if (!teamRepository.existsById(request.getTeam1Id())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team1 not found: " + request.getTeam1Id());
        }
        if (!teamRepository.existsById(request.getTeam2Id())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team2 not found: " + request.getTeam2Id());
        }

        // Проверка: счёт корректен
        if (request.getTeam1Score() < 0 || request.getTeam2Score() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Scores cannot be negative");
        }
        if (request.getTeam1Score() > MAX_ROUNDS || request.getTeam2Score() > MAX_ROUNDS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max rounds is " + MAX_ROUNDS);
        }

        if (request.getTeam1Score() == 13 && request.getTeam2Score() > 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid score: If team has 13 rounds, opponent cannot have more than 11 rounds (max 24 total)");
        }
        if (request.getTeam2Score() == 13 && request.getTeam1Score() > 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid score: If team has 13 rounds, opponent cannot have more than 11 rounds (max 24 total)");
        }

        // Дополнительная проверка: если у одной команды больше 13, у другой не может быть больше разницы
        if (request.getTeam1Score() > 13 && request.getTeam2Score() > request.getTeam1Score() - 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid score: To win with more than 13 rounds, you need at least 2 round advantage");
        }
        if (request.getTeam2Score() > 13 && request.getTeam1Score() > request.getTeam2Score() - 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid score: To win with more than 13 rounds, you need at least 2 round advantage");
        }

        Integer winnerTeamId = null;
        int team1Won = 0;
        int team2Won = 0;

        if (request.getTeam1Score() > request.getTeam2Score()) {
            winnerTeamId = request.getTeam1Id();
            team1Won = 1;
        } else if (request.getTeam2Score() > request.getTeam1Score()) {
            winnerTeamId = request.getTeam2Id();
            team2Won = 1;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Match cannot be a draw");
        }

        // Создаём матч
        Match match = new Match();
        match.setTeam1Id(request.getTeam1Id());
        match.setTeam2Id(request.getTeam2Id());
        match.setTeam1Score(request.getTeam1Score());
        match.setTeam2Score(request.getTeam2Score());
        match.setWinnerTeamId(winnerTeamId);
        match.setMatchDate(LocalDateTime.now());
        match.setMapName(request.getMapName());

        Match savedMatch = matchRepository.save(match);

        // Обновляем статистику команд (PostgreSQL + Redis)
        updateTeamStats(request.getTeam1Id(), team1Won);
        updateTeamStats(request.getTeam2Id(), team2Won);

        // Обновляем статистику игроков
        if (request.getPlayerStats() != null) {
            for (MatchRequest.PlayerMatchStatRequest stat : request.getPlayerStats()) {
                if (!playerRepository.existsById(stat.getPlayerId())) {
                    continue;
                }

                // Сохраняем статистику игрока в матче
                PlayerMatchStats playerMatchStats = new PlayerMatchStats();
                playerMatchStats.setMatchId(savedMatch.getMatchId());
                playerMatchStats.setPlayerId(stat.getPlayerId());
                playerMatchStats.setKills(stat.getKills());
                playerMatchStats.setAssists(stat.getAssists());
                playerMatchStats.setDeaths(stat.getDeaths());
                playerMatchStatsRepository.save(playerMatchStats);

                int playerWon = 0;
                Integer playerTeamId = getPlayerTeamId(stat.getPlayerId());
                if (playerTeamId != null && playerTeamId.equals(winnerTeamId)) {
                    playerWon = 1;
                }

                // Обновляем статистику игрока (PostgreSQL + Redis)
                updatePlayerStats(stat.getPlayerId(), stat.getKills(), stat.getAssists(), stat.getDeaths(), playerWon);
            }
        }
    }

    private void updateTeamStats(Integer teamId, int won) {
        //Сохраняем в PostgreSQL (постоянное хранилище)
        TeamStatistics teamStats = teamStatisticsRepository.findByTeamId(teamId)
                .orElseGet(() -> {
                    TeamStatistics ts = new TeamStatistics();
                    ts.setTeamId(teamId);
                    ts.setMatchesPlayed(0);
                    ts.setMatchesWon(0);
                    return ts;
                });

        teamStats.setMatchesPlayed(teamStats.getMatchesPlayed() + 1);
        teamStats.setMatchesWon(teamStats.getMatchesWon() + won);
        teamStatisticsRepository.save(teamStats);

        //Обновляем Redis (быстрый кэш)
        redisStatsService.incrementTeamMatches(teamId);
        if (won == 1) {
            redisStatsService.incrementTeamWins(teamId);
        }
    }

    private void updatePlayerStats(Integer playerId, int kills, int assists, int deaths, int won) {
        PlayerStatistics playerStats = playerStatisticsRepository.findByPlayerId(playerId)
                .orElseGet(() -> {
                    PlayerStatistics ps = new PlayerStatistics();
                    ps.setPlayerId(playerId);
                    ps.setKills(0);
                    ps.setAssists(0);
                    ps.setDeaths(0);
                    ps.setMatchesPlayed(0);
                    ps.setMatchesWon(0);
                    return ps;
                });

        playerStats.setKills(playerStats.getKills() + kills);
        playerStats.setAssists(playerStats.getAssists() + assists);
        playerStats.setDeaths(playerStats.getDeaths() + deaths);
        playerStats.setMatchesPlayed(playerStats.getMatchesPlayed() + 1);
        playerStats.setMatchesWon(playerStats.getMatchesWon() + won);
        playerStatisticsRepository.save(playerStats);

        BigDecimal kd = BigDecimal.ZERO;
        if (playerStats.getDeaths() > 0) {
            kd = BigDecimal.valueOf(playerStats.getKills())
                    .divide(BigDecimal.valueOf(playerStats.getDeaths()), 2, RoundingMode.HALF_UP);
        } else if (playerStats.getKills() > 0) {
            kd = BigDecimal.valueOf(playerStats.getKills());
        }

        int totalRounds = playerStats.getMatchesPlayed() * 15;
        BigDecimal kr = BigDecimal.ZERO;
        if (totalRounds > 0) {
            kr = BigDecimal.valueOf(playerStats.getKills())
                    .divide(BigDecimal.valueOf(totalRounds), 2, RoundingMode.HALF_UP);
        }

        BigDecimal winRate = BigDecimal.ZERO;
        if (playerStats.getMatchesPlayed() > 0) {
            winRate = BigDecimal.valueOf(playerStats.getMatchesWon())
                    .divide(BigDecimal.valueOf(playerStats.getMatchesPlayed()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        PlayerStats playerStatsCalc = playerStatsRepository.findByPlayerId(playerId)
                .orElseGet(() -> {
                    PlayerStats ps = new PlayerStats();
                    ps.setPlayerId(playerId);
                    ps.setWinRate(BigDecimal.ZERO);
                    ps.setKd(BigDecimal.ZERO);
                    ps.setKr(BigDecimal.ZERO);
                    return ps;
                });
        playerStatsCalc.setWinRate(winRate);
        playerStatsCalc.setKd(kd);
        playerStatsCalc.setKr(kr);
        playerStatsRepository.save(playerStatsCalc);

        redisStatsService.incrementPlayerMatches(playerId);
        redisStatsService.incrementPlayerKills(playerId, kills);
        redisStatsService.incrementPlayerAssists(playerId, assists);
        redisStatsService.incrementPlayerDeaths(playerId, deaths);
        if (won == 1) {
            redisStatsService.incrementPlayerWins(playerId);
        }
    }

    private Integer getPlayerTeamId(Integer playerId) {
        return playerRepository.findTeamIdByPlayerId(playerId);
    }
}
