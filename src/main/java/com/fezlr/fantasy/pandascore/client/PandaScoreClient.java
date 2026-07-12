package com.fezlr.fantasy.pandascore.client;

import com.fezlr.fantasy.pandascore.dto.PandaScorePlayerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PandaScoreClient {
    private final RestClient pandaScoreRestClient;

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

    private List<PandaScorePlayerDto> fetchPlayersPage(int page) {
        return pandaScoreRestClient.get()
                .uri("/csgo/players?per_page=100&page={page}", page)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
