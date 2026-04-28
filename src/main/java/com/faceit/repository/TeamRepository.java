package com.faceit.repository;

import com.faceit.entity.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

    Optional<Team> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.players p LEFT JOIN FETCH p.playerStatistics WHERE t.teamId = :teamId")
    Optional<Team> findByIdWithPlayersAndStats(@Param("teamId") Integer teamId);

    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.players WHERE t.teamId = :teamId")
    Optional<Team> findByIdWithPlayers(@Param("teamId") Integer teamId);

    @EntityGraph(attributePaths = {"players"})
    Optional<Team> findByTeamId(Integer teamId);

    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.players")
    List<Team> findAllWithPlayers();
}
