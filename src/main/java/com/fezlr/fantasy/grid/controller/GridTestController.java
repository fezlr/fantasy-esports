package com.fezlr.fantasy.grid.controller;

import com.fezlr.fantasy.cs2.player.service.PlayerCS2Service;
import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import com.fezlr.fantasy.cs2.team.service.TeamCS2Service;
import com.fezlr.fantasy.grid.service.PlayerGridIdResolutionService;
import com.fezlr.fantasy.grid.service.PlayerStatisticsAggregationService;
import com.fezlr.fantasy.grid.service.SeriesBackfillService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/_test/grid")
@RequiredArgsConstructor
public class GridTestController {
    private final PlayerGridIdResolutionService playerGridIdResolutionService;
    private final TeamCS2Service teamCS2Service;
    private final SeriesBackfillService seriesBackfillService;
    private final PlayerStatisticsAggregationService playerStatisticsAggregationService;

    @PostMapping("/sync-team/{teamId}")
    public String syncTeam(@PathVariable Long teamId) {
        TeamCS2Entity team = teamCS2Service.findById(teamId);

        if (team.getGridId() == null) {
            return "team has no gridId yet";
        }

        Instant fallbackSince = Instant.now().minus(90, ChronoUnit.DAYS);
        seriesBackfillService.syncTeam(team.getGridId(), fallbackSince);

        return "synced";
    }

    @PostMapping("/aggregate/{playerId}")
    public String aggregate(@PathVariable Long playerId) {
        playerStatisticsAggregationService.recalculateOne(playerId);
        return "aggregated";
    }


    @PostMapping("/resolve/{playerId}")
    public Optional<Long> resolvePlayer(@PathVariable Long playerId) {
        return playerGridIdResolutionService.resolveOne(playerId);
    }
}
