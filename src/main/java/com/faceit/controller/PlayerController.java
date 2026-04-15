package com.faceit.controller;

import com.faceit.dto.PlayerRequest;
import com.faceit.dto.PlayerResponse;
import com.faceit.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    // GET: получить всех игроков
    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    // GET: получить игрока по ID
    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayerById(@PathVariable Integer id) {
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }

    // GET: получить игроков по ID команды
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<PlayerResponse>> getPlayersByTeam(@PathVariable Integer teamId) {
        return ResponseEntity.ok(playerService.getPlayersByTeam(teamId));
    }

    // POST: создать нового игрока
    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody PlayerRequest request) {
        PlayerResponse response = playerService.createPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT: переместить игрока в другую команду
    @PutMapping("/{playerId}/team/{newTeamId}")
    public ResponseEntity<PlayerResponse> movePlayerToTeam(@PathVariable Integer playerId,
                                                           @PathVariable Integer newTeamId) {
        PlayerResponse response = playerService.updatePlayerTeam(playerId, newTeamId);
        return ResponseEntity.ok(response);
    }

    // DELETE: полностью удалить игрока
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Integer id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}
