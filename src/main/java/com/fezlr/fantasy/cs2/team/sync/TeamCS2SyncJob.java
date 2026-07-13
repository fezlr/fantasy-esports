package com.fezlr.fantasy.cs2.team.sync;

import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import com.fezlr.fantasy.cs2.team.service.TeamCS2Service;
import com.fezlr.fantasy.pandascore.client.PandaScoreClient;
import com.fezlr.fantasy.pandascore.dto.PandaScoreTeamDto;
import com.fezlr.fantasy.pandascore.mapper.PandaScoreTeamCS2Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TeamCS2SyncJob {
    private final PandaScoreClient pandaScoreClient;
    private final PandaScoreTeamCS2Mapper pandaScoreTeamCS2Mapper;
    private final TeamCS2Service teamCS2Service;

    public void syncTeams() {
        log.info("Starting syncing teams INSTANT={}", Instant.now());
        List<PandaScoreTeamDto> teams = pandaScoreClient.fetchAllTeams();

        int updated = 0;
        for (PandaScoreTeamDto dto : teams) {
            try {
                TeamCS2Entity entity = pandaScoreTeamCS2Mapper.toEntity(dto);
                teamCS2Service.createOrUpdate(entity);
                updated++;
            } catch (Exception e) {
                log.error("Failed to sync team sourceId={}", dto.sourceId(), e);
            }
        }
        log.info("Ending syncing teams, UPDATED={}/{}, INSTANT={}", updated, teams.size(), Instant.now());
    }

}
