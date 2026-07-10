package com.fezlr.fantasy.user.dto.response;

import java.time.LocalDateTime;

public record UserResponse(

        Long telegramId,

        String telegramUsername,

        LocalDateTime createdAt,

        LocalDateTime lastActiveAt
) {
}
