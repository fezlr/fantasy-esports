package com.fezlr.fantasy.cs2.player.controller;

import com.fezlr.fantasy.cs2.player.dto.response.PlayerCS2Response;
import com.fezlr.fantasy.cs2.player.service.PlayerCS2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/cs2/players")
@Controller
public class PlayerCS2Controller {
    private final PlayerCS2Service playerCS2Service;

    @GetMapping
    public ResponseEntity<List<PlayerCS2Response>> getAllPlayers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(playerCS2Service.findAllPlayers());
    }
}
