package com.fezlr.fantasy.cs2.player.service;

import com.fezlr.fantasy.cs2.player.dto.response.PlayerCS2Response;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.mapper.PlayerCS2Mapper;
import com.fezlr.fantasy.cs2.player.repository.PlayerCS2Repository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerCS2Service {
    private final PlayerCS2Repository playerCS2Repository;
    private final PlayerCS2Mapper playerCS2Mapper;

    public PlayerCS2Entity findById(Long playerId) {
        return playerCS2Repository.findById(playerId).orElseThrow(() -> new EntityNotFoundException("Player is not found"));
    }

    public List<PlayerCS2Response> findAllPlayers() {
        return playerCS2Repository.findAll().stream()
                .map(playerCS2Mapper::toResponse).toList();
    }

    public List<PlayerCS2Entity> findAllBySteamIdIsNotNull() {
        return playerCS2Repository.findAllBySteamIdIsNotNull();
    }

    public List<PlayerCS2Entity> findAllWithoutGridId() {
        return playerCS2Repository.findAllByGridIdIsNull();
    }

    @Transactional
    public void assignGridIdAndSetSteamId(Long id, Long gridId, Long steamId) {
        PlayerCS2Entity entity = playerCS2Repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Player is not found"));

        if (entity.getGridId() == null) {
            entity.setGridId(gridId);
        }

        if (entity.getSteamId() == null && steamId != null) {
            entity.setSteamId(steamId);
        }

        playerCS2Repository.save(entity);

        log.info("Player updated: playerId={}, nickname={}, gridId={}, steamId={}", id, entity.getNickname(), entity.getGridId(), entity.getSteamId());
    }

    @Transactional
    public PlayerCS2Entity createOrUpdate(PlayerCS2Entity newPlayerInfoEntity) {
        return playerCS2Repository.findByPandaScoreId(newPlayerInfoEntity.getPandaScoreId())
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

    @Transactional
    public void save(PlayerCS2Entity entity) {
        playerCS2Repository.save(entity);
    }
}