package com.faceit.controller;

import com.faceit.service.RedisStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/redis-stats")
@RequiredArgsConstructor
public class RedisStatsController {

    private final RedisStatsService redisStatsService;

    @GetMapping("/team/{teamId}")
    public ResponseEntity<Map<String, Object>> getTeamStats(@PathVariable Integer teamId) {
        return ResponseEntity.ok(redisStatsService.getAllTeamStats(teamId));
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<Map<String, Object>> getPlayerStats(@PathVariable Integer playerId) {
        return ResponseEntity.ok(redisStatsService.getAllPlayerStats(playerId));
    }

    @DeleteMapping("/team/{teamId}")
    public ResponseEntity<Void> clearTeamStats(@PathVariable Integer teamId) {
        redisStatsService.clearTeamStats(teamId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/player/{playerId}")
    public ResponseEntity<Void> clearPlayerStats(@PathVariable Integer playerId) {
        redisStatsService.clearPlayerStats(playerId);
        return ResponseEntity.noContent().build();
    }
}
