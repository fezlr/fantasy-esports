package com.fezlr.fantasy.valve.team.entity;

import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "teams_cs2_valve_history")
public class ValveHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private TeamCS2Entity team;

    @Column(name = "vrs_points", nullable = false)
    private Integer vrsPoints;

    @Column(name = "valve_rating", nullable = false)
    private Integer valveRating;

    @Column(name = "ranking_date", nullable = false)
    private LocalDate rankingDate;
}
