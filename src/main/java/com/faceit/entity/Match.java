package com.faceit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "match")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Integer matchId;

    @Column(name = "team1_id", nullable = false)
    private Integer team1Id;

    @Column(name = "team2_id", nullable = false)
    private Integer team2Id;

    @Column(name = "team1_score", nullable = false)
    private Integer team1Score;

    @Column(name = "team2_score", nullable = false)
    private Integer team2Score;

    @Column(name = "winner_team_id")
    private Integer winnerTeamId;

    @Column(name = "match_date", nullable = false)
    private LocalDateTime matchDate;

    @Column(name = "map_name")
    private String mapName;

    // Связи с командами
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team1_id", insertable = false, updatable = false)
    private Team team1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team2_id", insertable = false, updatable = false)
    private Team team2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_team_id", insertable = false, updatable = false)
    private Team winnerTeam;
}