package com.fezlr.fantasy.grid.service;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.service.PlayerCS2Service;
import com.fezlr.fantasy.grid.client.GridClient;
import com.fezlr.fantasy.grid.dto.player_search.PlayerSearchDto;
import com.fezlr.fantasy.grid.resolver.GridPlayerMatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerGridIdResolutionService {
    private final GridClient gridClient;
    private final GridPlayerMatcher gridPlayerMatcher;
    private final PlayerCS2Service playerCS2Service;

    /*
     * Finding player CS2 and setting grid id
     * TODO refactor: steamId
     */
    public Optional<Long> resolveOne(Long playerId) {
        PlayerCS2Entity player = playerCS2Service.findById(playerId);
        List<PlayerSearchDto> candidates = gridClient.findPlayerByNickname(player.getNickname());
        Optional<PlayerSearchDto> match = gridPlayerMatcher.matchExact(candidates, player.getNickname());

        if (match.isEmpty()) {
            return Optional.empty();
        }

        PlayerSearchDto dto = match.get();
        Long steamId = dto.steamId().orElse(null);

        if (steamId != null) {
            Optional<PlayerCS2Entity> conflict = playerCS2Service.findBySteamId(steamId);

            if (conflict.isPresent() && !conflict.get().getId().equals(player.getId())) {
                log.warn("steamId={} already belongs to playerId={} nickname={} — skipping playerId={} nickname={}, likely a duplicate PandaScore record",
                        steamId, conflict.get().getId(), conflict.get().getNickname(), player.getId(), player.getNickname());

                return Optional.empty();
            }
        }

        playerCS2Service.assignGridIdAndSetSteamId(player.getId(), dto.id(), steamId);

        return Optional.of(dto.id());
    }

    /*
     * Finding list of players CS2 and setting for them grid id
     */
    public void resolveAllMissing() {
        List<PlayerCS2Entity> players = playerCS2Service.findAllWithoutGridId();

        for (PlayerCS2Entity player : players) {
            try {
                resolveOne(player.getId());
            } catch (Exception e) {
                log.error("Failed resolving gridId for playerId={} nickname={}: {}",
                        player.getId(), player.getNickname(), e.getMessage());
            }
        }
    }
}
