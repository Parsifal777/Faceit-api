package com.faceit.repository;

import com.faceit.entity.PlayerMatchStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerMatchStatsRepository extends JpaRepository<PlayerMatchStats, Integer> {

    List<PlayerMatchStats> findByMatchId(Integer matchId);

    List<PlayerMatchStats> findByPlayerId(Integer playerId);
}