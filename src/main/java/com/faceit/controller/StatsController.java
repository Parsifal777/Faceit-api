package com.faceit.controller;

import com.faceit.dto.PlayerStatsResponse;
import com.faceit.dto.TeamStatsResponse;
import com.faceit.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/teams")
    public ResponseEntity<List<TeamStatsResponse>> getAllTeamStats() {
        return ResponseEntity.ok(statsService.getAllTeamStats());
    }

    @GetMapping("/teams/{teamId}")
    public ResponseEntity<TeamStatsResponse> getTeamStats(@PathVariable Integer teamId) {
        return ResponseEntity.ok(statsService.getTeamStats(teamId));
    }

    @GetMapping("/players")
    public ResponseEntity<List<PlayerStatsResponse>> getAllPlayerStats() {
        return ResponseEntity.ok(statsService.getAllPlayerStats());
    }

    @GetMapping("/players/{playerId}")
    public ResponseEntity<PlayerStatsResponse> getPlayerStats(@PathVariable Integer playerId) {
        return ResponseEntity.ok(statsService.getPlayerStats(playerId));
    }
}
