package com.fezlr.fantasy.telegram.handler.impl;

import com.fezlr.fantasy.telegram.handler.CommandHandler;
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

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public void handle(Message message) {
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
