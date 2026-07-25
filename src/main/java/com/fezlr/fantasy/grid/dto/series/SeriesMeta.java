package com.fezlr.fantasy.grid.dto.series;

import java.time.Instant;

public record SeriesMeta(
        Instant startedAt,
        String tournamentName
) {
}
