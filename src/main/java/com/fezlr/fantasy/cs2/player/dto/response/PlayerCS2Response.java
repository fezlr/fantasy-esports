package com.fezlr.fantasy.cs2.player.dto.response;

import lombok.Builder;

@Builder
public record PlayerCS2Response(
        String nickname,

        String firstName,

        String lastName,

        String imageUrl,

        Long teamId,

        boolean isActive
) {
}
