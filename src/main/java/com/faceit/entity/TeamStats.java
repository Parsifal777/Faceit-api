package com.faceit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "team_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamStats {

    @Id
    @Column(name = "team_id")
    private Integer teamId;

    @DecimalMin(value = "0.00", message = "Win rate cannot be negative")
    @Digits(integer = 3, fraction = 2, message = "Win rate must have up to 3 digits and 2 decimal places")
    @Column(name = "win_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal winRate = BigDecimal.ZERO;

    // Relationship with Team
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", insertable = false, updatable = false)
    private Team team;
}
