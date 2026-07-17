package com.fezlr.fantasy.cs2.card.service;

import com.fezlr.fantasy.cs2.card.dto.response.CardCS2Response;
import com.fezlr.fantasy.cs2.card.mapper.CardCS2Mapper;
import com.fezlr.fantasy.cs2.card.repository.CardCS2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CardCS2Service {
    private final CardCS2Repository cardCS2Repository;
    private final CardCS2Mapper cardCS2Mapper;

    public List<CardCS2Response> findAllCardsByCurrentUser(Long telegramId) {
        return cardCS2Repository.findAllByUserTelegramId(telegramId).stream()
                .map(cardCS2Mapper::toResponse).toList();

    }
}
