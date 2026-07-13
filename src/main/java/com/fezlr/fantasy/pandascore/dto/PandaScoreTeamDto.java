package com.fezlr.fantasy.pandascore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PandaScoreTeamDto(
        //pandascore id
        //not in entity
        @JsonProperty("id") Long sourceId,

        //not in entity
        @JsonProperty("location") String location,

        @JsonProperty("name") String name
) {
}
