package com.fezlr.fantasy.valve.team.service;
import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import com.fezlr.fantasy.cs2.team.service.TeamCS2Service;
import com.fezlr.fantasy.valve.team.entity.ValveHistoryEntity;
import com.fezlr.fantasy.valve.team.repository.ValveHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class ValveHistoryService {
    private final ValveHistoryRepository valveHistoryRepository;
    private final TeamCS2Service teamCS2Service;

    public boolean existsByRankingDate(LocalDate rankingDate) {
        return valveHistoryRepository
                .existsByRankingDate(rankingDate);
    }

    @Transactional
    public void saveRanking(TeamCS2Entity team, Integer vrsPoints, Integer valveRating , LocalDate rankingDate) {
        teamCS2Service.updateValveRanking(team, vrsPoints, valveRating, rankingDate);

        ValveHistoryEntity history = ValveHistoryEntity.builder()
                .team(team)
                .vrsPoints(vrsPoints)
                .valveRating(valveRating)
                .rankingDate(rankingDate)
                .build();

        valveHistoryRepository.save(history);
    }
}