package com.fezlr.fantasy.cs2.player.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PlayerCS2StatisticResponse(
        Integer kills,

        Integer deaths,

        Integer assists,

        Integer headshots,

        BigDecimal rating
) {
}
