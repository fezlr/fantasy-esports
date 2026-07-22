package com.fezlr.fantasy.grid.service;

import com.fezlr.fantasy.grid.client.GridClient;
import com.fezlr.fantasy.grid.dto.series.SeriesSummaryDto;
import com.fezlr.fantasy.grid.dto.series_state.SeriesStateDto;
import com.fezlr.fantasy.grid.entity.SeriesPlayerStatCS2Entity;
import com.fezlr.fantasy.grid.mapper.GridSeriesStatMapper;
import com.fezlr.fantasy.grid.repository.SeriesPlayerStatCS2Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class SeriesBackfillService {
    private static final int BATCH_SIZE = 20;
    private final GridClient gridClient;
    private final GridSeriesStatMapper gridSeriesStatMapper;
    private final SeriesPlayerStatCS2Repository seriesPlayerStatCS2Repository;

    public void syncTeam(Long teamGridId, Instant fallbackSince) {
        Instant since = seriesPlayerStatCS2Repository.findLatestSeriesStartedAt()
                .map(latest -> latest.minus(1, ChronoUnit.DAYS))
                .orElse(fallbackSince);

        Instant until = Instant.now();

        List<SeriesSummaryDto> series = gridClient.fetchSeriesForTeam(teamGridId, since, until);

        List<SeriesSummaryDto> newSeries = series.stream()
                .filter(s -> !seriesPlayerStatCS2Repository.existsBySeriesId(s.id()))
                .toList();

        if (newSeries.isEmpty()) {
            log.info("There are no new series for teamGridId={}", teamGridId);
            return;
        }

        Map<Long, Instant> startTimesBySeriesId = newSeries.stream()
                .collect(Collectors.toMap(SeriesSummaryDto::id, SeriesSummaryDto::startTimeScheduled));

        List<List<SeriesSummaryDto>> batches = partition(newSeries, BATCH_SIZE);

        for (List<SeriesSummaryDto> batch : batches) {
            List<Long> batchIds = batch.stream().map(SeriesSummaryDto::id).toList();
            Map<Long, SeriesStateDto> states = gridClient.fetchSeriesStates(batchIds);

            List<SeriesPlayerStatCS2Entity> entities = gridSeriesStatMapper.toFlatEntities(states, startTimesBySeriesId);
            saveAllStats(entities);
        }

        log.info("Synced {} new series for teamGridId={}", newSeries.size(), teamGridId);
    }

    @Transactional
    protected void saveAllStats(List<SeriesPlayerStatCS2Entity> entities) {
        seriesPlayerStatCS2Repository.saveAll(entities);
    }

    private List<List<SeriesSummaryDto>> partition(List<SeriesSummaryDto> list, int size) {
        return IntStream.range(0, (list.size() + size - 1) / size)
                .mapToObj(i -> list.subList(i * size, Math.min(list.size(), (i + 1) * size)))
                .toList();
    }
}
