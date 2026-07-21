package com.fezlr.fantasy.grid.dto.stats;

public record PlayerAggregateStats(
        Long gridPlayerId,
        Long seriesCount,
        Long kills,
        Long deaths
) {
}
