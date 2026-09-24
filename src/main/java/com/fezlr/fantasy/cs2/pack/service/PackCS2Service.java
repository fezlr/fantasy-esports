package com.fezlr.fantasy.cs2.pack.service;

import com.fezlr.fantasy.common.exception.AccessDeniedException;
import com.fezlr.fantasy.common.exception.NotEnoughFantasyPointsException;
import com.fezlr.fantasy.common.exception.PackShopIsNotAvailableException;
import com.fezlr.fantasy.cs2.card.entity.CardCS2Entity;
import com.fezlr.fantasy.cs2.card.enums.Rarity;
import com.fezlr.fantasy.cs2.card.service.CardCS2Service;
import com.fezlr.fantasy.cs2.pack.dto.response.InventoryPackCardCS2Response;
import com.fezlr.fantasy.cs2.pack.dto.response.PackCS2Response;
import com.fezlr.fantasy.cs2.pack.dto.response.PackPurchaseCS2Response;
import com.fezlr.fantasy.cs2.pack.entity.PackCS2Entity;
import com.fezlr.fantasy.cs2.pack.mapper.PackCS2Mapper;
import com.fezlr.fantasy.cs2.pack.repository.PackCS2Repository;
import com.fezlr.fantasy.cs2.packshop.entity.PackShopCS2Entity;
import com.fezlr.fantasy.cs2.packshop.service.PackShopCS2Service;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.service.PlayerCS2Service;
import com.fezlr.fantasy.user.entity.UserCS2Entity;
import com.fezlr.fantasy.user.entity.UserEntity;
import com.fezlr.fantasy.user.service.UserCS2Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PackCS2Service {
    private final PackShopCS2Service packShopCS2Service;
    private final PackRarityRollerService packRarityRollerService;
    private final PlayerCS2Service playerCS2Service;
    private final CardCS2Service cardCS2Service;
    private final PackCS2Repository packCS2Repository;
    private final PackCS2Mapper packCS2Mapper;
    private final UserCS2Service userCS2Service;

    public List<PackCS2Response> findAllPacksByTelegramId(Long telegramId) {
        return packCS2Repository
                .findAllByUserTelegramId(telegramId)
                .stream()
                .map(packCS2Mapper::toResponse).toList();
    }

    @Transactional
    public PackPurchaseCS2Response purchasePackByUser(UserEntity currentUser, Long shopPackId) {
        if (!currentUser.isEnabled()) {
            throw new AccessDeniedException("User is not enabled");
        }

        PackShopCS2Entity shopPack = packShopCS2Service.findById(shopPackId);

        if (!shopPack.isAvailable()) {
            throw new PackShopIsNotAvailableException("Pack is not available");
        }

        if (shopPack.getCost() > currentUser.getUserCS2Entity().getFantasyPoints()) {
            throw new NotEnoughFantasyPointsException("Not enough fantasy points");
        }

        PackCS2Entity pack = PackCS2Entity.builder()
                .userCS2(currentUser.getUserCS2Entity())
                .packShopCS2(shopPack)
                .receivedAt(Instant.now())
                .build();

        int updatedRows = userCS2Service.subtractFantasyPoints(currentUser.getId(), shopPack.getCost());

        if (updatedRows == 0) {
            throw new NotEnoughFantasyPointsException("Nothing was updated");
        }

        UserCS2Entity userCS2Entity = currentUser.getUserCS2Entity();

        userCS2Entity.getPacksCS2().add(pack);

        return packCS2Mapper.toPurchaseResponse(packCS2Repository.save(pack), userCS2Entity.getFantasyPoints() - shopPack.getCost());
    }

    @Transactional
    public InventoryPackCardCS2Response openPackByUser(UserEntity currentUser, Long packId) {
        if (!currentUser.isEnabled()) {
            throw new AccessDeniedException("User is not enabled");
        }

        PackCS2Entity pack = packCS2Repository
                .findById(packId)
                .orElseThrow(() -> new EntityNotFoundException("Shop pack is not found"));

        validateOwnership(currentUser, pack);
        validateNotOpened(pack);

        Rarity rolledRarity = packRarityRollerService.roll(pack.getPackShopCS2().getDropChances());
        PlayerCS2Entity player = playerCS2Service
                .findRandomActivePlayer();

        saveDroppedCard(currentUser, player, rolledRarity);

        deletePack(currentUser, pack);

        markPackAsOpened(pack);

        return packCS2Mapper.toOpenResultResponse(rolledRarity, player);
    }

    private void deletePack(UserEntity currentUser, PackCS2Entity pack) {
        currentUser.getUserCS2Entity().getPacksCS2().remove(pack);
    }

    private void markPackAsOpened(PackCS2Entity pack) {
        pack.setOpened(true);
        pack.setOpenedAt(Instant.now());

        packCS2Repository.save(pack);
    }

    private void saveDroppedCard(UserEntity currentUser, PlayerCS2Entity player, Rarity rolledRarity) {
        CardCS2Entity card = CardCS2Entity.builder()
                .userCS2(currentUser.getUserCS2Entity())
                .playerCS2(player)
                .rarity(rolledRarity)
                .receivedAt(Instant.now())
                .build();

        cardCS2Service.save(card);
    }

    private void validateNotOpened(PackCS2Entity pack) {
        if (pack.isOpened()) {
            throw new IllegalStateException("PackId=" + pack.getId() + " is opened");
        }
    }

    private void validateOwnership(UserEntity currentUser, PackCS2Entity pack) {
        if (!pack.getUserCS2().getId().equals(currentUser.getUserCS2Entity().getId())) {
            throw new IllegalArgumentException("Current userId=" + currentUser.getId() + " does not have packId=" + pack.getId());
        }
    }


}
