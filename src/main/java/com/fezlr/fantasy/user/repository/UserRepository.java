package com.fezlr.fantasy.user.repository;

import com.fezlr.fantasy.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByTelegramId(Long telegramId);

}
