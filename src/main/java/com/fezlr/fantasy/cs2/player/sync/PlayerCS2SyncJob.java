package com.fezlr.fantasy.cs2.player.sync;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.service.PlayerCS2Service;
import com.fezlr.fantasy.pandascore.client.PandaScoreClient;
import com.fezlr.fantasy.pandascore.dto.PandaScorePlayerDto;
import com.fezlr.fantasy.pandascore.mapper.PandaScorePlayerCS2Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PlayerCS2SyncJob {
    private final PandaScoreClient pandaScoreClient;
    private final PandaScorePlayerCS2Mapper pandaScorePlayerCS2Mapper;
    private final PlayerCS2Service playerCS2Service;

    public void syncPlayers() {
        log.info("Starting syncing players INSTANT={}", Instant.now());
        List<PandaScorePlayerDto> players = pandaScoreClient.fetchAllPlayers();

        int updated = 0;
        for (PandaScorePlayerDto dto : players) {
            try {
                PlayerCS2Entity entity = pandaScorePlayerCS2Mapper.toEntity(dto);
                playerCS2Service.createOrUpdate(entity);
                updated++;
            } catch (Exception e) {
                log.error("Failed to sync player sourceId={}", dto.sourceId(), e);
            }
        }
        log.info("Ending syncing players, UPDATED={}/{}, INSTANT={}", updated, players.size(), Instant.now());
    }
}
