package com.fezlr.fantasy.cs2.card.dto.response;

import com.fezlr.fantasy.common.enums.Rarity;
import lombok.Builder;
import java.time.Instant;

@Builder
public record CardCS2Response(
        Rarity rarity,

        Instant receivedAt,

        CardCS2SummaryResponse player
) {
}
