package com.fezlr.fantasy.cs2.team.service;

import com.fezlr.fantasy.common.exception.SourceIdNotFound;
import com.fezlr.fantasy.cs2.team.dto.response.TeamCS2Response;
import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import com.fezlr.fantasy.cs2.team.mapper.TeamCS2Mapper;
import com.fezlr.fantasy.cs2.team.repository.TeamCS2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TeamCS2Service {
    private final TeamCS2Repository teamCS2Repository;
    private final TeamCS2Mapper teamCS2Mapper;

    public TeamCS2Entity getBySourceId(Long sourceId) {
        return teamCS2Repository.findByPandaScoreId(sourceId)
                .orElseThrow(() -> new SourceIdNotFound("Source id is not found"));
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

    public List<TeamCS2Response> findAllTeams() {
        return teamCS2Repository.findAll().stream()
                .map(teamCS2Mapper::toResponse).toList();
    }
}
