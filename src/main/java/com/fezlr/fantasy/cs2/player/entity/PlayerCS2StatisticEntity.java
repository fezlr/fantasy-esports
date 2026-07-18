package com.fezlr.fantasy.cs2.player.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "player_statistics_cs2")
public class PlayerCS2StatisticEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "playerStatistics")
    private PlayerCS2Entity player;

    @Column(name = "series_count")
    private Integer seriesCount;

    @Column(name = "kills")
    private Integer kills;

    @Column(name = "deaths")
    private Integer deaths;

    @Column(name = "assists")
    private Integer assists;

    @Column(name = "headshots")
    private Integer headshots;

    @Column(name = "rating")
    private BigDecimal rating;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

}
