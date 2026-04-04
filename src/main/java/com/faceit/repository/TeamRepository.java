package com.faceit.repository;

import com.faceit.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

    Optional<Team> findByNameIgnoreCase(String name);

    List<Team> findByNameContainingIgnoreCase(String namePart);

    boolean existsByName(String name);

    @Query("SELECT t FROM Team t WHERE SIZE(t.players) > :minPlayers")
    List<Team> findTeamsWithMinPlayers(@Param("minPlayers") int minPlayers);

    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.players WHERE t.teamId = :teamId")
    Optional<Team> findByIdWithPlayers(@Param("teamId") Integer teamId);
}
