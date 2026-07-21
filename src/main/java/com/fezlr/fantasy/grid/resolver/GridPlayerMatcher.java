package com.fezlr.fantasy.grid.resolver;

import com.fezlr.fantasy.grid.dto.player_search.PlayerSearchDto;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class GridPlayerMatcher {

    //ivan == IVAN equalsIgnoreCase
    public Optional<PlayerSearchDto> matchExact(List<PlayerSearchDto> candidates, String expectedNickname) {
        return candidates.stream()
                .filter(p -> p.nickname().equalsIgnoreCase(expectedNickname))
                .max(Comparator.comparingLong(this::parseIdOrZero));
    }

    private long parseIdOrZero(PlayerSearchDto player) {
        try {
            return player.id();
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}