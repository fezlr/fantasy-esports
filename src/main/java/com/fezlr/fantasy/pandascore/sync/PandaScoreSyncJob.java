package com.fezlr.fantasy.pandascore.sync;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.service.PlayerCS2Service;
import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import com.fezlr.fantasy.cs2.team.service.TeamCS2Service;
import com.fezlr.fantasy.pandascore.client.PandaScoreClient;
import com.fezlr.fantasy.pandascore.dto.PandaScorePlayerDto;
import com.fezlr.fantasy.pandascore.dto.PandaScoreTeamDto;
import com.fezlr.fantasy.pandascore.mapper.PandaScorePlayerCS2Mapper;
import com.fezlr.fantasy.pandascore.mapper.PandaScoreTeamCS2Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PandaScoreSyncJob {
    private final PandaScoreClient pandaScoreClient;
    private final PandaScoreTeamCS2Mapper pandaScoreTeamCS2Mapper;
    private final TeamCS2Service teamCS2Service;
    private final PlayerCS2Service playerCS2Service;
    private final PandaScorePlayerCS2Mapper pandaScorePlayerCS2Mapper;

    public void syncTeamsAndPlayers() {
        List<PandaScoreTeamDto> teams = pandaScoreClient.fetchTrackedTeams();
        for (PandaScoreTeamDto dto : teams) {
            TeamCS2Entity team = teamCS2Service.createOrUpdate(pandaScoreTeamCS2Mapper.toEntity(dto));
            for (PandaScorePlayerDto playerDto : dto.players()) {
                PlayerCS2Entity player = pandaScorePlayerCS2Mapper.toEntity(playerDto);
                player.setTeam(team);
                playerCS2Service.createOrUpdate(player);
            }
        }
    }
}