package com.fezlr.fantasy.user.controller;

import com.fezlr.fantasy.telegram.security.InitDataValidator;
import com.fezlr.fantasy.user.dto.response.UserResponse;
import com.fezlr.fantasy.user.entity.UserEntity;
import com.fezlr.fantasy.user.mapper.UserMapper;
import com.fezlr.fantasy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Controller
public class UserController {
    private final InitDataValidator initDataValidator;
    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getCurrentUser(@RequestHeader("X-Telegram-Init-Data") String initData) {
        Long telegramId = initDataValidator.validateAndExtractUserId(initData);
        UserEntity userEntity = userService.getByTelegramId(telegramId);
        return userMapper.toResponse(userEntity);
    }
}
