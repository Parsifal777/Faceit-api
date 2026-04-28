package com.faceit.service;

import com.faceit.dto.PlayerRequest;
import com.faceit.dto.PlayerResponse;
import com.faceit.entity.Player;
import com.faceit.entity.Team;
import com.faceit.repository.PlayerRepository;
import com.faceit.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    private static final int MAX_PLAYERS_PER_TEAM = 5;


    public List<PlayerResponse> getAllPlayers() {
        // Используем метод с @EntityGraph (загружаем team и playerStatistics за 1 запрос)
        return playerRepository.findAllByOrderByNicknameAsc().stream()
                .map(this::convertToResponseOptimized)
                .collect(Collectors.toList());
    }

    public PlayerResponse getPlayerById(Integer id) {
        // Оптимизированный метод с JOIN FETCH (все данные за 1 запрос)
        Player player = playerRepository.findByIdWithAllData(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found with id: " + id));
        return convertToResponseOptimized(player);
    }

    public List<PlayerResponse> getPlayersByTeam(Integer teamId) {
        // Проверяем, существует ли команда
        if (!teamRepository.existsById(teamId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + teamId);
        }

        // Оптимизированный метод - загружаем всех игроков команды со статистикой за 1 запрос
        return playerRepository.findByTeamIdWithAllData(teamId).stream()
                .map(this::convertToResponseOptimized)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlayerResponse createPlayer(PlayerRequest request) {
        // Проверка: существует ли команда
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Team not found with id: " + request.getTeamId()));

        // Проверка: не занят ли никнейм
        if (playerRepository.existsByNicknameIgnoreCase(request.getNickname())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Nickname already exists: " + request.getNickname());
        }

        // Проверка: не превышен ли лимит игроков в команде (максимум 5)
        int currentPlayersCount = playerRepository.countByTeamId(request.getTeamId());
        if (currentPlayersCount >= MAX_PLAYERS_PER_TEAM) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Team '%s' already has %d players (max %d). Cannot add more players.",
                            team.getName(), currentPlayersCount, MAX_PLAYERS_PER_TEAM));
        }

        // Создаём игрока
        Player player = new Player();
        player.setNickname(request.getNickname());
        player.setTeamId(request.getTeamId());

        Player savedPlayer = playerRepository.save(player);
        return convertToResponseOptimized(savedPlayer);
    }

    @Transactional
    public PlayerResponse updatePlayerTeam(Integer playerId, Integer newTeamId) {
        // Оптимизированная загрузка игрока
        Player player = playerRepository.findByIdWithTeam(playerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Player not found with id: " + playerId));

        // Если игрок уже в этой команде, ничего не делаем
        if (player.getTeamId() != null && player.getTeamId().equals(newTeamId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Player is already in this team");
        }

        // Проверка: существует ли новая команда
        Team newTeam = teamRepository.findById(newTeamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Team not found with id: " + newTeamId));

        // Проверка: не превышен ли лимит в новой команде
        int currentPlayersCount = playerRepository.countByTeamId(newTeamId);
        if (currentPlayersCount >= MAX_PLAYERS_PER_TEAM) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Team '%s' already has %d players (max %d). Cannot add more players.",
                            newTeam.getName(), currentPlayersCount, MAX_PLAYERS_PER_TEAM));
        }

        // Обновляем команду игрока
        player.setTeamId(newTeamId);
        Player updatedPlayer = playerRepository.save(player);

        return convertToResponseOptimized(updatedPlayer);
    }

    @Transactional
    public void deletePlayer(Integer id) {
        if (!playerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found with id: " + id);
        }
        playerRepository.deleteById(id);
    }

    private PlayerResponse convertToResponseOptimized(Player player) {

        String teamName = null;
        if (player.getTeam() != null) {
            teamName = player.getTeam().getName();
        }

        return new PlayerResponse(
                player.getPlayerId(),
                player.getNickname(),
                player.getTeamId(),
                teamName
        );
    }
}
