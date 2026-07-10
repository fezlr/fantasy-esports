package com.fezlr.fantasy.user.service;

import com.fezlr.fantasy.user.dto.request.TelegramUserData;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import com.fezlr.fantasy.user.entity.UserEntity;
import com.fezlr.fantasy.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;

@Validated
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserEntity getByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));
    }

    @Transactional
    public UserEntity createOrUpdate(@Valid TelegramUserData telegramUser) {
        return userRepository.findByTelegramId(telegramUser.telegramId())
                .map(existing -> {
                    existing.setTelegramUsername(telegramUser.telegramUsername());
                    existing.setLastActiveAt(Instant.now());

                    //save and return
                    return userRepository.save(existing);
                })
                .orElseGet(() -> {
                    UserEntity userToCreate = UserEntity.builder()
                    .telegramId(telegramUser.telegramId())
                    .telegramUsername(telegramUser.telegramUsername())
                    .lastActiveAt(Instant.now())
                    .build();

                    //save and return
                    return userRepository.save(userToCreate);
                });
    }
}
