package com.fezlr.fantasy.grid.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "series_player_stats_cs2")
@Entity
public class SeriesPlayerStatCS2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "series_id", nullable = false)
    private Long seriesId;

    @Column(name = "game_number", nullable = false)
    private Integer gameNumber;

    @Column(name = "steam_id", nullable = false)
    private Long steamId;

    @Column(name = "kills", nullable = false)
    private Integer kills;

    @Column(name = "kill_assists_given", nullable = false)
    private Integer killAssistsGiven;

    @Column(name = "deaths", nullable = false)
    private Integer deaths;

    @Column(name = "team_kills", nullable = false)
    private Integer teamKills;

    @Column(name = "self_kills", nullable = false)
    private Integer selfKills;

    @Column(name = "damage_dealt", nullable = false)
    private Integer damageDealt;

    @Column(name = "damage_taken", nullable = false)
    private Integer damageTaken;

    @Column(name = "headshots", nullable = false)
    private Integer headshots;

    @Column(name = "tournament_name")
    private String tournamentName;

    @Column(name = "series_started_at", nullable = false)
    private Instant seriesStartedAt;
}
