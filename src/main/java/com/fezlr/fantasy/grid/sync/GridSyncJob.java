package com.fezlr.fantasy.grid.sync;

import com.fezlr.fantasy.grid.service.GridBackfillOrchestratorService;
import com.fezlr.fantasy.grid.service.PlayerGridIdResolutionService;
import com.fezlr.fantasy.grid.service.PlayerStatisticsAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class GridSyncJob {
    private final GridBackfillOrchestratorService gridBackfillOrchestratorService;
    private final PlayerGridIdResolutionService playerGridIdResolutionService;
    private final PlayerStatisticsAggregationService playerStatisticsAggregationService;

//    @Scheduled(cron = "0 0 5 * * *")
    public void syncTeamsAndPlayers() {
        log.info("Starting GRID sync..");

        gridBackfillOrchestratorService.syncAllTeams();
        playerStatisticsAggregationService.recalculateAll();

        log.info("Ending GRID sync..");
    }

    public void syncMissingGridIdPlayersAndTeams() {
        log.info("Starting sync missing gridId players and teams");

        playerGridIdResolutionService.resolveAllMissing();

        log.info("Ending sync missing gridId players and teams");
    }
}
