package com.fezlr.fantasy.grid.mapper;

import com.fezlr.fantasy.grid.dto.series.SeriesMeta;
import com.fezlr.fantasy.grid.dto.series_state.GameStateDto;
import com.fezlr.fantasy.grid.dto.series_state.PlayerGameStateDto;
import com.fezlr.fantasy.grid.dto.series_state.SeriesStateDto;
import com.fezlr.fantasy.grid.entity.SeriesPlayerStatCS2Entity;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class GridSeriesStatMapper {

    public List<SeriesPlayerStatCS2Entity> toFlatEntities(Map<Long, SeriesStateDto> statesBySeriesId, Map<Long, SeriesMeta> metaBySeriesId) {
        List<SeriesPlayerStatCS2Entity> result = new ArrayList<>();

        statesBySeriesId.forEach((seriesId, seriesState) -> {
            SeriesMeta meta = metaBySeriesId.get(seriesId);
            if (meta == null || seriesState.games() == null) {
                return;
            }

            List<GameStateDto> games = seriesState.games();
            for (int gameNumber = 0; gameNumber < games.size(); gameNumber++) {
                GameStateDto game = games.get(gameNumber);
                if (game.teams() == null) {
                    continue;
                }

                for (var team : game.teams()) {
                    if (team.players() == null) {
                        continue;
                    }

                    for (PlayerGameStateDto player : team.players()) {
                        result.add(toEntity(seriesId, gameNumber, meta, player));
                    }
                }
            }
        });

        return result;
    }

    private SeriesPlayerStatCS2Entity toEntity(Long seriesId, int gameNumber, SeriesMeta meta, PlayerGameStateDto player) {
        return SeriesPlayerStatCS2Entity.builder()
                .seriesId(seriesId)
                .gameNumber(gameNumber)
                .steamId(player.steamId())
                .kills(Objects.requireNonNullElse(player.kills(), 0))
                .killAssistsGiven(Objects.requireNonNullElse(player.killAssistsGiven(), 0))
                .deaths(Objects.requireNonNullElse(player.deaths(), 0))
                .teamKills(Objects.requireNonNullElse(player.teamKills(), 0))
                .selfKills(Objects.requireNonNullElse(player.selfKills(), 0))
                .damageDealt(Objects.requireNonNullElse(player.damageDealt(), 0))
                .damageTaken(Objects.requireNonNullElse(player.damageTaken(), 0))
                .headshots(Objects.requireNonNullElse(player.headshots(),0))
                .tournamentName(meta.tournamentName())
                .seriesStartedAt(meta.startedAt())
                .build();
    }
}