package com.fezlr.fantasy.grid.resolver;

import com.fezlr.fantasy.grid.dto.player_search.PlayerSearchDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class GridPlayerMatcher {

    public Optional<PlayerSearchDto> matchExact(
            List<PlayerSearchDto> candidates,
            String expectedNickname
    ) {
        List<PlayerSearchDto> matches = candidates.stream()
                .filter(player -> player.nickname() != null)
                .filter(player -> player.nickname().strip()
                        .equalsIgnoreCase(expectedNickname.strip()))
                .toList();

        if (matches.size() == 1) {
            return Optional.of(matches.getFirst());
        }

        if (matches.isEmpty() || !haveSameSteamId(matches)) {
            return Optional.empty();
        }

        return matches.stream()
                .max(Comparator.comparingLong(PlayerSearchDto::id));
    }

    private boolean haveSameSteamId(List<PlayerSearchDto> players) {
        Long steamId = getSteamId(players.getFirst());

        return steamId != null
                && players.stream()
                .allMatch(player -> Objects.equals(steamId, getSteamId(player)));
    }

    private Long getSteamId(PlayerSearchDto player) {
        return player.externalLinks().stream()
                .filter(link -> link.dataProvider() != null)
                .filter(link -> "STEAM".equalsIgnoreCase(
                        link.dataProvider().name()
                ))
                .map(link -> link.externalEntity().id())
                .findFirst()
                .orElse(null);
    }
}