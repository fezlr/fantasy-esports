package com.fezlr.fantasy.grid.dto.player_search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlayerSearchDto(
        Long id,
        String nickname,
        TeamRefDto team
) {
}