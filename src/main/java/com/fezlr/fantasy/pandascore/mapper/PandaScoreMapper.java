package com.fezlr.fantasy.pandascore.mapper;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.team.entity.TeamEntity;
import com.fezlr.fantasy.pandascore.dto.PandaScorePlayerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@RequiredArgsConstructor
@Component
public class PandaScoreMapper {
    private final TeamService teamService;

    public PlayerCS2Entity toEntity(PandaScorePlayerDto dto) {
        TeamEntity team = teamService.getBySourceId(dto.sourceId());

        return PlayerCS2Entity.builder()
                .nickname(dto.nickname())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .team(team)
                .imageUrl(dto.imageUrl())
                .sourceId(dto.sourceId())
                .lastSyncedAt(Instant.now())
                .isActive(dto.isActive())
                .build();
    }
}
