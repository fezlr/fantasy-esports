package com.fezlr.fantasy.cs2.packshop.mapper;

import com.fezlr.fantasy.cs2.card.enums.Rarity;
import com.fezlr.fantasy.cs2.packshop.dto.response.PackShopCS2Response;
import com.fezlr.fantasy.cs2.packshop.entity.PackDropCS2ChanceEntity;
import com.fezlr.fantasy.cs2.packshop.entity.PackShopCS2Entity;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PackShopCS2Mapper {

    public PackShopCS2Response toResponse(PackShopCS2Entity entity) {
        return PackShopCS2Response.builder()
                .packType(entity.getPackType())
                .name(entity.getName())
                .cost(entity.getCost())
                .rarityChances(toRarityChances(entity.getDropChances()))
                .build();
    }

    public Map<Rarity, BigDecimal> toRarityChances(List<PackDropCS2ChanceEntity> list) {
        return list.stream().collect(Collectors.toMap(
                PackDropCS2ChanceEntity::getRarity,
                PackDropCS2ChanceEntity::getChance));
    }
}
