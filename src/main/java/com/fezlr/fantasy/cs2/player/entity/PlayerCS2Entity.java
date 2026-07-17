package com.fezlr.fantasy.cs2.player.entity;

import com.fezlr.fantasy.cs2.enums.Role;
import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "players_cs2")
public class PlayerCS2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private TeamCS2Entity team;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "player_statistics")
    private PlayerCS2StatisticEntity playerStatistics;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "grid_id")
    private Long gridId;

    @Column(name = "panda_score_id", unique = true)
    private Long pandaScoreId;

    @Column(name = "last_synced_at", nullable = false)
    private Instant lastSyncedAt;

    //is a player playing currently or not
    @Column(name = "is_active")
    private boolean isActive;
}
