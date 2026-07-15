package com.fezlr.fantasy.pandascore.client;

import com.fezlr.fantasy.pandascore.config.PandaScoreClientConfig;
import com.fezlr.fantasy.pandascore.dto.PandaScoreTeamDto;
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
    @Value("${pandascore.tracked-team-ids}")
    private List<Long> trackedTeamIds;
    private final PandaScoreClientConfig pandaScoreClientConfig;

    public List<PandaScoreTeamDto> fetchTrackedTeams() {
        String idsParam = trackedTeamIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        log.info("TrackedTeamIds: {}", trackedTeamIds);
        log.info("idsParam: {}", idsParam);
        log.info("TrackedTeamIds SIZE: {}", trackedTeamIds.size());
        log.info("idsParam LENGTH: {}", idsParam.length());

        return pandaScoreClientConfig.pandaScoreRestClient().get()
                .uri("/csgo/teams?filter[id]={ids}&per_page=100&token={apiKey}", idsParam, apiKey)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});


    }
}
