package com.fezlr.fantasy.cs2.sync;

import com.fezlr.fantasy.cs2.team.sync.TeamCS2SyncJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CS2SyncJob {
    private final TeamCS2SyncJob teamCS2SyncJob;

    @Scheduled(fixedRate = 3_600_000)
    public void syncAll() {
        log.info("Starting syncAll..");

        teamCS2SyncJob.syncTeams();

        log.info("Ending syncAll...");
    }
}
