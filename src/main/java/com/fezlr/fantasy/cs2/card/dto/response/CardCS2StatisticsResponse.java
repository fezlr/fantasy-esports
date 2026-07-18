package com.fezlr.fantasy.cs2.card.dto.response;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record CardCS2StatisticsResponse(
        Integer seriesCount,

        Integer kills,

        Integer deaths,

        Integer assists,

        Integer headshots,

        BigDecimal rating,

        Instant lastSyncedAt
) {
}
