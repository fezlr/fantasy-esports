package com.fezlr.fantasy.cs2.team.mapper;

import com.fezlr.fantasy.cs2.player.mapper.PlayerCS2Mapper;
import com.fezlr.fantasy.cs2.team.dto.response.TeamCS2Response;
import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TeamCS2Mapper {
    private final PlayerCS2Mapper playerCS2Mapper;

    public TeamCS2Response toResponse(TeamCS2Entity entity) {
        return TeamCS2Response.builder()
                .name(entity.getName())
                .valveRank(entity.getValveRank())
                .logoUrl(entity.getLogoUrl())
                .players(entity.getPlayers().stream().map(playerCS2Mapper::toResponse).toList())
                .build();
    }
}
