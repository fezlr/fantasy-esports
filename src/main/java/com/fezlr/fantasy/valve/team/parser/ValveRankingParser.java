package com.fezlr.fantasy.valve.team.parser;

import com.fezlr.fantasy.valve.team.dto.ValveGitHubFileDto;
import com.fezlr.fantasy.valve.team.dto.ValveTeamRankingDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ValveRankingParser {
    private static final String GLOBAL_STANDINGS_PREFIX = "standings_global_";
    private static final String STANDINGS_FILE_SUFFIX = ".md";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");
    private static final Pattern STANDINGS_TABLE_ROW_PATTERN = Pattern.compile(
            "\\|\\s*(\\d+)\\s*\\|\\s*(\\d+)\\s*\\|\\s*([^|]+?)\\s*\\|"
    );

    public boolean isGlobalStandings(ValveGitHubFileDto file) {
        return file.name().startsWith(GLOBAL_STANDINGS_PREFIX)
                && file.name().endsWith(STANDINGS_FILE_SUFFIX);
    }

    public LocalDate extractDate(ValveGitHubFileDto file) {
        String date = file.name().substring(
                GLOBAL_STANDINGS_PREFIX.length(),
                file.name().length() - STANDINGS_FILE_SUFFIX.length()
        );

        return LocalDate.parse(date, DATE_FORMATTER);
    }

    public List<ValveTeamRankingDto> parse(String markdown) {
        return markdown.lines()
                .map(STANDINGS_TABLE_ROW_PATTERN::matcher)
                .filter(Matcher::find)
                .map(this::parseRankingRow)
                .toList();
    }

    private ValveTeamRankingDto parseRankingRow(Matcher matcher) {
        return ValveTeamRankingDto.builder()
                .valveRating(Integer.parseInt(matcher.group(1)))
                .vrsPoints(Integer.parseInt(matcher.group(2)))
                .teamName(matcher.group(3).strip())
                .build();
    }
}
