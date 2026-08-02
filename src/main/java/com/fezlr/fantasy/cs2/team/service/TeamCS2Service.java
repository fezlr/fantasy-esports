package com.fezlr.fantasy.cs2.team.service;

import com.fezlr.fantasy.cs2.team.dto.response.TeamCS2Response;
import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import com.fezlr.fantasy.cs2.team.mapper.TeamCS2Mapper;
import com.fezlr.fantasy.cs2.team.repository.TeamCS2Repository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamCS2Service {
    private final TeamCS2Repository teamCS2Repository;
    private final TeamCS2Mapper teamCS2Mapper;

    public List<TeamCS2Response> findAllTeams() {
        return teamCS2Repository.findAll().stream()
                .map(teamCS2Mapper::toResponse).toList();
    }

    public List<TeamCS2Entity> findAll() {
        return teamCS2Repository.findAll();
    }

    public TeamCS2Entity findByPandaScoreIdOrNull(Long pandaScoreId) {
        return teamCS2Repository.findByPandaScoreId(pandaScoreId)
                .orElse(null);
    }

    @Transactional
    public TeamCS2Entity createOrUpdate(TeamCS2Entity newTeamInfoEntity) {
        return teamCS2Repository.findByPandaScoreId(newTeamInfoEntity.getPandaScoreId())
                .map(existing -> {
                    existing.setName(newTeamInfoEntity.getName());
                    existing.setLastSyncedAt(Instant.now());

                    return teamCS2Repository.save(existing);
                })
                .orElseGet(() -> {
                    newTeamInfoEntity.setLastSyncedAt(Instant.now());
                    return teamCS2Repository.save(newTeamInfoEntity);
                });
    }

    @Transactional
    public void assignGridIdIfMissing(Long teamId, Long gridTeamId) {
        TeamCS2Entity entity = teamCS2Repository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team is not found"));

        if (entity.getGridId() == null) {
            entity.setGridId(gridTeamId);
        }

        teamCS2Repository.save(entity);

        log.info("Player updated: teamId={}, name={}, gridId={}", teamId, entity.getName(), entity.getGridId());
    }

    public TeamCS2Entity findById(Long teamId) {
        return teamCS2Repository.findById(teamId).orElseThrow(() -> new EntityNotFoundException("Team is not found"));
    }

    @Transactional
    public void updateLastSeriesSyncedAt(Long teamId, Instant timestamp) {
        TeamCS2Entity team = findById(teamId);
        team.setLastSeriesSyncedAt(timestamp);
        teamCS2Repository.save(team);
    }

    public List<TeamCS2Entity> findAllWithoutGridId() {
        return teamCS2Repository.findAllByGridIdIsNull();
    }

    @Transactional
    public void updateValveRanking(TeamCS2Entity team, Integer vrsPoints, Integer valveRating, LocalDate rankingDate) {
        if (vrsPoints == null) {
            log.error("Null vrsPoints for teamName={}, teamId={}, skipping update", team.getName(), team.getId());
            return;
        }

        if (valveRating == null) {
            log.error("Null valveRating for teamName={}, teamId={}, skipping update", team.getName(), team.getId());
            return;
        }

        if (vrsPoints < 0) {
            log.error("Negative vrsPoints={} for teamName={}, teamId={}, skipping update — check the parser", vrsPoints, team.getName(), team.getId());
            return;
        }

        if (valveRating < 1) {
            log.error("Invalid valveRating={} for teamName={}, teamId={}, skipping update — check the parser", valveRating, team.getName(), team.getId());
            return;
        }

        team.setVrsPoints(vrsPoints);
        team.setValveRating(valveRating);
        team.setLastValveRatingSyncedAt(rankingDate);
        teamCS2Repository.save(team);
    }
}
