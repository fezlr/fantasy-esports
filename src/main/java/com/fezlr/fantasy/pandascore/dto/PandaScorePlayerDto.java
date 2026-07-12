package com.fezlr.fantasy.pandascore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PandaScorePlayerDto(
        //pandascore id
        @JsonProperty("id") Long sourceId,

        @JsonProperty("name") String nickname,

        @JsonProperty("first_name") String firstName,

        @JsonProperty("last_name") String lastName,

        @JsonProperty("image_url") String imageUrl,

        //not in entity
        @JsonProperty("birthday") String birthDay,

        //team_id in pandascore
        @JsonProperty("team_id") String teamId,

        @JsonProperty("active") boolean isActive
) {
}
