package com.faceit.service;

import com.faceit.dto.PlayerStatsResponse;
import com.faceit.dto.TeamStatsResponse;
import com.faceit.entity.*;
import com.faceit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final TeamRepository teamRepository;
    private final TeamStatisticsRepository teamStatisticsRepository;
    private final TeamStatsRepository teamStatsRepository;
    private final PlayerRepository playerRepository;
    private final PlayerStatisticsRepository playerStatisticsRepository;
    private final PlayerStatsRepository playerStatsRepository;

    public List<TeamStatsResponse> getAllTeamStats() {
        return teamRepository.findAll().stream()
                .map(this::convertToTeamStatsResponse)
                .collect(Collectors.toList());
    }

    public TeamStatsResponse getTeamStats(Integer teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
        return convertToTeamStatsResponse(team);
    }

    public List<PlayerStatsResponse> getAllPlayerStats() {
        return playerRepository.findAll().stream()
                .map(this::convertToPlayerStatsResponse)
                .collect(Collectors.toList());
    }

    public PlayerStatsResponse getPlayerStats(Integer playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));
        return convertToPlayerStatsResponse(player);
    }

    private TeamStatsResponse convertToTeamStatsResponse(Team team) {
        // Используем orElseGet с созданием объекта через new и setter'ы
        TeamStatistics statistics = teamStatisticsRepository.findByTeamId(team.getTeamId())
                .orElseGet(() -> {
                    TeamStatistics ts = new TeamStatistics();
                    ts.setTeamId(team.getTeamId());
                    ts.setMatchesPlayed(0);
                    ts.setMatchesWon(0);
                    return ts;
                });

        TeamStats stats = teamStatsRepository.findByTeamId(team.getTeamId())
                .orElseGet(() -> {
                    TeamStats ts = new TeamStats();
                    ts.setTeamId(team.getTeamId());
                    ts.setWinRate(BigDecimal.ZERO);
                    return ts;
                });

        return new TeamStatsResponse(
                team.getTeamId(),
                team.getName(),
                statistics.getMatchesPlayed(),
                statistics.getMatchesWon(),
                stats.getWinRate()
        );
    }

    private PlayerStatsResponse convertToPlayerStatsResponse(Player player) {
        PlayerStatistics statistics = playerStatisticsRepository.findByPlayerId(player.getPlayerId())
                .orElseGet(() -> {
                    PlayerStatistics ps = new PlayerStatistics();
                    ps.setPlayerId(player.getPlayerId());
                    ps.setKills(0);
                    ps.setAssists(0);
                    ps.setDeaths(0);
                    ps.setMatchesPlayed(0);
                    ps.setMatchesWon(0);
                    return ps;
                });

        PlayerStats stats = playerStatsRepository.findByPlayerId(player.getPlayerId())
                .orElseGet(() -> {
                    PlayerStats ps = new PlayerStats();
                    ps.setPlayerId(player.getPlayerId());
                    ps.setWinRate(BigDecimal.ZERO);
                    ps.setKd(BigDecimal.ZERO);
                    ps.setKr(BigDecimal.ZERO);
                    return ps;
                });

        final String[] teamName = {null};
        if (player.getTeamId() != null) {
            teamRepository.findById(player.getTeamId()).ifPresent(team -> teamName[0] = team.getName());
        }

        return new PlayerStatsResponse(
                player.getPlayerId(),
                player.getNickname(),
                player.getTeamId(),
                teamName[0],
                statistics.getKills(),
                statistics.getAssists(),
                statistics.getDeaths(),
                statistics.getMatchesPlayed(),
                statistics.getMatchesWon(),
                stats.getWinRate(),
                stats.getKd(),
                stats.getKr()
        );
    }
}
