package com.fezlr.fantasy.grid.dto.series_state;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlayerGameStateDto(
        @JsonProperty("id") Long steamId,
        Integer kills,
        Integer killAssistsGiven,
        Integer killAssistsReceived,
        Integer deaths,
        Integer damageDealt,
        Integer damageTaken,
        Integer headshots
) {
}
