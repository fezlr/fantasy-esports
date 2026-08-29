package com.fezlr.fantasy.cs2.packshop.entity;

import com.fezlr.fantasy.cs2.pack.enums.PackType;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "packs_shop_cs2")
public class PackShopCS2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "pack_type", nullable = false)
    private PackType packType;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cost", nullable = false)
    private Integer cost;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    @OneToMany(mappedBy = "packShop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackDropCS2ChanceEntity> dropChances;
}
