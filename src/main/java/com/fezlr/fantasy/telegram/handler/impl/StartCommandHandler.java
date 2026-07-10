package com.fezlr.fantasy.telegram.handler.impl;

import com.fezlr.fantasy.telegram.handler.CommandHandler;
import com.fezlr.fantasy.user.dto.request.TelegramUserData;
import com.fezlr.fantasy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {
    private final TelegramClient telegramClient;
    private final UserService userService;

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public void handle(Message message) {
        //save or update user's data (last_active_at)
        userService.createOrUpdate(TelegramUserData.builder()
                .telegramId(message.getFrom().getId())
                .telegramUsername(message.getFrom().getUserName())
                .build());

        //create message
        SendMessage sendMessage = SendMessage.builder()
                .chatId(message.getChatId())
                .text("darova")
                .build();

        //send message
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
