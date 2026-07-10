package com.fezlr.fantasy.user.mapper;

import com.fezlr.fantasy.user.dto.response.UserResponse;
import com.fezlr.fantasy.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(UserEntity userEntity) {
        return UserResponse.builder()
                .telegramId(userEntity.getTelegramId())
                .telegramUsername(userEntity.getTelegramUsername())
                .createdAt(userEntity.getCreatedAt())
                .lastActiveAt(userEntity.getLastActiveAt())
                .build();
    }
}
