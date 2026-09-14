package com.fezlr.fantasy.cs2.pack.controller;

import com.fezlr.fantasy.cs2.pack.dto.response.PackCS2Response;
import com.fezlr.fantasy.cs2.pack.dto.response.PackPurchaseCS2Response;
import com.fezlr.fantasy.cs2.pack.service.PackCS2Service;
import com.fezlr.fantasy.telegram.security.InitDataValidator;
import com.fezlr.fantasy.user.dto.request.TelegramUserData;
import com.fezlr.fantasy.user.entity.UserEntity;
import com.fezlr.fantasy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/cs2/packs")
@Controller
public class PackCS2Controller {
    private final InitDataValidator initDataValidator;
    private final PackCS2Service packCS2Service;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<PackCS2Response>> getAllPacksByCurrentUser(@RequestHeader(value = "X-Telegram-Init-Data") String initData) {
        TelegramUserData telegramUserData = initDataValidator.validateAndExtractUser(initData);
        UserEntity currentUser = userService.findByTelegramId(telegramUserData.telegramId());

        return ResponseEntity
                .ok(packCS2Service.findAllPacksByTelegramId(currentUser.getTelegramId()));
    }


    @PostMapping("/purchase/{shopPackId}")
    public ResponseEntity<PackPurchaseCS2Response> purchasePackByCurrentUser(@RequestHeader(value = "X-Telegram-Init-Data") String initData, @PathVariable("shopPackId") Long shopPackId) {
        TelegramUserData telegramUserData = initDataValidator.validateAndExtractUser(initData);
        UserEntity currentUser = userService.findByTelegramId(telegramUserData.telegramId());

        return ResponseEntity
                .ok(packCS2Service.purchasePackByUser(currentUser, shopPackId));
    }
}
