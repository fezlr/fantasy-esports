package com.fezlr.fantasy.grid.dto.series_state;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TeamGameStateDto(
        List<PlayerGameStateDto> players
) {
}
