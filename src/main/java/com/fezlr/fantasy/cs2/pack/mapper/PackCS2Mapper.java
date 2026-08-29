package com.fezlr.fantasy.cs2.pack.mapper;

import com.fezlr.fantasy.cs2.pack.dto.response.PackCS2Response;
import com.fezlr.fantasy.cs2.pack.entity.PackCS2Entity;
import com.fezlr.fantasy.cs2.packshop.entity.PackDropCS2ChanceEntity;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PackCS2Mapper {
    public PackCS2Response toResponse(PackCS2Entity entity) {
        List<PackDropCS2ChanceEntity> packDrop = entity.getPackShopCS2().getDropChances();

        return PackCS2Response.builder()
                .name(entity.getPackType().name())
                .price(entity.getPackShopCS2().getCost())
                .rarityChances(packDrop.stream().collect(Collectors.toMap(
                                PackDropCS2ChanceEntity::getRarity,
                                PackDropCS2ChanceEntity::getChance)))
                .build();
    }
}
