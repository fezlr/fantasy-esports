package com.fezlr.fantasy.user.dto.request;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record TelegramUserData(
        @NotNull
        Long telegramId,

        String telegramUsername
) {
}
