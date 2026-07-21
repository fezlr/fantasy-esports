package com.fezlr.fantasy.grid.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "series_player_stats_cs2", uniqueConstraints = @UniqueConstraint(columnNames = {"series_id", "game_number", "grid_player_id"}))
@Entity
public class SeriesPlayerStatCS2Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "series_id", nullable = false)
    private Long seriesId;

    @Column(name = "game_number", nullable = false)
    private Integer gameNumber;

    @Column(name = "grid_player_id", nullable = false)
    private Long gridPlayerId;

    @Column(nullable = false)
    private Integer kills;

    @Column(nullable = false)
    private Integer deaths;

    @Column(name = "series_started_at", nullable = false)
    private Instant seriesStartedAt;
}
