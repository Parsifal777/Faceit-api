package com.faceit.repository;

import com.faceit.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

    Optional<Player> findByNicknameIgnoreCase(String nickname);

    List<Player> findByTeamId(Integer teamId);

    List<Player> findByTeamIdIsNull();

    @Query("SELECT p FROM Player p JOIN PlayerStats ps ON p.playerId = ps.playerId WHERE ps.kd > :minKd")
    List<Player> findPlayersWithKdGreaterThan(@Param("minKd") double minKd);

    @Query("SELECT p FROM Player p " +
            "LEFT JOIN FETCH p.playerStatistics " +
            "LEFT JOIN FETCH p.playerStats " +
            "WHERE p.playerId = :playerId")
    Optional<Player> findByIdWithAllStats(@Param("playerId") Integer playerId);

    @Modifying
    @Transactional
    @Query("UPDATE Player p SET p.teamId = :newTeamId WHERE p.teamId = :oldTeamId")
    int movePlayersToTeam(@Param("oldTeamId") Integer oldTeamId,
                          @Param("newTeamId") Integer newTeamId);
}
