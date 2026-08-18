package com.fezlr.fantasy.user.entity;

import com.fezlr.fantasy.cs2.pack.entity.PackCS2Entity;
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
@Table(name = "users_cs2")
public class UserCS2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;

    @Builder.Default
    @OneToMany(mappedBy = "userCS2", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PackCS2Entity> packsCS2 = new ArrayList<>();

    @Builder.Default
    @Column(name = "fantasy_points", nullable = false)
    private Integer fantasyPoints = 0;
}
