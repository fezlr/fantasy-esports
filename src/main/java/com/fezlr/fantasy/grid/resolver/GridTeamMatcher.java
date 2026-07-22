package com.fezlr.fantasy.grid.resolver;

import com.fezlr.fantasy.grid.dto.player_search.TeamRefDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GridTeamMatcher {

    public Optional<TeamRefDto> matchExact(List<TeamRefDto> candidates, String name) {
        return candidates.stream()
                .filter(p -> p.name().strip().equalsIgnoreCase(name.strip()))
                .findFirst();
    }
}
