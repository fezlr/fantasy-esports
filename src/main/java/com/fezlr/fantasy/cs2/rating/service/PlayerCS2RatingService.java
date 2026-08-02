package com.fezlr.fantasy.cs2.rating.service;

import com.fezlr.fantasy.grid.dto.stats.PlayerAggregateStats;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PlayerCS2RatingService {
    private static final BigDecimal ASSIST_WEIGHT = BigDecimal.valueOf(0.5);
    private static final BigDecimal HEADSHOT_WEIGHT = BigDecimal.valueOf(0.1);

    public BigDecimal calculate(PlayerAggregateStats stats) {
        if (stats.seriesCount() == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal kills = BigDecimal.valueOf(stats.kills());
        BigDecimal deaths = BigDecimal.valueOf(stats.deaths());
        BigDecimal assists = BigDecimal.valueOf(stats.killAssistsGiven());
        BigDecimal headshots = BigDecimal.valueOf(stats.headshots());

        BigDecimal score = kills
                .subtract(deaths)
                .add(assists.multiply(ASSIST_WEIGHT))
                .add(headshots.multiply(HEADSHOT_WEIGHT))
                .divide(BigDecimal.valueOf(stats.seriesCount()), 2, RoundingMode.HALF_UP);

        return score.max(BigDecimal.ZERO);
    }
}