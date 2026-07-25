package com.fezlr.fantasy.grid.service;

import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import com.fezlr.fantasy.cs2.team.service.TeamCS2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GridBackfillOrchestratorService {
    private final TeamCS2Service teamCS2Service;
    private final SeriesBackfillService seriesBackfillService;

    public void syncAllTeams() {
        Instant fallbackSince = Instant.now().minus(90, ChronoUnit.DAYS);

        List<TeamCS2Entity> teams = teamCS2Service.findAll();

        for (TeamCS2Entity team : teams) {
            if (team.getGridId() == null) {
                log.warn("Skipping teamId={} ({}) — no gridId resolved yet", team.getId(), team.getName());
                continue;
            }

            try {
                seriesBackfillService.syncTeam(team.getId(), fallbackSince);
            } catch (Exception e) {
                log.error("Failed syncing teamGridId={}, teamName={}: {}", team.getGridId(), team.getName(), e.getMessage());
            }
        }
    }
}
