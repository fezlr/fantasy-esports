package com.fezlr.fantasy.cs2.card.dto.response;

import com.fezlr.fantasy.cs2.player.dto.response.InventoryPlayerCS2Response;
import lombok.Builder;

@Builder
public record InventoryCardCS2Response(

        String rarity,

        InventoryPlayerCS2Response player

) {
}
