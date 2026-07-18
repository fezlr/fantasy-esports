package com.fezlr.fantasy.grid.client;

import com.fezlr.fantasy.grid.dto.CS2PlayerSeriesStatisticsDto;
import com.fezlr.fantasy.grid.validator.GridValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.FieldAccessException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class GridClient {
    private final HttpGraphQlClient gridGraphQlClient;
    private final GridValidator gridValidator;

    public Optional<CS2PlayerSeriesStatisticsDto> fetchPlayerStats(String gridId) {
        try {
            CS2PlayerSeriesStatisticsDto dto = gridGraphQlClient
                    .documentName("playerStatistics")
                    .variable("playerId", gridId)
                    .retrieveSync("playerStatistics.series")
                    .toEntity(CS2PlayerSeriesStatisticsDto.class);

            return Optional.ofNullable(dto)
                    .filter(gridValidator::hasMatchData);
        } catch(FieldAccessException e) {
            log.error("GRID field failed for a player's gridId = {}: {}", gridId, e.getMessage());
            return Optional.empty();
        } catch(Exception e) {
            log.error("GRID failed for a player's gridId = {}: {}", gridId, e.getMessage());
            return Optional.empty();
        }

    }
}
