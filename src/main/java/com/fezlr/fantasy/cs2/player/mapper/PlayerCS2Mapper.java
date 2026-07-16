package com.fezlr.fantasy.cs2.player.mapper;

import com.fezlr.fantasy.cs2.player.dto.response.PlayerCS2Response;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import org.springframework.stereotype.Component;

@Component
public class PlayerCS2Mapper {
    public PlayerCS2Response toResponse(PlayerCS2Entity entity) {
        return PlayerCS2Response.builder()
                .nickname(entity.getNickname())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .imageUrl(entity.getHltvImageUrl())
                .teamId(entity.getTeam().getId())
                .isActive(entity.isActive())
                .build();
    }
}
