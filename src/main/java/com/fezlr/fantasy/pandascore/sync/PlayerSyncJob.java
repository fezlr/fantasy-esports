package com.fezlr.fantasy.pandascore.sync;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.service.PlayerCS2Service;
import com.fezlr.fantasy.pandascore.client.PandaScoreClient;
import com.fezlr.fantasy.pandascore.dto.PandaScorePlayerDto;
import com.fezlr.fantasy.pandascore.mapper.PandaScoreMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PlayerSyncJob {
    private final PandaScoreClient pandaScoreClient;
    private final PandaScoreMapper pandaScoreMapper;
    private final PlayerCS2Service playerCS2Service;

    @Scheduled(fixedRate = 3_600_000)
    public void syncPlayers() {
        log.info("Starting syncing players INSTANT={}", Instant.now());
        List<PandaScorePlayerDto> players = pandaScoreClient.fetchAllPlayers();

        int updated = 0;
        for (PandaScorePlayerDto dto : players) {
            try {
                PlayerCS2Entity entity = pandaScoreMapper.toEntity(dto);
                playerCS2Service.createOrUpdate(entity);
                updated++;
            } catch (Exception e) {
                log.error("Failed to sync player sourceId={}", dto.sourceId(), e);
            }
        }
        log.info("Ending syncing players, UPDATED={}/{}, INSTANT={}", updated, players.size(), Instant.now());
    }
}
