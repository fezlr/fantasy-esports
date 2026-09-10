package com.fezlr.fantasy.cs2.pack.dto.response;

import com.fezlr.fantasy.cs2.pack.enums.PackType;
import lombok.Builder;

@Builder
public record PackPurchaseShopCS2Response(

        PackType packType,

        String name,

        Integer cost
) {
}
