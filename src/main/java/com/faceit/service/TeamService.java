package com.faceit.service;

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

    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TeamResponse getTeamById(Integer id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + id));
        return convertToResponse(team);
    }

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

    @Transactional
    public void deleteTeam(Integer id) {
        if (!teamRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + id);
        }
        teamRepository.deleteById(id);
    }

    private TeamResponse convertToResponse(Team team) {
        return new TeamResponse(team.getTeamId(), team.getName());
    }
}
