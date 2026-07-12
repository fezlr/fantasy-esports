package com.fezlr.fantasy.pandascore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PandaScoreClientConfig {
    @Value("${pandascore.base-url}")
    private String baseUrl;

    @Value("${pandascore.api-key}")
    private String apiKey;

    public RestClient pandaScoreRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
}
