package com.fezlr.fantasy.valve.team.dto;

import lombok.Builder;

@Builder
public record ValveTeamRankingDto(
        Integer valveRating,

        Integer vrsPoints,

        String teamName
) {
}
