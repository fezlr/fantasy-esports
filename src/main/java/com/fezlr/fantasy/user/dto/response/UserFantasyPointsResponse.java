package com.fezlr.fantasy.user.dto.response;

import lombok.Builder;

@Builder
public record UserFantasyPointsResponse(
        long fantasyPoints
) {
}
