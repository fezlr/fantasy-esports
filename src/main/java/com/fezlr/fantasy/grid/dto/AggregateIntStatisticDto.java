package com.fezlr.fantasy.grid.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AggregateIntStatisticDto(
        Integer sum,

        Integer min,

        Integer max,

        Double avg
) {
}
