package com.fezlr.fantasy.cs2.player.entity;

import jakarta.persistence.*;
import lombok.*;

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

    //other fields...


}
