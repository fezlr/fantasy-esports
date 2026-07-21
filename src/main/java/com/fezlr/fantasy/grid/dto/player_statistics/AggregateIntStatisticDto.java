package com.fezlr.fantasy.grid.dto.player_statistics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AggregateIntStatisticDto(
        Integer sum,

        Integer min,

        Integer max,

        Double avg
) {
}
