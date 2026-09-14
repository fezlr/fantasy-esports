package com.fezlr.fantasy.common.exception.dto;

import lombok.Builder;
import java.time.Instant;

@Builder
public record ErrorResponseDto(
        int status,

        String error,

        String message,

        Instant timestamp
) {
}
