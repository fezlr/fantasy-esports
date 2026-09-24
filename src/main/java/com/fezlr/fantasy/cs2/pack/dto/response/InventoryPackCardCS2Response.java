package com.fezlr.fantasy.cs2.pack.dto.response;

import com.fezlr.fantasy.cs2.card.enums.Rarity;
import com.fezlr.fantasy.cs2.player.dto.response.InventoryPlayerCS2Response;
import lombok.Builder;

@Builder
public record InventoryPackCardCS2Response(

        Rarity rarity,

        InventoryPlayerCS2Response player

) {
}
