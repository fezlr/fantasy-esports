package com.fezlr.fantasy.grid.dto;

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
