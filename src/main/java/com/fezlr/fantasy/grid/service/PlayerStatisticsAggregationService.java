package com.fezlr.fantasy.grid.service;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2StatisticEntity;
import com.fezlr.fantasy.cs2.player.service.PlayerCS2Service;
import com.fezlr.fantasy.cs2.rating.service.PlayerCS2RatingService;
import com.fezlr.fantasy.grid.dto.stats.PlayerAggregateStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerStatisticsAggregationService {
    private static final int LOOK_BACK_DAYS = 90;
    private final PlayerCS2Service playerCS2Service;
    private final PlayerCS2RatingService playerCS2RatingService;
    private final SeriesPlayerStatCS2Service seriesPlayerStatCS2Service;
    private final ObjectProvider<PlayerStatisticsAggregationService> selfProvider;

    /*
     * Finding aggregate stats for a single player and applying them
     */
    @Transactional
    public void recalculateOne(Long playerId) {
        PlayerCS2Entity player = playerCS2Service.findById(playerId);
        Instant since = Instant.now().minus(LOOK_BACK_DAYS, ChronoUnit.DAYS);

        Optional<PlayerAggregateStats> aggregate = seriesPlayerStatCS2Service
                .aggregateForPlayer(player.getSteamId(), since);

        aggregate.ifPresentOrElse(
                stats -> applyStats(player, stats),
                () -> log.info("No aggregate data for playerId={} (steamId={})", player.getId(), player.getSteamId())
        );
    }

    /*
     * Finding list of players and recalculating stats for each of them
     */
    public void recalculateAll() {
        List<PlayerCS2Entity> players = playerCS2Service.findAllBySteamIdIsNotNull();
        PlayerStatisticsAggregationService self = selfProvider.getObject();

        for (PlayerCS2Entity player : players) {
            try {
                self.recalculateOne(player.getId());
            } catch (Exception e) {
                log.error("Failed recalculating stats for playerId={} nickname={}: {}",
                        player.getId(), player.getNickname(), e.getMessage());
            }
        }
    }

    private void applyStats(PlayerCS2Entity player, PlayerAggregateStats stats) {
        PlayerCS2StatisticEntity statEntity = player.getPlayerStatistics();
        if (statEntity == null) {
            log.info("Creating statistic entity for playerNickname={}, playerId={}", player.getNickname(), player.getId());
            statEntity = PlayerCS2StatisticEntity.builder()
                    .player(player)
                    .build();

            player.setPlayerStatistics(statEntity);
        }

        statEntity.setSeriesCount(stats.seriesCount());
        statEntity.setKills(stats.kills());
        statEntity.setAssists(stats.killAssistsGiven());
        statEntity.setHeadshots(stats.headshots());
        statEntity.setDeaths(stats.deaths());
        statEntity.setRating(playerCS2RatingService.calculate(stats));
        statEntity.setLastSyncedAt(Instant.now());

        playerCS2Service.save(player);
        log.info("Synced new stats for playerNickname={}, playerId={}", player.getNickname(), player.getId());
    }
}