package com.fezlr.fantasy.cs2.pack.dto.response;

import lombok.Builder;

@Builder
public record InventoryPackCS2Response(
        String name,

        Integer count,

        String icon
) {
}
