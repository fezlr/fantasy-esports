package com.fezlr.fantasy.pandascore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PandaScoreTeamDto(
        @JsonProperty("id") Long sourceId,

        @JsonProperty("location") String location,

        @JsonProperty("name") String name,

        @JsonProperty("acronym") String acronym,

        //disabled in mapper
        @JsonProperty("image_url") String imageUrl,

        @JsonProperty("players") List<PandaScorePlayerDto> players
) {
}
