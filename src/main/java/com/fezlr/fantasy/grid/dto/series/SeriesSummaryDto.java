package com.fezlr.fantasy.grid.dto.series;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SeriesSummaryDto(
        Long id,
        Instant startTimeScheduled
) {
}