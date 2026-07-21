package com.fezlr.fantasy.grid.service;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.service.PlayerCS2Service;
import com.fezlr.fantasy.cs2.team.service.TeamCS2Service;
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
    private final TeamCS2Service teamCS2Service;

    public Optional<Long> resolveOne(Long playerId) {
        PlayerCS2Entity player = playerCS2Service.findById(playerId);
        List<PlayerSearchDto> candidates = gridClient.findPlayerByNickname(player.getNickname());
        Optional<PlayerSearchDto> match = gridPlayerMatcher.matchExact(candidates, player.getNickname());

        match.ifPresent(dto -> {
            playerCS2Service.assignGridId(player.getId(), dto.id());

            if (dto.team() != null) {
                teamCS2Service.assignGridIdIfMissing(player.getTeam().getId(), dto.team().id());
            }
        });

        return match.map(PlayerSearchDto::id);
    }

    public void resolveAllMissing() {
        log.info("Starting resolveAllMissing");
        List<PlayerCS2Entity> players = playerCS2Service.findAllWithoutGridId();

        for (PlayerCS2Entity player : players) {
            try {
                Thread.sleep(3500);
                Optional<Long> gridId = resolveOne(player.getId());
                if (gridId.isEmpty()) {
                    log.warn("No exact GRID match for nickname={}", player.getNickname());
                }
            } catch (Exception e) {
                log.error("Failed resolving gridId for playerId={} nickname={}: {}",
                        player.getId(), player.getNickname(), e.getMessage());
            }
        }
        log.info("Ending resolveAllMissing");
    }
}
