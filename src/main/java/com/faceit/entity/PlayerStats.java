package com.faceit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "player_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStats {

    @Id
    @Column(name = "player_id")
    private Integer playerId;

    @DecimalMin(value = "0.00", message = "Win rate cannot be negative")
    @Digits(integer = 3, fraction = 2, message = "Win rate must have up to 3 digits and 2 decimal places")
    @Column(name = "win_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal winRate = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "K/D cannot be negative")
    @Digits(integer = 3, fraction = 2, message = "K/D must have up to 3 digits and 2 decimal places")
    @Column(name = "kd", nullable = false, precision = 5, scale = 2)
    private BigDecimal kd = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "K/R cannot be negative")
    @Digits(integer = 3, fraction = 2, message = "K/R must have up to 3 digits and 2 decimal places")
    @Column(name = "kr", nullable = false, precision = 5, scale = 2)
    private BigDecimal kr = BigDecimal.ZERO;

    // Relationship with Player
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", insertable = false, updatable = false)
    private Player player;
}
