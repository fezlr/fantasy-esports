package com.fezlr.fantasy.cs2.pack.service;

import com.fezlr.fantasy.common.exception.AccessDeniedException;
import com.fezlr.fantasy.common.exception.NotEnoughFantasyPointsException;
import com.fezlr.fantasy.common.exception.PackShopIsNotAvailableException;
import com.fezlr.fantasy.cs2.pack.dto.response.PackCS2Response;
import com.fezlr.fantasy.cs2.pack.dto.response.PackPurchaseCS2Response;
import com.fezlr.fantasy.cs2.pack.entity.PackCS2Entity;
import com.fezlr.fantasy.cs2.pack.mapper.PackCS2Mapper;
import com.fezlr.fantasy.cs2.pack.repository.PackCS2Repository;
import com.fezlr.fantasy.cs2.packshop.entity.PackShopCS2Entity;
import com.fezlr.fantasy.cs2.packshop.service.PackShopCS2Service;
import com.fezlr.fantasy.user.entity.UserCS2Entity;
import com.fezlr.fantasy.user.entity.UserEntity;
import com.fezlr.fantasy.user.service.UserCS2Service;
import com.fezlr.fantasy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PackCS2Service {
    private final PackShopCS2Service packShopCS2Service;
    private final PackCS2Repository packCS2Repository;
    private final PackCS2Mapper packCS2Mapper;
    private final UserService userService;
    private final UserCS2Service userCS2Service;

    public List<PackCS2Response> findAllPacksByTelegramId(Long telegramId) {
        return packCS2Repository.findAllByUserTelegramId(telegramId).stream()
                    .map(packCS2Mapper::toResponse).toList();
    }

    //TODO: add tests and check
    @Transactional
    public PackPurchaseCS2Response purchasePackByUser(UserEntity currentUser, Long shopPackId) {
        if (!currentUser.isEnabled()) {
            throw new AccessDeniedException("User is not enabled");
        }

        PackShopCS2Entity shopPack = packShopCS2Service.findById(shopPackId);

        if (!shopPack.isAvailable()) {
            throw new PackShopIsNotAvailableException("Pack is not available");
        }

        //TODO: also make a check in frontend (block button if not enough points)
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
}
