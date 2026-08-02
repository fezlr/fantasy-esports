package com.fezlr.fantasy.grid.service;

import com.fezlr.fantasy.common.validator.SteamValidator;
import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import com.fezlr.fantasy.cs2.team.service.TeamCS2Service;
import com.fezlr.fantasy.grid.client.GridClient;
import com.fezlr.fantasy.grid.dto.series.SeriesMeta;
import com.fezlr.fantasy.grid.dto.series.SeriesSummaryDto;
import com.fezlr.fantasy.grid.dto.series_state.SeriesStateDto;
import com.fezlr.fantasy.grid.entity.SeriesPlayerStatCS2Entity;
import com.fezlr.fantasy.grid.mapper.GridSeriesStatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class SeriesBackfillService {
    private static final int BATCH_SIZE = 5;
    private final GridClient gridClient;
    private final GridSeriesStatMapper gridSeriesStatMapper;
    private final SeriesPlayerStatCS2Service seriesPlayerStatCS2Service;
    private final TeamCS2Service teamCS2Service;
    private final SteamValidator steamValidator;

    public void syncTeam(Long teamId, Instant fallbackSince) {
        TeamCS2Entity team = teamCS2Service.findById(teamId);
        Long gridTeamId = team.getGridId();

        Instant since = Objects.requireNonNullElse(team.getLastSeriesSyncedAt(), fallbackSince);
        Instant until = Instant.now();

        List<SeriesSummaryDto> series = gridClient.fetchSeriesForTeam(gridTeamId, since, until);

        List<SeriesSummaryDto> newSeries = series.stream()
                .filter(s -> !seriesPlayerStatCS2Service.existsBySeriesId(s.id()))
                .toList();

        if (newSeries.isEmpty()) {
            log.info("No new series for teamName={}, teamGridId={}", team.getName(), gridTeamId);
            teamCS2Service.updateLastSeriesSyncedAt(teamId, until);
            return;
        }

        Map<Long, SeriesMeta> metaBySeriesId = newSeries.stream()
                .collect(Collectors.toMap(
                        SeriesSummaryDto::id,
                        s -> new SeriesMeta(
                                s.startTimeScheduled(),
                                s.tournament() != null ? s.tournament().name() : null
                        )
                ));

        List<List<SeriesSummaryDto>> batches = partition(newSeries);

        for (List<SeriesSummaryDto> batch : batches) {
            List<Long> batchIds = batch.stream().map(SeriesSummaryDto::id).toList();
            Map<Long, SeriesStateDto> states = gridClient.fetchSeriesStates(batchIds);

            List<SeriesPlayerStatCS2Entity> entities = gridSeriesStatMapper.toFlatEntities(states, metaBySeriesId);
            seriesPlayerStatCS2Service.saveAll(entities);
        }

        teamCS2Service.updateLastSeriesSyncedAt(teamId, until);
        log.info("Synced {} new series for teamName={}, teamGridId={}", newSeries.size(), team.getName(), gridTeamId);
    }

    private List<List<SeriesSummaryDto>> partition(List<SeriesSummaryDto> list) {
        return IntStream.range(0, (list.size() + BATCH_SIZE - 1) / BATCH_SIZE)
                .mapToObj(i -> list.subList(i * BATCH_SIZE, Math.min(list.size(), (i + 1) * BATCH_SIZE)))
                .toList();
    }
}