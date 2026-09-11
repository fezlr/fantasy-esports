package com.fezlr.fantasy.user.mapper;

import com.fezlr.fantasy.user.dto.response.UserFantasyPointsResponse;
import com.fezlr.fantasy.user.entity.UserCS2Entity;
import org.springframework.stereotype.Component;

@Component
public class UserCS2Mapper {
    public UserFantasyPointsResponse toFantasyPointsResponse(UserCS2Entity userCS2Entity) {
        return UserFantasyPointsResponse.builder()
                .fantasyPoints(userCS2Entity.getFantasyPoints())
                .build();
    }
}
