package com.fezlr.fantasy.pandascore.client;

import com.fezlr.fantasy.pandascore.config.PandaScoreClientConfig;
import com.fezlr.fantasy.pandascore.dto.PandaScorePlayerDto;
import com.fezlr.fantasy.pandascore.dto.PandaScoreTeamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PandaScoreClient {
    @Value("${pandascore.api-key}")
    private String apiKey;
    private final PandaScoreClientConfig pandaScoreClientConfig;

    public List<PandaScorePlayerDto> fetchAllPlayers() {
        List<PandaScorePlayerDto> players = new ArrayList<>();
        int page = 1;

        while (true) {
            List<PandaScorePlayerDto> pageResult = fetchPlayersPage(page);

            if (pageResult.isEmpty()) {
                break;
            }

            players.addAll(pageResult);
            page++;
        }

        return players;
    }

    public List<PandaScoreTeamDto> fetchAllTeams() {
        List<PandaScoreTeamDto> teams = new ArrayList<>();
        int page = 1;

        while (true) {
            List<PandaScoreTeamDto> pageResult = fetchTeamsPage(page);

            if (pageResult.isEmpty()) {
                break;
            }

            teams.addAll(pageResult);
            page++;
        }

        return teams;
    }

    private List<PandaScorePlayerDto> fetchPlayersPage(int page) {
        return pandaScoreClientConfig.pandaScoreRestClient().get()
                .uri("/csgo/players?per_page=100&page={page}&token={token}", page, apiKey)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    private List<PandaScoreTeamDto> fetchTeamsPage(int page) {
        return pandaScoreClientConfig.pandaScoreRestClient().get()
                .uri("/csgo/teams?per_page=100&page={page}&token={token}", page, apiKey)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
