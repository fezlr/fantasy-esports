package com.fezlr.fantasy.valve.team.service;

import com.fezlr.fantasy.cs2.team.entity.TeamCS2Entity;
import com.fezlr.fantasy.cs2.team.service.TeamCS2Service;
import com.fezlr.fantasy.valve.team.client.ValveRankingClient;
import com.fezlr.fantasy.valve.team.dto.ValveGitHubFileDto;
import com.fezlr.fantasy.valve.team.dto.ValveTeamRankingDto;
import com.fezlr.fantasy.valve.team.parser.ValveRankingParser;
import com.fezlr.fantasy.valve.team.resolver.ValveTeamMatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ValveRankingSyncService {
    private final ValveRankingClient valveRankingClient;
    private final ValveRankingParser valveRankingParser;
    private final ValveTeamMatcher valveTeamMatcher;
    private final TeamCS2Service teamCS2Service;
    private final ValveHistoryService teamCS2ValveHistoryService;

    public void syncLatest() {
        Integer currentYear = Year.now().getValue();
        List<ValveGitHubFileDto> files = valveRankingClient.fetchFiles(currentYear);

        Optional<ValveGitHubFileDto> latestStandings = files
                .stream()
                .filter(valveRankingParser::isGlobalStandings)
                .max(java.util.Comparator.comparing(valveRankingParser::extractDate));

        if (latestStandings.isEmpty()) {
            log.warn("No global standings files found for year={}", currentYear);
            return;
        }

        ValveGitHubFileDto file = latestStandings.get();
        LocalDate rankingDate = valveRankingParser.extractDate(file);

        if (teamCS2ValveHistoryService.existsByRankingDate(rankingDate)) {
            log.info("Valve ranking for date={} already synced, skipping", rankingDate);
            return;
        }

        String markdown = valveRankingClient.fetchMarkdown(file.downloadUrl());
        List<ValveTeamRankingDto> rankings = valveRankingParser.parse(markdown);

        List<TeamCS2Entity> localTeams = teamCS2Service.findAll();
        Map<String, TeamCS2Entity> index = valveTeamMatcher.buildIndex(localTeams);

        //TODO refactor: delete matched
        Integer matched = 0;
        for (ValveTeamRankingDto ranking : rankings) {
            Optional<TeamCS2Entity> team = valveTeamMatcher.match(index, ranking.teamName());

            if (team.isEmpty()) {
                log.warn("No local team match for valveTeamName={}", ranking.teamName());
                continue;
            }

            teamCS2ValveHistoryService.saveRanking(team.get(), ranking.vrsPoints(), ranking.valveRating(), rankingDate);

            log.info("Synced Valve ranking for teamName={}, vrsPoints={}, valveRating={}", team.get().getName(), ranking.vrsPoints(), ranking.valveRating());
            matched++;
        }

        log.info("Synced Valve ranking for date={}, matched {}/{} teams", rankingDate, matched, rankings.size());
    }
}
