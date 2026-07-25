package com.fezlr.fantasy.grid.repository;

import com.fezlr.fantasy.grid.dto.stats.PlayerAggregateStats;
import com.fezlr.fantasy.grid.entity.SeriesPlayerStatCS2Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;

public interface SeriesPlayerStatCS2Repository extends JpaRepository<SeriesPlayerStatCS2Entity, Long> {

    @Query("SELECT MAX(s.seriesStartedAt) FROM SeriesPlayerStatCS2Entity s")
    Optional<Instant> findLatestSeriesStartedAt();

    @Query("""
            SELECT new com.fezlr.fantasy.grid.dto.stats.PlayerAggregateStats(
                s.steamId,
                CAST(COUNT(DISTINCT s.seriesId) AS integer),
                CAST(SUM(s.kills) AS integer),
                CAST(SUM(s.killAssistsGiven) AS integer),
                CAST(SUM(s.deaths) AS integer),
                CAST(SUM(s.headshots) AS integer)
            )
            FROM SeriesPlayerStatCS2Entity s
            WHERE s.steamId = :steamId
              AND s.seriesStartedAt >= :since
            GROUP BY s.steamId
            """)
    Optional<PlayerAggregateStats> aggregateForPlayer(@Param("steamId") Long steamId, @Param("since") Instant since);

    boolean existsBySeriesId(Long seriesId);

}
