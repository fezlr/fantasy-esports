package com.fezlr.fantasy.cs2.card.mapper;

import com.fezlr.fantasy.cs2.card.dto.response.InventoryCardCS2Response;
import com.fezlr.fantasy.cs2.card.dto.response.SellCardCS2Response;
import com.fezlr.fantasy.cs2.card.entity.CardCS2Entity;
import com.fezlr.fantasy.cs2.player.dto.response.InventoryPlayerCS2Response;
import com.fezlr.fantasy.cs2.player.dto.response.PlayerCS2StatisticResponse;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2StatisticEntity;
import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import org.springframework.stereotype.Component;

@Component
public class CardCS2Mapper {

    public InventoryCardCS2Response toInventoryResponse(CardCS2Entity entity) {
        return InventoryCardCS2Response.builder()
                .rarity(entity.getRarity().name())
                .player(toInventoryPlayerResponse(entity.getPlayerCS2()))
                .build();
    }

    public SellCardCS2Response toSellCardResponse(CardCS2Entity entity) {
        return SellCardCS2Response.builder()
                .sellPrice(entity.getSellPrice())
                .build();
    }

    private InventoryPlayerCS2Response toInventoryPlayerResponse(PlayerCS2Entity entity) {
        TeamCS2Entity team = entity.getTeam();

        return InventoryPlayerCS2Response.builder()
                .nickname(entity.getNickname())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .imageUrl(entity.getImageUrl())
                .teamId(team != null ? team.getId() : null)
                .teamName(team != null ? team.getName() : null)
                .teamLogoUrl(team != null ? team.getLogoUrl() : null)
                .isActive(entity.isActive())
                .playerStatistic(entity.getPlayerStatistics() != null ? toPlayerStatisticResponse(entity.getPlayerStatistics()) : null)
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
