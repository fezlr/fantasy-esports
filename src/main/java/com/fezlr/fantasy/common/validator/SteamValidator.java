package com.fezlr.fantasy.common.validator;

import org.springframework.stereotype.Component;

@Component
public class SteamValidator {
    private static final long MIN_STEAM_ID64 = 76561197960265728L;

    public boolean isValidSteamId(Long steamId) {
        return MIN_STEAM_ID64 <= steamId;
    }
}
