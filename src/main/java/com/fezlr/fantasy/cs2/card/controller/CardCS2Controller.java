package com.fezlr.fantasy.cs2.card.controller;

import com.fezlr.fantasy.cs2.card.dto.response.CardCS2Response;
import com.fezlr.fantasy.cs2.card.service.CardCS2Service;
import com.fezlr.fantasy.telegram.security.InitDataValidator;
import com.fezlr.fantasy.user.dto.request.TelegramUserData;
import com.fezlr.fantasy.user.entity.UserEntity;
import com.fezlr.fantasy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/cs2/cards")
@Controller
public class CardCS2Controller {
    private final InitDataValidator initDataValidator;
    private final CardCS2Service cardCS2Service;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<CardCS2Response>> getAllCardsByCurrentUser(@RequestHeader(value = "X-Telegram-Init-Data") String initData) {
        TelegramUserData telegramUser = initDataValidator.validateAndExtractUser(initData);
        UserEntity user = userService.createOrUpdate(telegramUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cardCS2Service.findAllCardsByTelegramId(user.getTelegramId()));
    }
}
