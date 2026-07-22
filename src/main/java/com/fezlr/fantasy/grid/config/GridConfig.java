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

    @Value("${grid.base-url}")
    private String baseUrl;

    @Value("${grid.stats-url}")
    private String statsUrl;

    @Value("${grid.live-url}")
    private String liveUrl;



    @Bean(name = "baseGraphQlClient")
    public HttpGraphQlClient baseGraphQlClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .build();

        return HttpGraphQlClient.builder(webClient).build();
    }

    @Bean(name = "statsGraphQlClient")
    public HttpGraphQlClient statsGraphQlClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl(statsUrl)
                .defaultHeader("x-api-key", apiKey)
                .build();

        return HttpGraphQlClient.builder(webClient).build();
    }

    @Bean(name = "liveGraphQlClient")
    public HttpGraphQlClient liveGraphQlClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl(liveUrl)
                .defaultHeader("x-api-key", apiKey)
                .build();

        return HttpGraphQlClient.builder(webClient).build();
    }
}
