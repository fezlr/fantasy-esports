package com.fezlr.fantasy.grid.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GridConfig {
    @Value("${grid.api-key}")
    private String apiKey;

    @Value("${grid.stats-url}")
    private String statsUrl;

    @Bean
    public HttpGraphQlClient gridGraphQlClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl(statsUrl)
                .defaultHeader("x-api-key", apiKey)
                .build();

        return HttpGraphQlClient.builder(webClient).build();
    }
}
