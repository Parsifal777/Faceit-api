package com.faceit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "player")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Integer playerId;

    @NotBlank(message = "Nickname is required")
    @Size(min = 3, max = 50, message = "Nickname must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Nickname can only contain letters, numbers and underscore")
    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(name = "team_id", nullable = false)  // ← добавь nullable = false
    private Integer teamId;

    // Relationship with Team (Many to One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", insertable = false, updatable = false)
    private Team team;

    // Relationship with PlayerStatistics (One to One)
    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PlayerStatistics playerStatistics;

    // Relationship with PlayerStats (One to One)
    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PlayerStats playerStats;
}
