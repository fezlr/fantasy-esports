package com.fezlr.fantasy.telegram;

import com.fezlr.fantasy.telegram.handler.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UpdateRouter {
    private final Map<String, CommandHandler> handlers;

    public UpdateRouter(List<CommandHandler> commandHandlers) {
        this.handlers = commandHandlers.stream()
                .collect(Collectors.toMap(CommandHandler::command, h -> h));
    }

    public void route(Update update) {
        if (!isTextMessage(update)) {
           return;
        }

        String text = update.getMessage().getText();
        CommandHandler handler = handlers.get(text);

        if (handler != null) {
            handler.handle(update.getMessage());
        }
    }

    private boolean isTextMessage(Update update) {
        return update != null && update.hasMessage() && update.getMessage().hasText();
    }
}
