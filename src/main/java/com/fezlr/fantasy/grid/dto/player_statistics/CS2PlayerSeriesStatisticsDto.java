package com.fezlr.fantasy.grid.dto.player_statistics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CS2PlayerSeriesStatisticsDto(
        Integer count,

        AggregateIntStatisticDto kills,

        AggregateIntStatisticDto killAssistsGiven,

        AggregateIntStatisticDto deaths,

        AggregateIntStatisticDto headshots
) {
}
