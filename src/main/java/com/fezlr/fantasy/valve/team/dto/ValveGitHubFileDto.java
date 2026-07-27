package com.fezlr.fantasy.valve.team.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ValveGitHubFileDto(
        String name,

        String path,

        @JsonProperty("download_url") String downloadUrl
) {
}
