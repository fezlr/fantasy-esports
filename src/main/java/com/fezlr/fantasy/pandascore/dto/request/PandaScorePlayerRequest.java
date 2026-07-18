package com.fezlr.fantasy.pandascore.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PandaScorePlayerRequest(
        //pandascore id
        @JsonProperty("id") Long pandaScoreId,

        @JsonProperty("name") String nickname,

        @JsonProperty("first_name") String firstName,

        @JsonProperty("last_name") String lastName,

        //disabled in mapper
        @JsonProperty("image_url") String imageUrl,

        //not in entity
        @JsonProperty("birthday") String birthDay,

        //team_id in pandascore
        @JsonProperty("team_id") Long teamId,

        @JsonProperty("active") boolean isActive
) {
}
