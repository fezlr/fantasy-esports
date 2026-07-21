package com.fezlr.fantasy.grid.dto.stats;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record GridPlayerStats(
        Integer count,

        Integer seriesCount,

        Integer kills,

        Integer deaths,

        Integer assists,

        Integer headshots,

        BigDecimal rating,

        Instant lastSyncedAt
) {
}
