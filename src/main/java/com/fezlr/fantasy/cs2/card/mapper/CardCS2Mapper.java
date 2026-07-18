package com.fezlr.fantasy.cs2.card.mapper;

import com.fezlr.fantasy.cs2.card.dto.response.CardCS2Response;
import com.fezlr.fantasy.cs2.card.dto.response.CardCS2StatisticsResponse;
import com.fezlr.fantasy.cs2.card.dto.response.CardCS2SummaryResponse;
import com.fezlr.fantasy.cs2.card.entity.CardCS2Entity;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2StatisticEntity;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class CardCS2Mapper {

    public CardCS2Response toResponse(CardCS2Entity entity) {
        PlayerCS2Entity player = entity.getPlayerCS2();
        PlayerCS2StatisticEntity statistics = player.getPlayerStatistics();

        return CardCS2Response.builder()
                .rarity(entity.getRarity())
                .receivedAt(Instant.now())
                .player(CardCS2SummaryResponse.builder()
                        .nickname(player.getNickname())
                        .firstName(player.getFirstName())
                        .lastName(player.getLastName())
                        .imageUrl(player.getImageUrl())
                        .teamId(player.getTeam() != null ? player.getTeam().getId() : null)
                        .isActive(player.isActive())
                        .playerStatistics(statistics != null ? toStatisticsSummary(statistics) : null)
                        .build())
                .build();
    }

    private CardCS2StatisticsResponse toStatisticsSummary(PlayerCS2StatisticEntity statistics) {
        return CardCS2StatisticsResponse.builder()
                .seriesCount(statistics.getSeriesCount())
                .kills(statistics.getKills())
                .deaths(statistics.getDeaths())
                .assists(statistics.getAssists())
                .headshots(statistics.getHeadshots())
                .rating(statistics.getRating())
                .lastSyncedAt(Instant.now())
                .build();
    }
}
