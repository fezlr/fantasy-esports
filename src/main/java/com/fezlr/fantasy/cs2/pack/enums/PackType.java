package com.fezlr.fantasy.cs2.pack.enums;

import com.fezlr.fantasy.common.enums.Rarity;
import lombok.Getter;
import java.util.Map;

@Getter
public enum PackType {

    COMMON(100,
            Map.of(
                    Rarity.COMMON, 0.600,
                    Rarity.RARE, 0.250,
                    Rarity.EPIC, 0.080,
                    Rarity.MYTHICAL, 0.050,
                    Rarity.LEGENDARY, 0.020
            )
    ),

    EPIC(250,
          Map.of(
                  Rarity.COMMON, 0.550,
                  Rarity.RARE, 0.200,
                  Rarity.EPIC, 0.180,
                  Rarity.MYTHICAL, 0.050,
                  Rarity.LEGENDARY, 0.020
          )
    ),

    LEGENDARY(600,
            Map.of(
                    Rarity.COMMON, 0.550,
                    Rarity.RARE, 0.200,
                    Rarity.EPIC, 0.120,
                    Rarity.MYTHICAL, 0.080,
                    Rarity.LEGENDARY, 0.050
            )
    );

    private int price;
    private Map<Rarity, Double> rarityChances;

    PackType(int price, Map<Rarity, Double> rarityChances) {
        this.price = price;
        this.rarityChances = rarityChances;
    }

}
