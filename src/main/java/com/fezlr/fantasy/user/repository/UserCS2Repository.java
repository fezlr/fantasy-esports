package com.fezlr.fantasy.user.repository;

import com.fezlr.fantasy.user.entity.UserCS2Entity;
import com.fezlr.fantasy.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserCS2Repository extends JpaRepository<UserCS2Entity, Long> {

    @Modifying
    @Query("""
            UPDATE UserCS2Entity ucs
            SET ucs.fantasyPoints = ucs.fantasyPoints - :amount
            WHERE ucs.user.id = :userId AND ucs.fantasyPoints >= :amount
            """)
    int subtractFantasyPoints(@Param("userId") Long userId, @Param("amount") long cost);

    Optional<UserCS2Entity> findByUser(UserEntity userEntity);
}
