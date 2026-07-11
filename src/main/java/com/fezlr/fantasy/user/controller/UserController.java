package com.fezlr.fantasy.user.controller;

import com.fezlr.fantasy.telegram.security.InitDataValidator;
import com.fezlr.fantasy.user.dto.request.TelegramUserData;
import com.fezlr.fantasy.user.dto.response.UserResponse;
import com.fezlr.fantasy.user.entity.UserEntity;
import com.fezlr.fantasy.user.mapper.UserMapper;
import com.fezlr.fantasy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {
    private final InitDataValidator initDataValidator;
    private final UserMapper userMapper;
    private final UserService userService;

    //TODO: remove required = false in prod!
    @GetMapping("/me")
    public UserResponse getCurrentUser(
            @RequestHeader(value = "X-Telegram-Init-Data"
//                    , required = false
            )
            String initData) {

//        //TODO: remove if-statement in prod!
//        if (initData == null) {
//            return userMapper.toResponse(userService.createOrUpdate(new TelegramUserData(123456789L, "test")));
//        }

        TelegramUserData telegramUser = initDataValidator.validateAndExtractUser(initData);
        UserEntity userEntity = userService.createOrUpdate(telegramUser);
        return userMapper.toResponse(userEntity);
    }
}
