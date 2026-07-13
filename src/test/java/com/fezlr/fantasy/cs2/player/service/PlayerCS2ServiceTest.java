package com.fezlr.fantasy.cs2.player.service;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.repository.PlayerCS2Repository;
import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerCS2ServiceTest {
    @Mock
    private PlayerCS2Repository playerCS2Repository;

    @InjectMocks
    private PlayerCS2Service playerCS2Service;

    @Test
    void shouldCreateNewPlayer_whenSourceIdNotFound() {
        PlayerCS2Entity newPlayer = PlayerCS2Entity.builder()
                .sourceId(123L)
                .nickname("N1CKname")
                .firstName("NameTest")
                .lastName("LastNameTest")
                .isActive(true)
                .build();
        when(playerCS2Repository.findBySourceId(123L)).thenReturn(Optional.empty());
        when(playerCS2Repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PlayerCS2Entity result = playerCS2Service.createOrUpdate(newPlayer);

        assertThat(result.getNickname()).isEqualTo("N1CKname");
        assertThat(result.getLastSyncedAt()).isNotNull();
        verify(playerCS2Repository).save(newPlayer);
    }

    @Test
    void shouldUpdateExistingPlayer_whenSourceIdFound() {
        PlayerCS2Entity existing = PlayerCS2Entity.builder()
                .id(1L)
                .sourceId(123L)
                .nickname("old_nick")
                .isActive(true)
                .build();

        PlayerCS2Entity updatedInfo = PlayerCS2Entity.builder()
                .sourceId(123L)
                .nickname("new_nick")
                .isActive(true)
                .build();

        when(playerCS2Repository.findBySourceId(123L)).thenReturn(Optional.of(existing));
        when(playerCS2Repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PlayerCS2Entity result = playerCS2Service.createOrUpdate(updatedInfo);

        assertThat(result.getNickname()).isEqualTo("new_nick");
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldUpdateIsActiveFlag_whenPlayerBecameInactive() {
        PlayerCS2Entity existing = PlayerCS2Entity.builder()
                .id(1L)
                .sourceId(123L)
                .nickname("s1mple")
                .isActive(true)
                .build();

        PlayerCS2Entity updatedInfo = PlayerCS2Entity.builder()
                .sourceId(123L)
                .nickname("s1mple")
                .isActive(false)
                .build();

        when(playerCS2Repository.findBySourceId(123L)).thenReturn(Optional.of(existing));
        when(playerCS2Repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PlayerCS2Entity result = playerCS2Service.createOrUpdate(updatedInfo);

        assertThat(result.isActive()).isFalse();
    }

    @Test
    void shouldAssignTeam_whenUpdatingPlayer() {
        TeamCS2Entity oldTeam = TeamCS2Entity.builder().id(1L).sourceId(10L).build();
        TeamCS2Entity newTeam = TeamCS2Entity.builder().id(2L).sourceId(20L).build();

        PlayerCS2Entity existing = PlayerCS2Entity.builder()
                .id(1L)
                .sourceId(123L)
                .team(oldTeam)
                .isActive(true)
                .build();

        PlayerCS2Entity updatedInfo = PlayerCS2Entity.builder()
                .sourceId(123L)
                .team(newTeam)
                .isActive(true)
                .build();
        when(playerCS2Repository.findBySourceId(123L)).thenReturn(Optional.of(existing));
        when(playerCS2Repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PlayerCS2Entity result = playerCS2Service.createOrUpdate(updatedInfo);

        assertThat(result.getTeam().getSourceId()).isEqualTo(20L);
    }
}