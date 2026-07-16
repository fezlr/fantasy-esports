package com.fezlr.fantasy.pandascore.mapper;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import com.fezlr.fantasy.cs2.team.service.TeamCS2Service;
import com.fezlr.fantasy.pandascore.dto.PandaScorePlayerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@RequiredArgsConstructor
@Component
public class PandaScorePlayerCS2Mapper {
    private final TeamCS2Service teamCS2Service;

    public PlayerCS2Entity toEntity(PandaScorePlayerDto dto) {
        TeamCS2Entity team = dto.teamId() != null ? teamCS2Service.getBySourceId(dto.teamId()) : null;

        return PlayerCS2Entity.builder()
                .nickname(dto.nickname())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .team(team)
                .sourceId(dto.sourceId())
                .lastSyncedAt(Instant.now())
                .isActive(dto.isActive())
                .build();
    }
}
