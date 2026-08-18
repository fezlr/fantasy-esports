package com.fezlr.fantasy.cs2.pack.repository;

import com.fezlr.fantasy.cs2.pack.entity.PackCS2Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PackCS2Repository extends JpaRepository<PackCS2Entity, Long> {

    @Query("""
       SELECT p FROM PackCS2Entity p
       WHERE p.userCS2.user.telegramId = :telegramId
    """)
    List<PackCS2Entity> findAllByUserTelegramId(@Param("telegramId") Long telegramId);

}
