package com.fezlr.fantasy.cs2.rating.service;

import com.fezlr.fantasy.grid.dto.stats.PlayerAggregateStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PlayerCS2RatingServiceTest {

    @InjectMocks
    private PlayerCS2RatingService playerCS2RatingService;

    @Test
    void shouldReturnZero_whenSeriesCountIsZero() {
        PlayerAggregateStats stats = new PlayerAggregateStats(
                123L,
                0,
                0,
                0,
                0,
                0
        );

        BigDecimal result = playerCS2RatingService.calculate(stats);

        assertThat(result).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldCalculateRating_whenStatsAreValid() {
        PlayerAggregateStats stats = new PlayerAggregateStats(
                123L,
                10,
                100,
                50,
                20,
                60
        );

        BigDecimal result = playerCS2RatingService.calculate(stats);

        assertThat(result).isEqualByComparingTo("11.10");
    }

    @Test
    void shouldReturnZero_whenDeathsExceedImpact() {
        PlayerAggregateStats stats = new PlayerAggregateStats(
                123L,
                10,
                20,
                100,
                1000,
                5
        );

        BigDecimal result = playerCS2RatingService.calculate(stats);

        assertThat(result).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldRoundRatingToTwoDecimalPlaces_whenResultHasFraction() {
        PlayerAggregateStats stats = new PlayerAggregateStats(
                123L,
                3,
                10,
                1,
                5,
                0
        );

        BigDecimal result = playerCS2RatingService.calculate(stats);

        assertThat(result).isEqualByComparingTo("1.83");
    }
}