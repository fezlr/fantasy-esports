package com.fezlr.fantasy.user.dto.response;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Builder
public record UserResponse(
        @NotNull
        Long telegramId,

        String telegramUsername,

        @NotNull
        Instant createdAt,

        @NotNull
        Instant lastActiveAt
) {
}
