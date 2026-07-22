package com.fezlr.fantasy.cs2.team.repository;

import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamCS2Repository extends JpaRepository<TeamCS2Entity, Long> {

    Optional<TeamCS2Entity> findByPandaScoreId(Long pandaScoreId);

    List<TeamCS2Entity> findAllByGridIdIsNull();
}
