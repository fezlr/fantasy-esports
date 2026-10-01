package com.fezlr.fantasy.cs2.card.service;

import java.math.BigDecimal;

import com.fezlr.fantasy.cs2.card.enums.Rarity;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CardSellPriceCS2ServiceTest {

    private static final int NO_TOP_RANK = 100;

    private final CardSellPriceCS2Service calculator = new CardSellPriceCS2Service();

    private PlayerCS2Entity player(String rating, int teamRank) {
        PlayerCS2Entity player = mock(PlayerCS2Entity.class, RETURNS_DEEP_STUBS);
        when(player.getPlayerStatistics().getRating()).thenReturn(new BigDecimal(rating));
        when(player.getTeam().getValveRating()).thenReturn(teamRank);
        return player;
    }

    private long price(String rating, int teamRank, Rarity rarity) {
        return calculator.calculateSellPrice(player(rating, teamRank), rarity);
    }

    @Test
    @DisplayName("Zero rating gives the base price")
    void shouldReturnBasePrice_whenZeroRating() {
        assertEquals(100L, price("0", NO_TOP_RANK, Rarity.COMMON));
    }

    @Test
    @DisplayName("Zero rating never produces a zero price")
    void shouldReturnPositivePrice_whenZeroRating() {
        assertTrue(price("0", NO_TOP_RANK, Rarity.COMMON) > 0);
    }

    @ParameterizedTest(name = "rating {0} is compared to the lower bound")
    @CsvSource({"-10", "-10.01", "-50", "-1000"})
    @DisplayName("Ratings at or below the minimum all cost the same")
    void shouldReturnTheSamePrice_whenRatingBelowMinimumIsCompare(String rating) {
        assertEquals(22L, price(rating, NO_TOP_RANK, Rarity.COMMON));
    }

    @ParameterizedTest(name = "rating {0} is compared to the higher bound")
    @CsvSource({"20", "20.01", "50", "1000"})
    @DisplayName("Ratings at or upper the maximum all cost the same")
    void shouldReturnTheSamePrice_whenRatingUpperMaximumIsCompare(String rating) {
        assertEquals(2008L, price(rating, NO_TOP_RANK, Rarity.COMMON));
    }

    @ParameterizedTest
    @DisplayName("Price is positive across the whole rating range")
    @ValueSource(strings = {"-15.0", "-5.0", "0.0", "1.0", "15.0", "25.0"})
    void shouldReturnAlwaysPositivePrice_whenRatingIsGiven(String rating) {
        long result = price(rating, NO_TOP_RANK, Rarity.COMMON);

        assertTrue(result > 0, "price must be positive for rating " + rating);
    }

    @ParameterizedTest
    @DisplayName("Price never decreases as rating grows")
    @CsvSource({
            "-15.0, -14.9",
            "-5.0,   0.0",
            "0.0,    0.1",
            "1.0,   10.0",
            "10.0,  25.0"
    })
    void shouldReturnMonotonicPrice_whenRatingIsGiven(String lowerRating, String higherRating) {
        long priceForLower = price(lowerRating, NO_TOP_RANK, Rarity.LEGENDARY);
        long priceForHigher = price(higherRating, NO_TOP_RANK, Rarity.LEGENDARY);

        assertTrue(priceForHigher >= priceForLower);
    }


    @ParameterizedTest(name = "{0} at rating 0 costs {1}")
    @CsvSource({
            "COMMON, 100",
            "RARE, 140",
            "EPIC, 200",
            "MYTHICAL, 280",
            "LEGENDARY, 400"
    })
    @DisplayName("Rarity multiplier is applied")
    void shouldReturnCorrectPrice_whenRarityMultiplierIsApplied(Rarity rarity, long expected) {
        assertEquals(expected, price("0", NO_TOP_RANK, rarity));
    }

    @ParameterizedTest
    @EnumSource(Rarity.class)
    @DisplayName("Every rarity costs at least as much as common")
    void shouldReturnCorrectPrice_whenEveryRarityIsNotCheaperThanCommon(Rarity rarity) {
        assertTrue(price("7", NO_TOP_RANK, rarity) >= price("7", NO_TOP_RANK, Rarity.COMMON));
    }

    @ParameterizedTest
    @DisplayName("Higher rarity costs strictly more")
    @CsvSource({
            "COMMON,    RARE",
            "RARE,      EPIC",
            "EPIC,      LEGENDARY"
    })
    void shouldReturnRarityOrder_whenIsStrict(Rarity lowerRarity, Rarity higherRarity) {
        long priceForLower = price("5", NO_TOP_RANK, lowerRarity);
        long priceForHigher = price("5", NO_TOP_RANK, higherRarity);

        assertTrue(priceForHigher > priceForLower);
    }


    @ParameterizedTest(name = "team rank {0} -> price {1}")
    @CsvSource({
            "1, 150",
            "3, 150",
            "5, 150",
            "6, 135",
            "15, 135",
            "16, 120",
            "25, 120",
            "26, 110",
            "50, 110",
            "51, 100",
            "100, 100",
            "1000, 100"
    })
    @DisplayName("Team rank bucket boundaries")
    void shouldReturnCorrectPrice_whenTeamRankBoundariesAreGiven(int teamRank, long expected) {
        assertEquals(expected, price("0", teamRank, Rarity.COMMON));
    }

    @ParameterizedTest
    @DisplayName("Better team never makes the card cheaper")
    @CsvSource({
            "1,   2",
            "2,   10",
            "10,  50",
            "50,  100",
            "100, 120"
    })
    void shouldReturnCorrectPrice_whenBetterTeamIsNotCheaper(int betterRank, int worseRank) {
        long priceForBetter = price("3", betterRank, Rarity.EPIC);
        long priceForWorse = price("3", worseRank, Rarity.EPIC);

        assertTrue(priceForBetter >= priceForWorse, "Price dropped for better rank: " + betterRank + " vs " + worseRank);
    }

    @Test
    @DisplayName("Team rank is combined with rating and rarity")
    void shouldReturnPrice_whenAllFactorsAreCombined() {
        assertEquals(600L, price("0", 1, Rarity.LEGENDARY));
        assertEquals(12051L, price("20", 1, Rarity.LEGENDARY));
    }

    @Test
    @DisplayName("Minimum price is positive")
    void shouldReturnCorrectPrice_whenMinimumPriceIsPositive() {
        assertTrue(price("-1000", 1000, Rarity.COMMON) > 0);
    }

    @Test
    @DisplayName("Missing team falls back to the default rank")
    void shouldReturnBasicPrice_whenNullTeamUsesDefaultRank() {
        PlayerCS2Entity player = mock(PlayerCS2Entity.class, RETURNS_DEEP_STUBS);
        when(player.getPlayerStatistics().getRating()).thenReturn(BigDecimal.ZERO);
        when(player.getTeam()).thenReturn(null);

        assertEquals(100L, calculator.calculateSellPrice(player, Rarity.COMMON));
    }

    @Test
    @DisplayName("Missing statistics fall back to zero rating")
    void shouldReturnBasicPrice_whenNullStatisticsUsesZeroRating() {
        PlayerCS2Entity player = mock(PlayerCS2Entity.class, RETURNS_DEEP_STUBS);
        when(player.getPlayerStatistics()).thenReturn(null);
        when(player.getTeam().getValveRating()).thenReturn(NO_TOP_RANK);

        assertEquals(100L, calculator.calculateSellPrice(player, Rarity.COMMON));
    }

    @Test
    @DisplayName("Missing rating value falls back to zero rating")
    void shouldReturnBasicPrice_whenNullRatingUsesZeroRating() {
        PlayerCS2Entity player = mock(PlayerCS2Entity.class, RETURNS_DEEP_STUBS);
        when(player.getPlayerStatistics().getRating()).thenReturn(null);
        when(player.getTeam().getValveRating()).thenReturn(NO_TOP_RANK);

        assertEquals(100L, calculator.calculateSellPrice(player, Rarity.COMMON));
    }

    @Test
    @DisplayName("Missing team and statistics together give the base price")
    void shouldReturnCorrectPrice_whenNullTeamAndStatistics() {
        PlayerCS2Entity player = mock(PlayerCS2Entity.class, RETURNS_DEEP_STUBS);
        when(player.getPlayerStatistics()).thenReturn(null);
        when(player.getTeam()).thenReturn(null);

        assertEquals(400L, calculator.calculateSellPrice(player, Rarity.LEGENDARY));
    }
}