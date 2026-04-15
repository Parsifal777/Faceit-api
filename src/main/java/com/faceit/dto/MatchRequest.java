package com.faceit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequest {

    private Integer team1Id;
    private Integer team2Id;
    private Integer team1Score;
    private Integer team2Score;
    private String mapName;

    // Статистика игроков в этом матче
    private List<PlayerMatchStatRequest> playerStats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerMatchStatRequest {
        private Integer playerId;
        private Integer kills;
        private Integer assists;
        private Integer deaths;
    }
}