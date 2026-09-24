package com.fezlr.fantasy.cs2.pack.service;

import com.fezlr.fantasy.cs2.card.enums.Rarity;
import com.fezlr.fantasy.cs2.packshop.entity.PackDropCS2ChanceEntity;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PackRarityRollerServiceTest {

    @Test
    void rollReturnsFirstRarity_whenRollIsBelowFirstThreshold() {
        PackRarityRollerService roller = new PackRarityRollerService(() -> 10.0);

        Rarity result = roller.roll(dropChances(
                chance(Rarity.COMMON, "65.00"),
                chance(Rarity.RARE, "35.00")
        ));

        assertEquals(Rarity.COMMON, result);
    }

    @Test
    void rollReturnsSecondRarity_whenRollIsAboveFirstThreshold() {
        PackRarityRollerService roller = new PackRarityRollerService(() -> 70.0);

        Rarity result = roller.roll(dropChances(
                chance(Rarity.COMMON, "65.00"),
                chance(Rarity.RARE, "35.00")
        ));

        assertEquals(Rarity.RARE, result);
    }

    @Test
    void rollReturnsSecondRarity_whenRollIsExactlyOnBoundary() {
        PackRarityRollerService roller = new PackRarityRollerService(() -> 65.0);

        Rarity result = roller.roll(dropChances(
                chance(Rarity.COMMON, "65.00"),
                chance(Rarity.RARE, "35.00")
        ));

        assertEquals(Rarity.RARE, result);
    }

    @Test
    void rollReturnsLastRarity_whenRollIsNearMaximum() {
        PackRarityRollerService roller = new PackRarityRollerService(() -> 99.99);

        Rarity result = roller.roll(dropChances(
                chance(Rarity.EPIC, "55.00"),
                chance(Rarity.MYTHICAL, "35.00"),
                chance(Rarity.LEGENDARY, "10.00")
        ));

        assertEquals(Rarity.LEGENDARY, result);
    }

    @Test
    void rollReturnsMiddleRarity_whenThereIsAMultiTierPack() {
        PackRarityRollerService roller = new PackRarityRollerService(() -> 60.0);

        Rarity result = roller.roll(dropChances(
                chance(Rarity.EPIC, "55.00"),
                chance(Rarity.MYTHICAL, "35.00"),
                chance(Rarity.LEGENDARY, "10.00")
        ));

        assertEquals(Rarity.MYTHICAL, result);
    }

    @Test
    void rollReturnsOnlyRarity_whenSingleChanceCoversFullRange() {
        PackRarityRollerService roller = new PackRarityRollerService(() -> 50.0);

        Rarity result = roller.roll(dropChances(
                chance(Rarity.LEGENDARY, "100.00")
        ));

        assertEquals(Rarity.LEGENDARY, result);
    }

    @Test
    void rollFallsBackToLastRarity_whenChancesSumBelow100DueToRounding() {
        PackRarityRollerService roller = new PackRarityRollerService(() -> 99.995);

        Rarity result = roller.roll(dropChances(
                chance(Rarity.COMMON, "49.99"),
                chance(Rarity.RARE, "49.99")
        ));

        assertEquals(Rarity.RARE, result);
    }

    @Test
    void rollReturnsFirstRarity_whenRollIsZero() {
        PackRarityRollerService roller = new PackRarityRollerService(() -> 0.0);

        Rarity result = roller.roll(dropChances(
                chance(Rarity.COMMON, "65.00"),
                chance(Rarity.RARE, "35.00")
        ));

        assertEquals(Rarity.COMMON, result);
    }

    private List<PackDropCS2ChanceEntity> dropChances(PackDropCS2ChanceEntity... chances) {
        return List.of(chances);
    }

    private PackDropCS2ChanceEntity chance(Rarity rarity, String chanceValue) {
        PackDropCS2ChanceEntity entity = new PackDropCS2ChanceEntity();
        entity.setRarity(rarity);
        entity.setChance(new BigDecimal(chanceValue));
        return entity;
    }
}
