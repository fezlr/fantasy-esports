package com.fezlr.fantasy.user.service;

import com.fezlr.fantasy.user.dto.response.UserFantasyPointsResponse;
import com.fezlr.fantasy.user.entity.UserEntity;
import com.fezlr.fantasy.user.mapper.UserCS2Mapper;
import com.fezlr.fantasy.user.repository.UserCS2Repository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserCS2Service {
    private final UserCS2Repository userCS2Repository;
    private final UserCS2Mapper userCS2Mapper;

    @Transactional
    public int subtractFantasyPoints(Long userId, long cost) {
        return userCS2Repository.subtractFantasyPoints(userId, cost);
    }

    public UserFantasyPointsResponse findFantasyPointsByUser(UserEntity userEntity) {
        return userCS2Mapper.toFantasyPointsResponse(
                userCS2Repository
                        .findByUser(userEntity)
                        .orElseThrow(
                                () -> new EntityNotFoundException("UserCS2 is not found")));
    }
}
