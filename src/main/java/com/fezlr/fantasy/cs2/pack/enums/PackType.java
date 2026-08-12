package com.fezlr.fantasy.cs2.pack.enums;

import com.fezlr.fantasy.common.enums.Rarity;
import lombok.Getter;
import java.util.Map;

@Getter
public enum PackType {

    BASIC(100,
            Map.of(
                    Rarity.COMMON, 0.600,
                    Rarity.RARE, 0.250,
                    Rarity.EPIC, 0.020,
                    Rarity.MYTHICAL, 0.005,
                    Rarity.LEGENDARY, 0.001
            )
    );

    private int price;
    private Map<Rarity, Double> rarityChances;

    PackType(int price, Map<Rarity, Double> rarityChances) {
        this.price = price;
        this.rarityChances = rarityChances;
    }

}
