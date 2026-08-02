package com.fezlr.fantasy.common.enums;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public enum Rarity {
    COMMON(BigDecimal.ONE),
    RARE(BigDecimal.valueOf(1.05)),
    EPIC(BigDecimal.valueOf(1.10)),
    MYTHICAL(BigDecimal.valueOf(1.20)),
    LEGENDARY(BigDecimal.valueOf(1.30));

    private final BigDecimal multiplier;

    Rarity(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }
}
