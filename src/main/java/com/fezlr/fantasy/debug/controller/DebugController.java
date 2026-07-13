package com.fezlr.fantasy.debug.controller;

import com.fezlr.fantasy.cs2.player.sync.PlayerCS2SyncJob;
import com.fezlr.fantasy.cs2.team.sync.TeamCS2SyncJob;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/debug")
@RequiredArgsConstructor
@Profile("dev")
public class DebugController {
    private final TeamCS2SyncJob teamCS2SyncJob;
    private final PlayerCS2SyncJob playerCS2SyncJob;

    @PostMapping("/sync-teams")
    public void syncTeams() {
        teamCS2SyncJob.syncTeams();
    }

    @PostMapping("/sync-players")
    public void syncPlayers() {
        playerCS2SyncJob.syncPlayers();
    }
}