package com.faceit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "player_match_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerMatchStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "match_id", nullable = false)
    private Integer matchId;

    @Column(name = "player_id", nullable = false)
    private Integer playerId;

    @Column(name = "kills", nullable = false)
    private Integer kills = 0;

    @Column(name = "assists", nullable = false)
    private Integer assists = 0;

    @Column(name = "deaths", nullable = false)
    private Integer deaths = 0;

    // Связи
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", insertable = false, updatable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", insertable = false, updatable = false)
    private Player player;
}