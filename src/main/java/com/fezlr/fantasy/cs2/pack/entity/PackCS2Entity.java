package com.fezlr.fantasy.cs2.pack.entity;

import com.fezlr.fantasy.cs2.packshop.entity.PackShopCS2Entity;
import com.fezlr.fantasy.user.entity.UserCS2Entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "packs_cs2")
public class PackCS2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_cs2_id", nullable = false)
    private UserCS2Entity userCS2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_shop_cs2_id")
    private PackShopCS2Entity packShopCS2;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "opened_at")
    private Instant openedAt;

    @Column(name = "is_opened", nullable = false)
    private boolean isOpened;
}
