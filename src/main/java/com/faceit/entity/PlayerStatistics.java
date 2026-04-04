package com.faceit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "player_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatistics {

    @Id
    @Column(name = "player_id")
    private Integer playerId;

    @Min(value = 0, message = "Kills cannot be negative")
    @Column(name = "kills", nullable = false)
    private Integer kills = 0;

    @Min(value = 0, message = "Assists cannot be negative")
    @Column(name = "assists", nullable = false)
    private Integer assists = 0;

    @Min(value = 0, message = "Deaths cannot be negative")
    @Column(name = "deaths", nullable = false)
    private Integer deaths = 0;

    @Min(value = 0, message = "Matches played cannot be negative")
    @Column(name = "matches_played", nullable = false)
    private Integer matchesPlayed = 0;

    @Min(value = 0, message = "Matches won cannot be negative")
    @Column(name = "matches_won", nullable = false)
    private Integer matchesWon = 0;

    // Relationship with Player
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", insertable = false, updatable = false)
    private Player player;
}
