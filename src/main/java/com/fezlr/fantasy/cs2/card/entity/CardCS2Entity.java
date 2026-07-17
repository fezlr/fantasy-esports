package com.fezlr.fantasy.cs2.card.entity;

import com.fezlr.fantasy.common.enums.Rarity;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cards_cs2")
public class CardCS2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_cs2_id")
    private PlayerCS2Entity playerCS2;

    @Column(name = "rarity", nullable = false)
    private Rarity rarity;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;
}
