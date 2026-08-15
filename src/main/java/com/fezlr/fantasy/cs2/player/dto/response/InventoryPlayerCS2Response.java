package com.fezlr.fantasy.cs2.player.dto.response;

import lombok.Builder;

@Builder
public record InventoryPlayerCS2Response(
        String nickname,

        String firstName,

        String lastName,

        String imageUrl,

        PlayerCS2StatisticResponse playerStatistic,

        Long teamId,

        String teamName,

        String teamLogoUrl,

        boolean isActive
) {
}
