package com.faceit.repository;

import com.faceit.entity.TeamStats;
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
public interface TeamStatsRepository extends JpaRepository<TeamStats, Integer> {

    Optional<TeamStats> findByTeamId(Integer teamId);

    @Modifying
    @Transactional
    @Query("UPDATE TeamStats ts SET ts.winRate = :winRate WHERE ts.teamId = :teamId")
    int updateWinRate(@Param("teamId") Integer teamId, @Param("winRate") BigDecimal winRate);

    List<TeamStats> findAllByOrderByWinRateDesc();
}
