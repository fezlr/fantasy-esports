package com.fezlr.fantasy.cs2.player.service;

import com.fezlr.fantasy.cs2.player.dto.response.PlayerCS2Response;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.mapper.PlayerCS2Mapper;
import com.fezlr.fantasy.cs2.player.repository.PlayerCS2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PlayerCS2Service {
    private final PlayerCS2Repository playerCS2Repository;
    private final PlayerCS2Mapper playerCS2Mapper;

    @Transactional
    public PlayerCS2Entity createOrUpdate(PlayerCS2Entity newPlayerInfoEntity) {
        return playerCS2Repository.findBySourceId(newPlayerInfoEntity.getSourceId())
                .map(existing -> {
                    existing.setFirstName(newPlayerInfoEntity.getFirstName());
                    existing.setLastName(newPlayerInfoEntity.getLastName());
                    existing.setNickname(newPlayerInfoEntity.getNickname());
                    existing.setImageUrl(newPlayerInfoEntity.getImageUrl());

                    if (newPlayerInfoEntity.getTeam() != null) {
                        existing.setTeam(newPlayerInfoEntity.getTeam());
                    }

                    existing.setActive(newPlayerInfoEntity.isActive());
                    existing.setLastSyncedAt(Instant.now());

                    return playerCS2Repository.save(existing);
                })
                .orElseGet(() -> {
                    newPlayerInfoEntity.setLastSyncedAt(Instant.now());
                    return playerCS2Repository.save(newPlayerInfoEntity);
                });
    }

    public List<PlayerCS2Response> findAllPlayers() {
        return playerCS2Repository.findAll().stream()
                .map(playerCS2Mapper::toResponse).toList();
    }
}