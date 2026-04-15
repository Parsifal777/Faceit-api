package com.faceit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatsResponse {
    private Integer playerId;
    private String nickname;
    private Integer teamId;
    private String teamName;
    private Integer kills;
    private Integer assists;
    private Integer deaths;
    private Integer matchesPlayed;
    private Integer matchesWon;
    private BigDecimal winRate;
    private BigDecimal kd;
    private BigDecimal kr;
}