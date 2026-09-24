package com.fezlr.fantasy.cs2.sync;

import com.fezlr.fantasy.grid.sync.GridSyncJob;
import com.fezlr.fantasy.pandascore.sync.PandaScoreSyncJob;
import com.fezlr.fantasy.valve.team.sync.ValveSyncJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CS2SyncJob {
    private final PandaScoreSyncJob pandaScoreSyncJob;
    private final GridSyncJob gridSyncJob;
    private final ValveSyncJob valveSyncJob;

    @Scheduled(fixedRate = 3_600_000)
    public void syncAll() {
        log.info("Starting syncAll..");

        //pandascore api
//        pandaScoreSyncJob.syncTeamsAndPlayers();

        //grid api
//        gridSyncJob.syncMissingGridIdPlayers();
//        gridSyncJob.syncMissingGridIdTeams();
//        gridSyncJob.syncTeamSeries();
//        gridSyncJob.calculatePlayerStatistics();

        //valve rankings
//        valveSyncJob.syncTeamCS2ValveRankings();

        log.info("Ending syncAll...");
    }
}
