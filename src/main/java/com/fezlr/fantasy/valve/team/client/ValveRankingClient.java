package com.fezlr.fantasy.valve.team.client;

import com.fezlr.fantasy.valve.team.config.ValveRankingConfig;
import com.fezlr.fantasy.valve.team.dto.ValveGitHubFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ValveRankingClient {
    private final ValveRankingConfig valveRankingConfig;

    public List<ValveGitHubFileDto> fetchFiles(Integer year) {
        return valveRankingConfig.valveRankingRestClient()
                .get()
                .uri("/{year}", year)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public String fetchMarkdown(String downloadUrl) {
        return valveRankingConfig.valveRankingRestClient()
                .get()
                .uri(URI.create(downloadUrl))
                .retrieve()
                .body(String.class);
    }
}
