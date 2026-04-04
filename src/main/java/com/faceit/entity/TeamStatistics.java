package com.faceit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamStatistics {

    @Id
    @Column(name = "team_id")
    private Integer teamId;

    @Min(value = 0, message = "Matches played cannot be negative")
    @Column(name = "matches_played", nullable = false)
    private Integer matchesPlayed = 0;

    @Min(value = 0, message = "Matches won cannot be negative")
    @Column(name = "matches_won", nullable = false)
    private Integer matchesWon = 0;

    // Relationship with Team
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", insertable = false, updatable = false)
    private Team team;
}
