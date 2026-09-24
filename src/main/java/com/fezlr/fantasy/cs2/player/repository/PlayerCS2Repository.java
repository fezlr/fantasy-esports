package com.fezlr.fantasy.cs2.player.repository;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlayerCS2Repository extends JpaRepository<PlayerCS2Entity, Long> {

    @Query(nativeQuery = true,
    value = """
            SELECT * FROM players_cs2
            WHERE is_active = true
            ORDER BY RANDOM()
            LIMIT 1
            """)
    Optional<PlayerCS2Entity> findRandomActivePlayer();


    Optional<PlayerCS2Entity> findByPandaScoreId(Long pandaScoreId);

    Optional<PlayerCS2Entity> findBySteamId(Long steamId);

    List<PlayerCS2Entity> findAllByGridIdIsNull();

    List<PlayerCS2Entity> findAllBySteamIdIsNotNull();
}
