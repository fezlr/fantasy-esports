package com.fezlr.fantasy.user.service;

import com.fezlr.fantasy.user.dto.request.TelegramUserData;
import com.fezlr.fantasy.user.entity.UserCS2Entity;
import com.fezlr.fantasy.user.enums.Role;
import com.fezlr.fantasy.user.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import com.fezlr.fantasy.user.entity.UserEntity;
import com.fezlr.fantasy.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.ArrayList;

@Validated
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDetailsImpl loadUserByTelegramId(Long telegramId) {
        UserEntity entity = userRepository
                .findByTelegramId(telegramId)
                .orElseThrow(() -> new EntityNotFoundException("Telegram id is not found"));

        return new UserDetailsImpl(entity);
    }

    @Transactional
    public UserEntity createOrUpdate(@Valid TelegramUserData telegramUser) {
        return userRepository
                .findByTelegramId(telegramUser.telegramId())
                .map(existing -> {
                    existing.setTelegramUsername(telegramUser.telegramUsername());
                    existing.setLastActiveAt(Instant.now());

                    return userRepository.save(existing);
                })
                .orElseGet(() -> {
                    UserCS2Entity userCS2ToCreate = UserCS2Entity.builder()
                            .packsCS2(new ArrayList<>())
                            .fantasyPoints(0)
                            .build();

                    UserEntity userToCreate = UserEntity.builder()
                            .telegramId(telegramUser.telegramId())
                            .telegramUsername(telegramUser.telegramUsername())
                            .role(Role.USER)
                            .userCS2Entity(userCS2ToCreate)
                            .isEnabled(true)
                            .lastActiveAt(Instant.now())
                            .build();

                    userCS2ToCreate.setUser(userToCreate);

                    return userRepository.save(userToCreate);
                });
    }

    public UserEntity findByTelegramId(Long telegramId) {
        return userRepository
                .findByTelegramId(telegramId)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));
    }

}
