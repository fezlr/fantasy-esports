package com.fezlr.fantasy.cs2.player.service;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.repository.PlayerCS2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class PlayerCS2Service {
    private final PlayerCS2Repository playerCS2Repository;

    @Transactional
    public PlayerCS2Entity createOrUpdate(PlayerCS2Entity newPlayerInfoEntity) {
        return playerCS2Repository.findBySourceId(newPlayerInfoEntity.getSourceId())
                .map(existing -> {
                    existing.setFirstName(newPlayerInfoEntity.getFirstName());
                    existing.setLastName(newPlayerInfoEntity.getLastName());
                    existing.setNickname(newPlayerInfoEntity.getNickname());
                    existing.setImageUrl(newPlayerInfoEntity.getImageUrl());
                    existing.setTeam(newPlayerInfoEntity.getTeam());
                    existing.setActive(newPlayerInfoEntity.isActive());
                    existing.setLastSyncedAt(Instant.now());

                    return playerCS2Repository.save(existing);
                })
                .orElseGet(() -> {
                    newPlayerInfoEntity.setLastSyncedAt(Instant.now());
                    return playerCS2Repository.save(newPlayerInfoEntity);
                });
    }
}