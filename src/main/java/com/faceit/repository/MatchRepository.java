package com.faceit.repository;

import com.faceit.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {

    List<Match> findByTeam1IdOrTeam2Id(Integer team1Id, Integer team2Id);

    @Query("SELECT m FROM Match m WHERE m.team1Id = :teamId OR m.team2Id = :teamId ORDER BY m.matchDate DESC")
    List<Match> findMatchesByTeam(@Param("teamId") Integer teamId);
}