package com.fezlr.fantasy.common.exception.dto;

import lombok.Builder;

@Builder
public record ErrorResponseDto(
        int status,

        String error,

        String message,

        String timestamp
) {
}
