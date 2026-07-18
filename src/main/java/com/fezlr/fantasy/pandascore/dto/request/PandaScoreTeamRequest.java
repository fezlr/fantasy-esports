package com.fezlr.fantasy.pandascore.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PandaScoreTeamRequest(
        @JsonProperty("id") Long pandaScoreId,

        @JsonProperty("location") String location,

        @JsonProperty("name") String name,

        @JsonProperty("acronym") String acronym,

        @JsonProperty("image_url") String logoUrl,

        @JsonProperty("players") List<PandaScorePlayerRequest> players
) {
}
