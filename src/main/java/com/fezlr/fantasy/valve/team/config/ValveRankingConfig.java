package com.fezlr.fantasy.valve.team.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ValveRankingConfig {
    @Value("${github.valve-ranking-url}")
    private String gitHubUrl;

    @Bean
    public RestClient valveRankingRestClient() {
        return RestClient.builder()
                .baseUrl(gitHubUrl)
                .build();
    }
}