package com.fezlr.fantasy.pandascore.mapper;

import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import com.fezlr.fantasy.pandascore.dto.PandaScoreTeamDto;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PandaScoreTeamCS2Mapper {
    public TeamCS2Entity toEntity(PandaScoreTeamDto dto) {
        return TeamCS2Entity.builder()
                .name(dto.name())
                .pandaScoreId(dto.pandaScoreId())
                .logoUrl(dto.logoUrl())
                .lastSyncedAt(Instant.now())
                .build();
    }
}
