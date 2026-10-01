package com.fezlr.fantasy.cs2.card.repository;

import com.fezlr.fantasy.cs2.card.entity.CardCS2Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardCS2Repository extends JpaRepository<CardCS2Entity, Long> {

    @Query("""
       SELECT c FROM CardCS2Entity c
       JOIN FETCH c.playerCS2 p
       LEFT JOIN FETCH  p.playerStatistics ps
       WHERE c.userCS2.user.id = :userId
    """)
    List<CardCS2Entity> findAllByUserId(@Param("userId") Long userId);

}
