package com.fezlr.fantasy.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface CommandHandler {
    String command();

    void handle(Message message);
}
