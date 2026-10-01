package com.fezlr.fantasy.cs2.packshop.service;

import com.fezlr.fantasy.common.exception.PackShopIsNotAvailableException;
import com.fezlr.fantasy.cs2.packshop.dto.response.PackShopCS2Response;
import com.fezlr.fantasy.cs2.packshop.entity.PackShopCS2Entity;
import com.fezlr.fantasy.cs2.packshop.mapper.PackShopCS2Mapper;
import com.fezlr.fantasy.cs2.packshop.repository.PackShopCS2Repository;
import com.fezlr.fantasy.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PackShopCS2Service {
    private final PackShopCS2Repository packShopCS2Repository;
    private final PackShopCS2Mapper packShopCS2Mapper;

    public List<PackShopCS2Response> findAllAvailablePacks() {
        return packShopCS2Repository
                .findAllAvailable()
                .stream()
                .map(packShopCS2Mapper::toResponse)
                .collect(Collectors.toList());
    }

    public PackShopCS2Entity findById(Long shopPackId) {
        return packShopCS2Repository
                .findById(shopPackId)
                .orElseThrow(() -> new EntityNotFoundException("Shop pack is not found"));
    }

    public void validateShopPackIsAvailable(PackShopCS2Entity shopPack) {
        if (!shopPack.isAvailable()) {
            throw new PackShopIsNotAvailableException("Pack is not available");
        }
    }
}
