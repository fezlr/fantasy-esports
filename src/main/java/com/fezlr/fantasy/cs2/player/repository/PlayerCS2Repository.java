package com.fezlr.fantasy.cs2.player.repository;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PlayerCS2Repository extends JpaRepository<PlayerCS2Entity, Long> {

    Optional<PlayerCS2Entity> findByPandaScoreId(Long pandaScoreId);

    List<PlayerCS2Entity> findAllByGridIdIsNull();

    List<PlayerCS2Entity> findAllBySteamIdIsNotNull();

}
