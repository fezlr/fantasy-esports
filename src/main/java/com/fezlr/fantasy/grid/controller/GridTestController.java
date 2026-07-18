package com.fezlr.fantasy.grid.controller;

import com.fezlr.fantasy.grid.client.GridClient;
import com.fezlr.fantasy.grid.dto.CS2PlayerSeriesStatisticsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/_test/grid")
@RequiredArgsConstructor
public class GridTestController {
    private final GridClient gridClient;
    private final HttpGraphQlClient gridGraphQlClient;

    @GetMapping("/player-stats")
    public CS2PlayerSeriesStatisticsDto testPlayerStats(
//            @PathVariable String playerId
    ) {
        return gridGraphQlClient
                .documentName("playerStatistics")
//                .variable("playerId", playerId)
                .retrieveSync("playerStatistics.series")
                .toEntity(CS2PlayerSeriesStatisticsDto.class);
    }
}
