package com.fezlr.fantasy.grid.dto.player_search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlayerSearchDto(
        Long id,
        String nickname,
        TeamRefDto team,
        List<ExternalLinkDto> externalLinks
) {

    public Optional<Long> steamId() {
        return externalLinks.stream()
                .filter(link -> "STEAM".equalsIgnoreCase(link.dataProvider().name()))
                .map(ExternalLinkDto::externalEntity)
                .map(ExternalEntityDto::id)
                .findFirst();
    }
}