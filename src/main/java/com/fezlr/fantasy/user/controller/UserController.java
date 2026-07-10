package com.fezlr.fantasy.user.controller;

import com.fezlr.fantasy.telegram.security.InitDataValidator;
import com.fezlr.fantasy.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final InitDataValidator initDataValidator;

//    public UserResponse getCurrentUser(@RequestHeader("X-Telegram-Init-Data") String initData) {
//
//    }
}
