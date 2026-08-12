package com.fezlr.fantasy.cs2.pack.mapper;

import com.fezlr.fantasy.cs2.pack.dto.response.PackCS2Response;
import com.fezlr.fantasy.cs2.pack.entity.PackCS2Entity;
import org.springframework.stereotype.Component;

@Component
public class PackCS2Mapper {
    public PackCS2Response toResponse(PackCS2Entity entity) {
        return PackCS2Response.builder()
                .name(entity.getPackType().name())
                .price(entity.getPackType().getPrice())
                .rarityChances(entity.getPackType().getRarityChances())
                .build();
    }
}
