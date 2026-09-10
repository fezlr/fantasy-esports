package com.fezlr.fantasy.cs2.pack.mapper;

import com.fezlr.fantasy.cs2.pack.dto.response.PackCS2Response;
import com.fezlr.fantasy.cs2.pack.dto.response.PackPurchaseCS2Response;
import com.fezlr.fantasy.cs2.pack.dto.response.PackPurchaseShopCS2Response;
import com.fezlr.fantasy.cs2.pack.entity.PackCS2Entity;
import com.fezlr.fantasy.cs2.packshop.entity.PackDropCS2ChanceEntity;
import com.fezlr.fantasy.cs2.packshop.entity.PackShopCS2Entity;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PackCS2Mapper {
    public PackCS2Response toResponse(PackCS2Entity entity) {
        List<PackDropCS2ChanceEntity> packDrop = entity.getPackShopCS2().getDropChances();

        return PackCS2Response.builder()
                .id(entity.getId())
                .name(entity.getPackShopCS2().getPackType().name())
                .packType(entity.getPackShopCS2().getPackType())
                .cost(entity.getPackShopCS2().getCost())
                .rarityChances(packDrop.stream().collect(Collectors.toMap(
                                PackDropCS2ChanceEntity::getRarity,
                                PackDropCS2ChanceEntity::getChance)))
                .build();
    }

    public PackPurchaseCS2Response toPurchaseResponse(PackCS2Entity entity, long remainFantasyPoints) {
        return PackPurchaseCS2Response.builder()
                .packId(entity.getId())
                .userFantasyPoints(remainFantasyPoints)
                .packShopResponse(toPurchaseShopResponse(entity.getPackShopCS2()))
                .receivedAt(entity.getReceivedAt())
                .build();
    }

    public PackPurchaseShopCS2Response toPurchaseShopResponse(PackShopCS2Entity entity) {
        return PackPurchaseShopCS2Response.builder()
                .packType(entity.getPackType())
                .name(entity.getName())
                .cost(entity.getCost())
                .build();
    }
}
