package com.fezlr.fantasy.grid.service;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2StatisticEntity;
import com.fezlr.fantasy.cs2.player.service.PlayerCS2Service;
import com.fezlr.fantasy.grid.dto.stats.PlayerAggregateStats;
import com.fezlr.fantasy.grid.repository.SeriesPlayerStatCS2Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerStatisticsAggregationService {
    private final PlayerCS2Service playerCS2Service;
    private final SeriesPlayerStatCS2Repository seriesPlayerStatCS2Repository;

    @Transactional
    public void recalculateAll() {
        Instant since = Instant.now().minus(90, ChronoUnit.DAYS);

        List<PlayerCS2Entity> players = playerCS2Service.findAllByGridIdIsNotNull();

        for (PlayerCS2Entity player : players) {
            Optional<PlayerAggregateStats> aggregate = seriesPlayerStatCS2Repository
                    .aggregateForPlayer(player.getGridId(), since);

            aggregate.ifPresentOrElse(
                    stats -> applyStats(player, stats),
                    () -> log.info("No aggregate data for playerId={} (gridId={})", player.getId(), player.getGridId())
            );
        }
    }

    private void applyStats(PlayerCS2Entity player, PlayerAggregateStats stats) {
        PlayerCS2StatisticEntity statEntity = player.getPlayerStatistics();
        if (statEntity == null) {
            statEntity = new PlayerCS2StatisticEntity();
            statEntity.setPlayer(player);
            player.setPlayerStatistics(statEntity);
        }

        statEntity.setSeriesCount(stats.seriesCount().intValue());
        statEntity.setKills(stats.kills().intValue());
        statEntity.setDeaths(stats.deaths().intValue());
        statEntity.setRating(computeRating(stats));
        statEntity.setLastSyncedAt(Instant.now());

        playerCS2Service.save(player);
    }

    private BigDecimal computeRating(PlayerAggregateStats stats) {
        int seriesCount = stats.seriesCount().intValue();
        if (seriesCount == 0) {
            return BigDecimal.ZERO;
        }

        double killsAvg = stats.kills().doubleValue() / seriesCount;
        double deathsAvg = stats.deaths().doubleValue() / seriesCount;
        double raw = killsAvg - deathsAvg;

        return BigDecimal.valueOf(raw).setScale(2, RoundingMode.HALF_UP);
    }
}
