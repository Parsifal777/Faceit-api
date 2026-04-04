package com.faceit.repository;

import com.faceit.entity.TeamStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TeamStatisticsRepository extends JpaRepository<TeamStatistics, Integer> {

    Optional<TeamStatistics> findByTeamId(Integer teamId);

    @Modifying
    @Transactional
    @Query("UPDATE TeamStatistics ts SET " +
            "ts.matchesPlayed = ts.matchesPlayed + 1, " +
            "ts.matchesWon = ts.matchesWon + :won " +
            "WHERE ts.teamId = :teamId")
    int updateMatchStats(@Param("teamId") Integer teamId, @Param("won") int won);
}
