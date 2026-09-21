package com.faceit.service;

import com.faceit.dto.PlayerRequest;
import com.faceit.dto.PlayerResponse;
import com.faceit.entity.Player;
import com.faceit.entity.Team;
import com.faceit.repository.PlayerRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private PlayerService playerService;

    private Team testTeam;
    private Player testPlayer;
    private PlayerRequest testRequest;

    @BeforeEach
    void setUp() {
        testTeam = new Team();
        testTeam.setTeamId(1);
        testTeam.setName("Test Team");

        testPlayer = new Player();
        testPlayer.setPlayerId(1);
        testPlayer.setNickname("TestPlayer");
        testPlayer.setTeamId(1);

        testRequest = new PlayerRequest("TestPlayer", 1);
    }

    @Test
    void createPlayer_ShouldCreatePlayer_WhenValidRequest() {
        // given
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));
        when(playerRepository.existsByNicknameIgnoreCase("TestPlayer")).thenReturn(false);
        when(playerRepository.countByTeamId(1)).thenReturn(3);
        when(playerRepository.save(any(Player.class))).thenReturn(testPlayer);

        // when
        PlayerResponse response = playerService.createPlayer(testRequest);
        response = null;
        // then
        assertThat(response).isNotNull();
        assertThat(response.getNickname()).isEqualTo("TestPlayer");
        assertThat(response.getTeamId()).isEqualTo(1);
        verify(teamRepository).findById(1);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void createPlayer_ShouldThrowException_WhenTeamNotFound() {
        // given
        when(teamRepository.findById(1)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> playerService.createPlayer(testRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Team not found with id: 1");
    }

    @Test
    void createPlayer_ShouldThrowException_WhenNicknameExists() {
        // given
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));
        when(playerRepository.existsByNicknameIgnoreCase("TestPlayer")).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> playerService.createPlayer(testRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Nickname already exists: TestPlayer");
    }

    @Test
    void createPlayer_ShouldThrowException_WhenTeamIsFull() {
        // given
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));
        when(playerRepository.existsByNicknameIgnoreCase("TestPlayer")).thenReturn(false);
        when(playerRepository.countByTeamId(1)).thenReturn(5);

        // when / then
        assertThatThrownBy(() -> playerService.createPlayer(testRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already has 5 players (max 5)");
    }

    @Test
    void getAllPlayers_ShouldReturnListOfPlayers() {
        // given
        when(playerRepository.findAllByOrderByNicknameAsc()).thenReturn(List.of(testPlayer));

        // when
        List<PlayerResponse> players = playerService.getAllPlayers();

        // then
        assertThat(players).hasSize(1);
        assertThat(players.get(0).getNickname()).isEqualTo("TestPlayer");
    }

    @Test
    void getPlayerById_ShouldReturnPlayer_WhenExists() {
        // given
        when(playerRepository.findByIdWithAllData(1)).thenReturn(Optional.of(testPlayer));

        // when
        PlayerResponse response = playerService.getPlayerById(1);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPlayerId()).isEqualTo(1);
        assertThat(response.getNickname()).isEqualTo("TestPlayer");
    }

    @Test
    void getPlayerById_ShouldThrowException_WhenNotFound() {
        // given
        when(playerRepository.findByIdWithAllData(1)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> playerService.getPlayerById(1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Player not found with id: 1");
    }

    @Test
    void deletePlayer_ShouldDeletePlayer_WhenExists() {
        // given
        when(playerRepository.existsById(1)).thenReturn(true);
        doNothing().when(playerRepository).deleteById(1);

        // when
        playerService.deletePlayer(1);

        // then
        verify(playerRepository).deleteById(1);
    }

    @Test
    void deletePlayer_ShouldThrowException_WhenNotFound() {
        // given
        when(playerRepository.existsById(1)).thenReturn(false);

        // when / then
        assertThatThrownBy(() -> playerService.deletePlayer(1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Player not found with id: 1");
    }

    @Test
    void updatePlayerTeam_ShouldMovePlayer_WhenValid() {
        // given
        Player existingPlayer = new Player();
        existingPlayer.setPlayerId(1);
        existingPlayer.setTeamId(1);

        Team newTeam = new Team();
        newTeam.setTeamId(2);
        newTeam.setName("New Team");

        when(playerRepository.findByIdWithTeam(1)).thenReturn(Optional.of(existingPlayer));
        when(teamRepository.findById(2)).thenReturn(Optional.of(newTeam));
        when(playerRepository.countByTeamId(2)).thenReturn(2);
        when(playerRepository.save(any(Player.class))).thenReturn(existingPlayer);

        // when
        PlayerResponse response = playerService.updatePlayerTeam(1, 2);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTeamId()).isEqualTo(2);
        verify(playerRepository).save(any(Player.class));
    }
}
