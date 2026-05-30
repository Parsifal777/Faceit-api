package com.faceit.service;

import com.faceit.annotation.Loggable;
import com.faceit.annotation.PerformanceLog;
import com.faceit.dto.TeamRequest;
import com.faceit.dto.TeamResponse;
import com.faceit.entity.Team;
import com.faceit.repository.TeamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    // Ручной конструктор вместо Lombok
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @PerformanceLog
    @Loggable(Loggable.LogLevel.INFO)
    public List<TeamResponse> getAllTeams() {
        // Используем JOIN FETCH для загрузки игроков одним запросом
        return teamRepository.findAllWithPlayers().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @PerformanceLog
    @Loggable(Loggable.LogLevel.INFO)
    public TeamResponse getTeamById(Integer id) {
        // Оптимизированный метод с JOIN FETCH (загружаем команду + игроков + их статистику)
        Team team = teamRepository.findByIdWithPlayersAndStats(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + id));
        return convertToResponse(team);
    }

    public TeamResponse getTeamByIdWithPlayers(Integer id) {
        Team team = teamRepository.findByIdWithPlayers(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + id));
        return convertToResponse(team);
    }

    @Loggable(Loggable.LogLevel.WARN)
    @Transactional
    public TeamResponse createTeam(TeamRequest request) {
        if (teamRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Team name already exists: " + request.getName());
        }

        Team team = new Team();
        team.setName(request.getName());

        Team savedTeam = teamRepository.save(team);
        return convertToResponse(savedTeam);
    }

    @Loggable(Loggable.LogLevel.ERROR)
    @Transactional
    public void deleteTeam(Integer id) {
        if (!teamRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + id);
        }
        teamRepository.deleteById(id);
    }

    private TeamResponse convertToResponse(Team team) {
        return new TeamResponse(
                team.getTeamId(),
                team.getName()
                // Если нужно вернуть количество игроков, можно добавить:
                // team.getPlayers() != null ? team.getPlayers().size() : 0
        );
    }
}