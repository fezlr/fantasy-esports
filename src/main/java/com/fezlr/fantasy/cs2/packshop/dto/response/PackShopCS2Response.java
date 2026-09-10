package com.fezlr.fantasy.cs2.packshop.dto.response;

import com.fezlr.fantasy.cs2.card.enums.Rarity;
import com.fezlr.fantasy.cs2.pack.enums.PackType;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.Map;

@Builder
public record PackShopCS2Response(
        Long id,

        PackType packType,

        String name,

        long cost,

        Map<Rarity, BigDecimal> rarityChances
) {
}
