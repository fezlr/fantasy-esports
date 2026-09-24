package com.fezlr.fantasy.cs2.pack.service;

import com.fezlr.fantasy.cs2.card.enums.Rarity;
import com.fezlr.fantasy.cs2.packshop.entity.PackDropCS2ChanceEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;

@Component
public class PackRarityRollerService {
    private final DoubleSupplier randomSource;

    public PackRarityRollerService() {
        this(() -> ThreadLocalRandom.current().nextDouble(0, 100));
    }

    /**
     * Constructor for unit-testing
     */
    PackRarityRollerService(DoubleSupplier randomSource) {
        this.randomSource = randomSource;
    }


    public Rarity roll(List<PackDropCS2ChanceEntity> dropChances) {
        BigDecimal roll = BigDecimal.valueOf(randomSource.getAsDouble());
        BigDecimal accumulator = BigDecimal.ZERO;

        for (PackDropCS2ChanceEntity chance : dropChances) {
            accumulator = accumulator.add(chance.getChance());

            if (roll.compareTo(accumulator) < 0) {
                return chance.getRarity();
            }
        }

        return dropChances.getLast().getRarity();
    }
}
