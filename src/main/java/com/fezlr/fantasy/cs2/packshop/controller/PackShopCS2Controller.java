package com.fezlr.fantasy.cs2.packshop.controller;

import com.fezlr.fantasy.cs2.packshop.dto.response.PackShopCS2Response;
import com.fezlr.fantasy.cs2.packshop.service.PackShopCS2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cs2/shop/packs")
public class PackShopCS2Controller {
    private final PackShopCS2Service packShopCS2Service;

    @GetMapping
    public ResponseEntity<List<PackShopCS2Response>> getAllAvailablePacks() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(packShopCS2Service.findAllAvailablePacks());
    }
}
