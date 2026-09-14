package com.fezlr.fantasy.pandascore.client;

import com.fezlr.fantasy.cs2.team.enums.TrackedTeam;
import com.fezlr.fantasy.pandascore.config.PandaScoreClientConfig;
import com.fezlr.fantasy.pandascore.dto.request.PandaScoreTeamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class PandaScoreClient {
    @Value("${pandascore.api-key}")
    private String apiKey;

    private final PandaScoreClientConfig pandaScoreClientConfig;

    public List<PandaScoreTeamRequest> fetchTrackedTeams() {
        String idsParam = TrackedTeam.getAllPandaScoreIds();

        log.info("idsParam: {}", idsParam);
        log.info("idsParam LENGTH: {}", idsParam.length());

        return pandaScoreClientConfig.pandaScoreRestClient()
                .get()
                .uri("/csgo/teams?filter[id]={idsParam}&per_page=100&token={apiKey}", idsParam, apiKey)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
