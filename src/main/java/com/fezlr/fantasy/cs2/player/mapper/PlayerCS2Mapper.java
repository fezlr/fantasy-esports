package com.fezlr.fantasy.cs2.player.mapper;

import com.fezlr.fantasy.cs2.player.dto.response.PlayerCS2Response;
import com.fezlr.fantasy.cs2.player.dto.response.PlayerCS2StatisticResponse;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2StatisticEntity;
import org.springframework.stereotype.Component;

@Component
public class PlayerCS2Mapper {
    public PlayerCS2Response toResponse(PlayerCS2Entity entity) {
        return PlayerCS2Response.builder()
                .nickname(entity.getNickname())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .imageUrl(entity.getImageUrl())
                .playerStatistic(toStatisticResponse(entity.getPlayerStatistics()))
                .teamId(entity.getTeam().getId())
                .isActive(entity.isActive())
                .build();
    }

    public PlayerCS2StatisticResponse toStatisticResponse(PlayerCS2StatisticEntity entity) {
       if (entity == null) {
           return null;
       }

        return PlayerCS2StatisticResponse.builder()
                .kills(entity.getKills())
                .deaths(entity.getDeaths())
                .assists(entity.getAssists())
                .headshots(entity.getHeadshots())
                .rating(entity.getRating())
                .build();
    }
}
