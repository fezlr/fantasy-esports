package com.fezlr.fantasy.cs2.player.repository;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerCS2Repository extends JpaRepository<PlayerCS2Entity, Long> {
}
