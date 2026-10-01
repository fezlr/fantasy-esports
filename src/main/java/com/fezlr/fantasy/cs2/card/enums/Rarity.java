package com.fezlr.fantasy.cs2.card.enums;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public enum Rarity {
    COMMON(BigDecimal.valueOf(1.0)),
    RARE(BigDecimal.valueOf(1.4)),
    EPIC(BigDecimal.valueOf(2.0)),
    MYTHICAL(BigDecimal.valueOf(2.8)),
    LEGENDARY(BigDecimal.valueOf(4.0));

    private final BigDecimal multiplier;

    Rarity(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }
}
