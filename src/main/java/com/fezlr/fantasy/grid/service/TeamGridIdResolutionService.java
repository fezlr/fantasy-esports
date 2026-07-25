package com.fezlr.fantasy.grid.service;

import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import com.fezlr.fantasy.cs2.team.service.TeamCS2Service;
import com.fezlr.fantasy.grid.client.GridClient;
import com.fezlr.fantasy.grid.dto.player_search.TeamRefDto;
import com.fezlr.fantasy.grid.resolver.GridTeamMatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamGridIdResolutionService {
    private final GridClient gridClient;
    private final GridTeamMatcher gridTeamMatcher;
    private final TeamCS2Service teamCS2Service;

    /*
     * Finding team CS2 and setting grid id
     */
    public Optional<Long> resolveOne(Long teamId) {

        TeamCS2Entity team = teamCS2Service.findById(teamId);
        List<TeamRefDto> candidates = gridClient.findTeamCS2ByName(team.getName());
        Optional<TeamRefDto> match = gridTeamMatcher.matchExact(candidates, team.getName());

        match.ifPresent(dto ->
                teamCS2Service.assignGridIdIfMissing(team.getId(), dto.id()));

        return match.map(TeamRefDto::id);
    }

    /*
     * Finding list of teams CS2 and setting for them grid id
     */
    public void resolveAllMissing() {
        List<TeamCS2Entity> teams = teamCS2Service.findAllWithoutGridId();

        for (TeamCS2Entity team : teams) {
            try {
                resolveOne(team.getId());
            } catch (Exception e) {
                log.error("Failed resolving gridId for playerId={} nickname={}: {}",
                        team.getId(), team.getName(), e.getMessage());
            }
        }
    }
}
