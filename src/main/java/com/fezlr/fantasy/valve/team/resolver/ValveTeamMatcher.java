package com.fezlr.fantasy.valve.team.resolver;

import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ValveTeamMatcher {

    public Map<String, TeamCS2Entity> buildIndex(List<TeamCS2Entity> teams) {
        if (teams == null) {
            return Collections.emptyMap();
        }

        return teams.stream()
                .collect(Collectors.toMap(
                        t -> Optional.ofNullable(t.getValveName())
                                .orElseGet(t::getName)
                                .toLowerCase(),
                        t -> t, (existing, duplicate) -> existing));
    }

    public Optional<TeamCS2Entity> match(Map<String, TeamCS2Entity> index, String valveTeamName) {
        return Optional.ofNullable(index.get(valveTeamName.toLowerCase()));
    }
}
