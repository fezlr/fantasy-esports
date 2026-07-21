package com.fezlr.fantasy.cs2.team.repository;

import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamCS2Repository extends JpaRepository<TeamCS2Entity, Long> {

    Optional<TeamCS2Entity> findByPandaScoreId(Long pandaScoreId);

    Optional<TeamCS2Entity> findByIdAndGridIdIsNull(Long id);
}
