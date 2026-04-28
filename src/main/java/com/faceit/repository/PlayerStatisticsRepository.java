package com.faceit.repository;

import com.faceit.entity.PlayerStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerStatisticsRepository extends JpaRepository<PlayerStatistics, Integer> {

    Optional<PlayerStatistics> findByPlayerId(Integer playerId);

    @Query("SELECT ps FROM PlayerStatistics ps JOIN FETCH ps.player WHERE ps.playerId = :playerId")
    Optional<PlayerStatistics> findByIdWithPlayer(@Param("playerId") Integer playerId);

    @Modifying
    @Transactional
    @Query("UPDATE PlayerStatistics ps SET " +
            "ps.kills = ps.kills + :kills, " +
            "ps.assists = ps.assists + :assists, " +
            "ps.deaths = ps.deaths + :deaths, " +
            "ps.matchesPlayed = ps.matchesPlayed + 1, " +
            "ps.matchesWon = ps.matchesWon + :won " +
            "WHERE ps.playerId = :playerId")
    int updateMatchStats(@Param("playerId") Integer playerId,
                         @Param("kills") int kills,
                         @Param("assists") int assists,
                         @Param("deaths") int deaths,
                         @Param("won") int won);

    @Query("SELECT ps FROM PlayerStatistics ps ORDER BY ps.kills DESC")
    List<PlayerStatistics> findTopByKills();
}
