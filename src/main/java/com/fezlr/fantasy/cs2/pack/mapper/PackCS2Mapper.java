package com.fezlr.fantasy.cs2.pack.mapper;

import com.fezlr.fantasy.cs2.card.enums.Rarity;
import com.fezlr.fantasy.cs2.pack.dto.response.InventoryPackCardCS2Response;
import com.fezlr.fantasy.cs2.pack.dto.response.PackCS2Response;
import com.fezlr.fantasy.cs2.pack.dto.response.PackPurchaseCS2Response;
import com.fezlr.fantasy.cs2.pack.dto.response.PackPurchaseShopCS2Response;
import com.fezlr.fantasy.cs2.pack.entity.PackCS2Entity;
import com.fezlr.fantasy.cs2.packshop.entity.PackDropCS2ChanceEntity;
import com.fezlr.fantasy.cs2.packshop.entity.PackShopCS2Entity;
import com.fezlr.fantasy.cs2.player.dto.response.InventoryPlayerCS2Response;
import com.fezlr.fantasy.cs2.player.dto.response.PlayerCS2StatisticResponse;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2StatisticEntity;
import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
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
                                PackDropCS2ChanceEntity::getChance,
                                (existingValue, newValue) -> existingValue)))
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

    public InventoryPackCardCS2Response toOpenResultResponse(Rarity rarity, PlayerCS2Entity player) {
        TeamCS2Entity team = player.getTeam();
        PlayerCS2StatisticEntity statistics = player.getPlayerStatistics();

        return InventoryPackCardCS2Response.builder()
                .rarity(rarity)
                .player(InventoryPlayerCS2Response.builder()
                        .nickname(player.getNickname())
                        .firstName(player.getFirstName())
                        .lastName(player.getLastName())
                        .imageUrl(player.getImageUrl())
                        .teamId(team != null ? team.getId() : null)
                        .teamName(team != null ? team.getName() : null)
                        .teamLogoUrl(team != null ? team.getLogoUrl() : null)
                        .isActive(player.isActive())
                        .playerStatistic(statistics != null ? toPlayerStatisticResponse(statistics) : null)
                        .build())
                .build();
    }

    private PlayerCS2StatisticResponse toPlayerStatisticResponse(PlayerCS2StatisticEntity entity) {
        return PlayerCS2StatisticResponse.builder()
                .kills(entity.getKills())
                .deaths(entity.getDeaths())
                .assists(entity.getAssists())
                .headshots(entity.getHeadshots())
                .rating(entity.getRating())
                .build();
    }
}
