package com.fezlr.fantasy.user.controller;

import com.fezlr.fantasy.telegram.security.InitDataValidator;
import com.fezlr.fantasy.user.dto.request.TelegramUserData;
import com.fezlr.fantasy.user.dto.response.UserFantasyPointsResponse;
import com.fezlr.fantasy.user.entity.UserEntity;
import com.fezlr.fantasy.user.service.UserCS2Service;
import com.fezlr.fantasy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/cs2/users")
@RestController
public class UserCS2Controller {
    private final InitDataValidator initDataValidator;
    private final UserService userService;
    private final UserCS2Service userCS2Service;

    @GetMapping("/me/points")
    public ResponseEntity<UserFantasyPointsResponse> getCurrentUserFantasyPoints(@RequestHeader(value = "X-Telegram-Init-Data") String initData) {
        TelegramUserData telegramUserData = initDataValidator.validateAndExtractUser(initData);
        UserEntity currentUser = userService.findByTelegramId(telegramUserData.telegramId());

        return ResponseEntity
                .ok(userCS2Service.findFantasyPointsByUser(currentUser));
    }
}
