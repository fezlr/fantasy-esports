package com.fezlr.fantasy.cs2.pack.dto.response;

import lombok.Builder;
import java.time.Instant;

@Builder
public record PackPurchaseCS2Response(

        Long packId,

        PackPurchaseShopCS2Response packShopResponse,

        long userFantasyPoints,

        Instant receivedAt

) {
}
