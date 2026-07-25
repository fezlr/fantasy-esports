package com.fezlr.fantasy.grid.client;

import com.fezlr.fantasy.grid.dto.player_search.PlayerSearchDto;
import com.fezlr.fantasy.grid.dto.player_search.TeamRefDto;
import com.fezlr.fantasy.grid.dto.series_state.SeriesStateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import com.fezlr.fantasy.grid.dto.series.SeriesSummaryDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.ClientGraphQlResponse;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class GridClient {
    private final HttpGraphQlClient baseGraphQlClient;
    private final HttpGraphQlClient liveGraphQlClient;
    private final GridRateLimiter gridRateLimiter;

    public List<PlayerSearchDto> findPlayerByNickname(String nickname) {
        try {
            gridRateLimiter.acquire();
            return baseGraphQlClient
                    .documentName("FindPlayerByNickname")
                    .variable("nickname", nickname.strip())
                    .retrieveSync("players.edges")
                    .toEntityList(PlayerEdgeWrapper.class)
                    .stream()
                    .map(PlayerEdgeWrapper::node)
                    .toList();
        } catch (Exception e) {
            log.error("GRID failed for nickname = {}: {}", nickname, e.getMessage());
            return List.of();
        }
    }

    public List<TeamRefDto> findTeamCS2ByName(String teamName) {
        try {
            gridRateLimiter.acquire();
            return baseGraphQlClient
                    .documentName("FindTeamCS2ByName")
                    .variable("teamName", teamName.strip())
                    .retrieveSync("teams.edges")
                    .toEntityList(TeamEdgeWrapper.class)
                    .stream()
                    .map(TeamEdgeWrapper::node)
                    .toList();
        } catch (Exception e) {
            log.error("GRID failed for teamName = {}: {}", teamName, e.getMessage());
            return List.of();
        }
    }

    public List<SeriesSummaryDto> fetchSeriesForTeam(Long teamGridId, Instant since, Instant until) {
        try {
            gridRateLimiter.acquire();
            return baseGraphQlClient
                    .documentName("SeriesForTeam")
                    .variable("teamId", teamGridId)
                    .variable("since", since.toString())
                    .variable("until", until.toString())
                    .retrieveSync("allSeries.edges")
                    .toEntityList(SeriesEdgeWrapper.class)
                    .stream()
                    .map(SeriesEdgeWrapper::node)
                    .toList();
        } catch (Exception e) {
            log.error("GRID failed for teamGridId = {}: {}", teamGridId, e.getMessage());
            return List.of();
        }
    }

    public Map<Long, SeriesStateDto> fetchSeriesStates(List<Long> seriesIds) {
        if (seriesIds.isEmpty()) {
            return Map.of();
        }

        StringBuilder query = new StringBuilder("query BatchSeriesState {\n");
        Map<String, Long> aliasToSeriesId = new LinkedHashMap<>();

        for (int i = 0; i < seriesIds.size(); i++) {
            String alias = "s" + i;
            aliasToSeriesId.put(alias, seriesIds.get(i));
            query.append("  ").append(alias)
                    .append(": seriesState(id: \"").append(seriesIds.get(i)).append("\") {\n")
                    .append("    games {\n")
                    .append("      teams {\n")
                    .append("        players {\n")
                    .append("          id\n")
                    .append("          kills\n")
                    .append("          killAssistsGiven\n")
                    .append("          deaths\n")
                    .append("          teamkills\n")
                    .append("          selfkills\n")

                    .append("          ... on GamePlayerStateCs2 {\n")
                    .append("            name\n")
                    .append("            damageDealt\n")
                    .append("            damageTaken\n")
                    .append("            headshots\n")
                    .append("          }\n")
                    .append("        }\n")
                    .append("      }\n")
                    .append("    }\n")
                    .append("  }\n");
        }
        query.append("}");

        try {
            gridRateLimiter.acquire();
            ClientGraphQlResponse response = liveGraphQlClient.document(query.toString()).executeSync();

            if (!response.isValid()) {
                log.error("GRID batch seriesState failed: {}", response.getErrors());
                return Map.of();
            }

            Map<String, SeriesStateDto> byAlias = response.toEntity(
                    new ParameterizedTypeReference<>() {}
            );

            Map<Long, SeriesStateDto> bySeriesId = new LinkedHashMap<>();
            byAlias.forEach((alias, state) -> {
                Long seriesId = aliasToSeriesId.get(alias);
                if (seriesId != null) {
                    bySeriesId.put(seriesId, state);
                }
            });

            return bySeriesId;
        } catch (Exception e) {
            log.error("GRID batch seriesState failed for {} series: {}", seriesIds.size(), e.getMessage());
            return Map.of();
        }
    }

    private record PlayerEdgeWrapper(PlayerSearchDto node) {}

    private record TeamEdgeWrapper(TeamRefDto node) {}

    private record SeriesEdgeWrapper(SeriesSummaryDto node) {}
}