package com.fezlr.fantasy.cs2.card.dto.response;

import lombok.Builder;

@Builder
public record CardCS2SummaryResponse(
        String nickname,

        String firstName,

        String lastName,

        String imageUrl,

        Long teamId,

        boolean isActive,

        CardCS2StatisticsResponse playerStatistics
) {
}
