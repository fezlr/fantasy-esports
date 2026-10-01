package com.fezlr.fantasy.cs2.card.entity;

import com.fezlr.fantasy.cs2.card.enums.Rarity;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.user.entity.UserCS2Entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

//TODO: cron for sellPrice

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
    @JoinColumn(name = "user_cs2_id", nullable = false)
    private UserCS2Entity userCS2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_cs2_id", nullable = false)
    private PlayerCS2Entity playerCS2;

    @Column(name = "sell_price")
    private long sellPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "rarity", nullable = false)
    private Rarity rarity;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;
}
