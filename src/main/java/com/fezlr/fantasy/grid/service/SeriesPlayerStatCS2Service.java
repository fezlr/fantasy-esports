package com.fezlr.fantasy.grid.service;

import com.fezlr.fantasy.grid.dto.stats.PlayerAggregateStats;
import com.fezlr.fantasy.grid.entity.SeriesPlayerStatCS2Entity;
import com.fezlr.fantasy.grid.repository.SeriesPlayerStatCS2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SeriesPlayerStatCS2Service {
    private final SeriesPlayerStatCS2Repository seriesPlayerStatCS2Repository;

    public Optional<PlayerAggregateStats> aggregateForPlayer(Long steamId, Instant since) {
        return seriesPlayerStatCS2Repository.aggregateForPlayer(steamId, since);
    }

    public boolean existsBySeriesId(Long id) {
        return seriesPlayerStatCS2Repository.existsBySeriesId(id);
    }

    @Transactional
    public void saveAll(List<SeriesPlayerStatCS2Entity> entities) {
        seriesPlayerStatCS2Repository.saveAll(entities);
    }
}
