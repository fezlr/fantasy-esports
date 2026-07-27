package com.fezlr.fantasy.pandascore.mapper;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import com.fezlr.fantasy.cs2.team.service.TeamCS2Service;
import com.fezlr.fantasy.pandascore.dto.request.PandaScorePlayerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.Instant;

@RequiredArgsConstructor
@Component
public class PandaScorePlayerCS2Mapper {
    private final TeamCS2Service teamCS2Service;

    public PlayerCS2Entity toEntity(PandaScorePlayerRequest dto) {
        TeamCS2Entity team = teamCS2Service.findByPandaScoreIdOrNull(dto.teamId());

        return PlayerCS2Entity.builder()
                .nickname(dto.nickname())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .team(team)
                .pandaScoreId(dto.id())
                .lastSyncedAt(Instant.now())
                .isActive(dto.isActive())
                .build();
    }
}
