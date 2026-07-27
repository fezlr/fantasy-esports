package com.fezlr.fantasy.cs2.team.entity;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.valve.team.entity.ValveHistoryEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "teams_cs2")
public class TeamCS2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "valveName")
    private String valveName;

    @Column(name = "vrs_points")
    private Integer vrsPoints;

    @Column(name = "valve_rating")
    private Integer valveRating;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "panda_score_id", unique = true)
    private Long pandaScoreId;

    @Column(name = "grid_id", unique = true)
    private Long gridId;

    @Builder.Default
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<PlayerCS2Entity> players = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<ValveHistoryEntity> valveTeamHistory = new ArrayList<>();

    @Column(name = "last_valve_rating_synced_at")
    private LocalDate lastValveRatingSyncedAt;

    @Column(name = "last_series_synced_at")
    private Instant lastSeriesSyncedAt;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;
}
