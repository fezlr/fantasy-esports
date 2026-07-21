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
                s.gridPlayerId,
                COUNT(DISTINCT s.seriesId),
                SUM(s.kills),
                SUM(s.deaths)
            )
            FROM SeriesPlayerStatCS2Entity s
            WHERE s.gridPlayerId = :gridPlayerId
              AND s.seriesStartedAt >= :since
            GROUP BY s.gridPlayerId
            """)
    Optional<PlayerAggregateStats> aggregateForPlayer(@Param("gridPlayerId") Long gridPlayerId, @Param("since") Instant since);

    boolean existsBySeriesId(Long seriesId);

}
