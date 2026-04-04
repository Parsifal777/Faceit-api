package com.faceit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Integer teamId;

    @NotBlank(message = "Team name is required")
    @Size(min = 2, max = 100, message = "Team name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // Relationship with Player (One to Many)
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    // Relationship with TeamStatistics (One to One)
    @OneToOne(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TeamStatistics teamStatistics;

    // Relationship with TeamStats (One to One)
    @OneToOne(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TeamStats teamStats;
}
