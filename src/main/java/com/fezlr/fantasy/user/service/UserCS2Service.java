package com.fezlr.fantasy.user.service;

import com.fezlr.fantasy.user.repository.UserCS2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserCS2Service {
    private final UserCS2Repository userCS2Repository;

    @Transactional
    public int subtractFantasyPoints(Long userId, long cost) {
        return userCS2Repository.subtractFantasyPoints(userId, cost);
    }

}
