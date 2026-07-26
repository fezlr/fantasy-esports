package com.fezlr.fantasy.grid.sync;

import com.fezlr.fantasy.grid.service.GridBackfillOrchestratorService;
import com.fezlr.fantasy.grid.service.PlayerGridIdResolutionService;
import com.fezlr.fantasy.grid.service.PlayerStatisticsAggregationService;
import com.fezlr.fantasy.grid.service.TeamGridIdResolutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class GridSyncJob {
    private final GridBackfillOrchestratorService gridBackfillOrchestratorService;
    private final PlayerGridIdResolutionService playerGridIdResolutionService;
    private final TeamGridIdResolutionService teamGridIdResolutionService;
    private final PlayerStatisticsAggregationService playerStatisticsAggregationService;

//    @Scheduled(cron = "0 0 5 * * *")
    public void syncTeamsAndPlayers() {
        log.info("Starting GRID sync..");

//        syncTeamSeries();
//        calculatePlayerStatistics();

        log.info("Ending GRID sync..");
    }

    public void syncTeamSeries() {
        log.info("Starting syncing new series for teams");

        gridBackfillOrchestratorService.syncAllTeams();

        log.info("Ending syncing new series for teams");
    }

    public void calculatePlayerStatistics() {
        log.info("Starting calculating player's statistics");

        playerStatisticsAggregationService.recalculateAll();

        log.info("Ending calculating player's statistics");
    }

    public void syncMissingGridIdPlayers() {
        log.info("Starting syncing missing gridId players");

        playerGridIdResolutionService.resolveAllMissing();

        log.info("Ending syncing missing gridId players");
    }

    public void syncMissingGridIdTeams() {
        log.info("Starting syncing missing gridId teams");

        teamGridIdResolutionService.resolveAllMissing();

        log.info("Ending syncing missing gridId teams");
    }
}
