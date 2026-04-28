package com.faceit.repository;

import com.faceit.entity.Player;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

    Optional<Player> findByNicknameIgnoreCase(String nickname);

    List<Player> findByTeamId(Integer teamId);

    boolean existsByNicknameIgnoreCase(String nickname);

    int countByTeamId(Integer teamId);

    @Query("SELECT p FROM Player p LEFT JOIN FETCH p.team WHERE p.playerId = :playerId")
    Optional<Player> findByIdWithTeam(@Param("playerId") Integer playerId);

    @Query("SELECT p FROM Player p LEFT JOIN FETCH p.team LEFT JOIN FETCH p.playerStatistics WHERE p.teamId = :teamId")
    List<Player> findByTeamIdWithAllData(@Param("teamId") Integer teamId);

    @Query("SELECT p FROM Player p LEFT JOIN FETCH p.playerStatistics WHERE p.playerId = :playerId")
    Optional<Player> findByIdWithStatistics(@Param("playerId") Integer playerId);

    @Query("SELECT p FROM Player p LEFT JOIN FETCH p.team LEFT JOIN FETCH p.playerStatistics WHERE p.playerId = :playerId")
    Optional<Player> findByIdWithAllData(@Param("playerId") Integer playerId);

    @Query("SELECT p.teamId FROM Player p WHERE p.playerId = :playerId")
    Integer findTeamIdByPlayerId(@Param("playerId") Integer playerId);

    @EntityGraph(attributePaths = {"team", "playerStatistics"})
    Optional<Player> findByPlayerId(Integer playerId);

    @EntityGraph(attributePaths = {"team"})
    List<Player> findAllByOrderByNicknameAsc();
}
