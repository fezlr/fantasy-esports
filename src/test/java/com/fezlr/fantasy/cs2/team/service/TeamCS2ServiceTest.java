package com.fezlr.fantasy.cs2.team.service;

import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import com.fezlr.fantasy.cs2.team.repository.TeamCS2Repository;
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
public class TeamCS2ServiceTest {
    @Mock
    private TeamCS2Repository teamCS2Repository;

    @InjectMocks
    private TeamCS2Service teamCS2Service;

    @Test
    void shouldCreateNewTeam_whenSourceIdNotFound() {
        TeamCS2Entity newTeam = TeamCS2Entity.builder()
                .sourceId(123L)
                .name("TEAMTEST")
                .build();
        when(teamCS2Repository.findBySourceId(123L)).thenReturn(Optional.empty());
        when(teamCS2Repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TeamCS2Entity result = teamCS2Service.createOrUpdate(newTeam);

        assertThat(result.getName()).isEqualTo("TEAMTEST");
        verify(teamCS2Repository).save(newTeam);
    }

    @Test
    void shouldUpdateExistingTeam_whenSourceIdFound() {
        TeamCS2Entity existing = TeamCS2Entity.builder()
                .id(1L)
                .sourceId(123L)
                .name("Old Name")
                .build();

        TeamCS2Entity updatedInfo = TeamCS2Entity.builder()
                .sourceId(123L)
                .name("New Name")
                .build();

        when(teamCS2Repository.findBySourceId(123L)).thenReturn(Optional.of(existing));
        when(teamCS2Repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TeamCS2Entity result = teamCS2Service.createOrUpdate(updatedInfo);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getId()).isEqualTo(1L);
    }
}