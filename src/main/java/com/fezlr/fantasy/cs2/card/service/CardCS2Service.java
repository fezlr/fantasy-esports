package com.fezlr.fantasy.cs2.card.service;

import com.fezlr.fantasy.common.exception.NotEnoughFantasyPointsException;
import com.fezlr.fantasy.cs2.card.dto.response.InventoryCardCS2Response;
import com.fezlr.fantasy.cs2.card.dto.response.SellCardCS2Response;
import com.fezlr.fantasy.cs2.card.entity.CardCS2Entity;
import com.fezlr.fantasy.cs2.card.mapper.CardCS2Mapper;
import com.fezlr.fantasy.cs2.card.repository.CardCS2Repository;
import com.fezlr.fantasy.user.entity.UserEntity;
import com.fezlr.fantasy.user.service.UserCS2Service;
import com.fezlr.fantasy.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RequiredArgsConstructor
@Service
public class CardCS2Service {
    private final UserService userService;
    private final UserCS2Service userCS2Service;
    private final CardCS2Repository cardCS2Repository;
    private final CardCS2Mapper cardCS2Mapper;

    public List<InventoryCardCS2Response> findAllCardsByTelegramId(UserEntity user) {
        return cardCS2Repository
                .findAllByUserId(user.getId())
                .stream()
                .map(cardCS2Mapper::toInventoryResponse)
                .collect(Collectors.toList());
    }

    //TODO: add tests
    @Transactional
    public SellCardCS2Response sellCardByTelegramId(UserEntity currentUser, Long cardId) {
        userService.isAvailable(currentUser);

        CardCS2Entity card = cardCS2Repository
                .findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card is not found"));

        validateOwnership(currentUser, card);

        deleteCard(currentUser, card);

        addCardSellPriceToAccount(currentUser, card);

        userService.save(currentUser);

        return cardCS2Mapper.toSellCardResponse(card);
    }

    private void addCardSellPriceToAccount(UserEntity currentUser, CardCS2Entity card) {
        int updatedRows = userCS2Service.addFantasyPoints(currentUser.getId(), card.getSellPrice());

        if (updatedRows == 0) {
            throw new NotEnoughFantasyPointsException("Nothing was updated");
        }
    }

    private void deleteCard(UserEntity currentUser, CardCS2Entity card) {
        currentUser.getUserCS2Entity().getCardsCS2().remove(card);
    }

    private void validateOwnership(UserEntity currentUser, CardCS2Entity card) {
        if (!card.getUserCS2().getId().equals(currentUser.getUserCS2Entity().getId())) {
            throw new IllegalArgumentException("Current userId=" + currentUser.getId() + " does not have cardId=" + card.getId());
        }
    }

    @Transactional
    public CardCS2Entity save(CardCS2Entity card) {
        return cardCS2Repository.save(card);
    }
}
