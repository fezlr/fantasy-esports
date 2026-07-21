package com.fezlr.fantasy.grid.controller;

import com.fezlr.fantasy.grid.dto.player_statistics.CS2PlayerSeriesStatisticsDto;
import com.fezlr.fantasy.grid.service.PlayerGridIdResolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/_test/grid")
@RequiredArgsConstructor
public class GridTestController {
    private final HttpGraphQlClient baseGraphQlClient;
    private final PlayerGridIdResolutionService playerGridIdResolutionService;

    @GetMapping("/player-stats")
    public CS2PlayerSeriesStatisticsDto testPlayerStats(
//            @PathVariable String playerId
    ) {
        return baseGraphQlClient
                .documentName("playerStatistics")
//                .variable("playerId", playerId)
                .retrieveSync("playerStatistics.series")
                .toEntity(CS2PlayerSeriesStatisticsDto.class);
    }

    @PostMapping("/resolve/{playerId}")
    public Optional<Long> resolvePlayer(@PathVariable Long playerId) {
        return playerGridIdResolutionService.resolveOne(playerId);
    }
}
