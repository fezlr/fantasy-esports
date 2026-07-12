package com.fezlr.fantasy.cs2.player.entity;

import com.fezlr.fantasy.cs2.enums.Role;
import com.fezlr.fantasy.cs2.team.entity.TeamEntity;
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

    //hltv rating for 3 months
    @Column(name = "rating", precision = 3, scale = 2)
    private Float rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private TeamEntity team;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "source_id", unique = true)
    private Long sourceId;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    //is a player playing currently or not
    @Column(name = "is_active")
    private boolean isActive;
}
