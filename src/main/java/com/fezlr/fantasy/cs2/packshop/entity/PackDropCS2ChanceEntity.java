package com.fezlr.fantasy.cs2.packshop.entity;

import com.fezlr.fantasy.cs2.card.enums.Rarity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "pack_drop_chances_cs2",
        uniqueConstraints = @UniqueConstraint(
            name = "uq_pack_shop_rarity",
            columnNames = {"pack_shop_cs2_id", "rarity"}
))
public class PackDropCS2ChanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_shop_cs2_id")
    private PackShopCS2Entity packShop;

    @Enumerated(EnumType.STRING)
    @Column(name = "rarity")
    private Rarity rarity;

    @Column(name = "chance")
    private BigDecimal chance;
}
