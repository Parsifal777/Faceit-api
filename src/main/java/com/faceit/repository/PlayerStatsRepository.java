package com.faceit.repository;

import com.faceit.entity.PlayerStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Integer> {

    Optional<PlayerStats> findByPlayerId(Integer playerId);

    @Modifying
    @Transactional
    @Query("UPDATE PlayerStats ps SET ps.kd = :kd WHERE ps.playerId = :playerId")
    int updateKd(@Param("playerId") Integer playerId, @Param("kd") BigDecimal kd);

    List<PlayerStats> findAllByOrderByKdDesc();

    List<PlayerStats> findTop5ByOrderByKdDesc();

    List<PlayerStats> findAllByOrderByWinRateDesc();
}
