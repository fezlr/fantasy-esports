package com.fezlr.fantasy.valve.team.repository;

import com.fezlr.fantasy.valve.team.entity.ValveHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ValveHistoryRepository extends JpaRepository<ValveHistoryEntity, Long> {

    boolean existsByRankingDate(LocalDate rankingDate);
}
