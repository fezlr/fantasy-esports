package com.fezlr.fantasy.cs2.pack.service;

import com.fezlr.fantasy.cs2.pack.dto.response.PackCS2Response;
import com.fezlr.fantasy.cs2.pack.mapper.PackCS2Mapper;
import com.fezlr.fantasy.cs2.pack.repository.PackCS2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PackCS2Service {
    private final PackCS2Repository packCS2Repository;
    private final PackCS2Mapper packCS2Mapper;

    public List<PackCS2Response> findAllPacksByTelegramId(Long telegramId) {
        return packCS2Repository.findAllByUserTelegramId(telegramId).stream()
                    .map(packCS2Mapper::toResponse).toList();
    }

}
