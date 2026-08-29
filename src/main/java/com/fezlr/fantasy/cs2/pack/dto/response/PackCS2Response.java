package com.fezlr.fantasy.cs2.pack.dto.response;

import com.fezlr.fantasy.cs2.card.enums.Rarity;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record PackCS2Response(
        String name,

        Integer price,

        Map<Rarity, BigDecimal> rarityChances
) {
}
