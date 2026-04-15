package com.faceit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamStatsResponse {
    private Integer teamId;
    private String teamName;
    private Integer matchesPlayed;
    private Integer matchesWon;
    private BigDecimal winRate;
}