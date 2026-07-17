package com.fezlr.fantasy.cs2.team.dto.response;

import com.fezlr.fantasy.cs2.player.dto.response.PlayerCS2Response;
import lombok.Builder;

import java.util.List;

@Builder
public record TeamCS2Response(
        String name,

        Integer valveRank,

        String logoUrl,

        List<PlayerCS2Response> players
) {
}
