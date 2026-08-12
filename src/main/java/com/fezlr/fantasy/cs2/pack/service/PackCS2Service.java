package com.fezlr.fantasy.cs2.pack.service;

import com.fezlr.fantasy.cs2.card.repository.CardCS2Repository;
import com.fezlr.fantasy.cs2.pack.entity.PackCS2Entity;
import com.fezlr.fantasy.cs2.pack.mapper.PackCS2Mapper;
import com.fezlr.fantasy.cs2.pack.repository.PackCS2Repository;
import jakarta.persistence.Entity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Entity
public class PackCS2Service {
    private final PackCS2Repository packCS2Repository;
    private final PackCS2Mapper packCS2Mapper;

    public List<PackCS2Entity> findAllPacksByTelegramId(Long telegramId) {
        return packCS2Repository.findAllByUserTelegramId(telegramId).stream()
                    .map(packCS2Mapper::toResponse).toList();
    }

}
