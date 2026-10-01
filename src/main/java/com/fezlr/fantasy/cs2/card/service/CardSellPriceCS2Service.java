package com.fezlr.fantasy.cs2.card.service;

import com.fezlr.fantasy.cs2.card.enums.Rarity;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2StatisticEntity;
import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;

import java.math.BigDecimal;
import java.util.Optional;

public class CardSellPriceCS2Service {
    private static final BigDecimal BASIC_CARD_PRICE = BigDecimal.valueOf(100);
    private static final BigDecimal MIN_CARD_RATING = BigDecimal.valueOf(-10);
    private static final BigDecimal MAX_CARD_RATING = BigDecimal.valueOf(20);
    private static final BigDecimal GROWTH_RATING = BigDecimal.valueOf(0.15);

    private static final Integer BASIC_NULL_TEAM_RANK = 100;

    private static final BigDecimal TOP_FROM_1_TO_5_MULTIPLIER = BigDecimal.valueOf(1.5);
    private static final BigDecimal TOP_FROM_5_TO_15_MULTIPLIER = BigDecimal.valueOf(1.35);
    private static final BigDecimal TOP_FROM_15_TO_25_MULTIPLIER = BigDecimal.valueOf(1.20);
    private static final BigDecimal TOP_FROM_25_TO_50_MULTIPLIER = BigDecimal.valueOf(1.10);
    private static final BigDecimal TOP_BASIC_MULTIPLIER = BigDecimal.ONE;

    public long calculateSellPrice(PlayerCS2Entity player, Rarity rarity) {
        BigDecimal rating = compareRating(getRating(player));
        Integer teamRank = getTeamRank(player);

        double curve = Math.exp(GROWTH_RATING.multiply(rating).doubleValue());
        BigDecimal ratingPrice = BASIC_CARD_PRICE.multiply(BigDecimal.valueOf(curve));

        return ratingPrice
                .multiply(rarity.getMultiplier())
                .multiply(getMultiplierForTeamsFromTop(teamRank))
                .longValue();
    }

    private BigDecimal getRating(PlayerCS2Entity player) {
        return Optional.ofNullable(player.getPlayerStatistics())
                .map(PlayerCS2StatisticEntity::getRating)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal compareRating(BigDecimal rating) {
        return MIN_CARD_RATING.max(rating.min(MAX_CARD_RATING));
    }

    private Integer getTeamRank(PlayerCS2Entity player) {
        return Optional.ofNullable(player.getTeam())
                .map(TeamCS2Entity::getValveRating)
                .orElse(BASIC_NULL_TEAM_RANK);
    }

    private BigDecimal getMultiplierForTeamsFromTop(Integer teamRank) {
        if (teamRank <= 5) {
            return TOP_FROM_1_TO_5_MULTIPLIER;
        } else if (teamRank <= 15) {
            return TOP_FROM_5_TO_15_MULTIPLIER;
        } else if (teamRank <= 25) {
            return TOP_FROM_15_TO_25_MULTIPLIER;
        } else if (teamRank <= 50) {
            return TOP_FROM_25_TO_50_MULTIPLIER;
        } else {
            return TOP_BASIC_MULTIPLIER;
        }
    }
}