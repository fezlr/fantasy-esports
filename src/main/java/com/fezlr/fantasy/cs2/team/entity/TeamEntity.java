package com.fezlr.fantasy.cs2.team.entity;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "teams")
public class TeamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "hltv_rank")
    private Integer hltvRank;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "source_id", unique = true)
    private Long sourceId;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<PlayerCS2Entity> players = new ArrayList<>();
}
