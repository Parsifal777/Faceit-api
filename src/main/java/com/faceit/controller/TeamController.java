package com.faceit.controller;

import com.faceit.dto.TeamRequest;
import com.faceit.dto.TeamResponse;
import com.faceit.entity.Team;
import com.faceit.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamRepository teamRepository;

    // GET: получить все команды
    @GetMapping
    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // GET: получить команду по ID
    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable Integer id) {
        return teamRepository.findById(id)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: создать новую команду
    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@RequestBody TeamRequest request) {
        // Проверяем, что имя не пустое
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Создаём новую команду
        Team team = new Team();
        team.setName(request.getName());

        // Сохраняем в базу
        Team savedTeam = teamRepository.save(team);

        // Возвращаем ответ
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(savedTeam));
    }

    // Вспомогательный метод: конвертация Entity → Response DTO
    private TeamResponse convertToResponse(Team team) {
        return new TeamResponse(team.getTeamId(), team.getName());
    }
}
