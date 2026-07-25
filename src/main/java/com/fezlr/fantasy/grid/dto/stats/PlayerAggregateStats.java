package com.fezlr.fantasy.grid.dto.stats;

public record PlayerAggregateStats(
        Long gridPlayerId,

        Integer seriesCount,

        Integer kills,

        //assists
        Integer killAssistsGiven,

        Integer deaths,

        Integer headshots
) {
}
