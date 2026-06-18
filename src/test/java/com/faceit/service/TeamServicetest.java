package com.faceit.service;

import com.faceit.dto.TeamRequest;
import com.faceit.dto.TeamResponse;
import com.faceit.entity.Team;
import com.faceit.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServicetest {
    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    private Team testTeam;
    private TeamRequest teamRequest;

    @BeforeEach
    void setUp() {
        testTeam = new Team();
        testTeam.setTeamId(1);
        testTeam.setName("Test Team");

        teamRequest = new TeamRequest("Test team");
    }

    @Test
    void createTeam_ShouldCreateTeam_WhenValidRequest() {
        when(teamRepository.existsByName("Test team")).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        TeamResponse response = teamService.createTeam(teamRequest);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Test Team");
        assertThat(response.teamId()).isEqualTo(1);

        verify(teamRepository).existsByName("Test Team");
        verify(teamRepository).save(any(Team.class));
    }
}
