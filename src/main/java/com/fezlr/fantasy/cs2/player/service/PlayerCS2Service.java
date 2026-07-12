package com.fezlr.fantasy.cs2.player.service;

import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.repository.PlayerCS2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PlayerCS2Service {
    private final PlayerCS2Repository playerCS2Repository;

    public void createOrUpdate(PlayerCS2Entity entity) {

    }


}
