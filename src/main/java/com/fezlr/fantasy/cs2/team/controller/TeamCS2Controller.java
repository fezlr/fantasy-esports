package com.fezlr.fantasy.cs2.team.controller;

import com.fezlr.fantasy.cs2.team.dto.response.TeamCS2Response;
import com.fezlr.fantasy.cs2.team.service.TeamCS2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/cs2/teams")
@Controller
public class TeamCS2Controller {
    private final TeamCS2Service teamCS2Service;

    @GetMapping
    public ResponseEntity<List<TeamCS2Response>> getAllTeams() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamCS2Service.findAllTeams());
    }
}
