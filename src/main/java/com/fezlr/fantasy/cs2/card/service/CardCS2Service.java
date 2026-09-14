package com.fezlr.fantasy.cs2.card.service;

import com.fezlr.fantasy.cs2.card.dto.response.InventoryCardCS2Response;
import com.fezlr.fantasy.cs2.card.mapper.CardCS2Mapper;
import com.fezlr.fantasy.cs2.card.repository.CardCS2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Validated
@RequiredArgsConstructor
@Service
public class CardCS2Service {
    private final CardCS2Repository cardCS2Repository;
    private final CardCS2Mapper cardCS2Mapper;

    public List<InventoryCardCS2Response> findAllCardsByTelegramId(Long telegramId) {
        return cardCS2Repository
                .findAllByUserTelegramId(telegramId)
                .stream()
                .map(cardCS2Mapper::toInventoryResponse)
                .collect(Collectors.toList());
    }
}
