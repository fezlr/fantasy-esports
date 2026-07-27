package com.fezlr.fantasy.valve.team.sync;

import com.fezlr.fantasy.valve.team.service.ValveRankingSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValveSyncJob {
    private final ValveRankingSyncService valveRankingSyncService;

    public void syncTeamCS2ValveRankings() {
        log.info("Starting syncing team cs2 valve rankings");

        valveRankingSyncService.syncLatest();

        log.info("Ending syncing team cs2 valve rankings");
    }
}
