package com.fezlr.fantasy.grid.mapper;

import com.fezlr.fantasy.grid.dto.series_state.GameStateDto;
import com.fezlr.fantasy.grid.dto.series_state.PlayerGameStateDto;
import com.fezlr.fantasy.grid.dto.series_state.SeriesStateDto;
import com.fezlr.fantasy.grid.entity.SeriesPlayerStatCS2Entity;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GridSeriesStatMapper {

    public List<SeriesPlayerStatCS2Entity> toFlatEntities(Map<Long, SeriesStateDto> statesBySeriesId, Map<Long, Instant> startTimesBySeriesId) {
        List<SeriesPlayerStatCS2Entity> result = new ArrayList<>();

        statesBySeriesId.forEach((seriesId, seriesState) -> {
            Instant startedAt = startTimesBySeriesId.get(seriesId);
            if (startedAt == null || seriesState.games() == null) {
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
                        result.add(toEntity(seriesId, gameNumber, startedAt, player));
                    }
                }
            }
        });

        return result;
    }

    private SeriesPlayerStatCS2Entity toEntity(Long seriesId, int gameNumber, Instant startedAt, PlayerGameStateDto player) {
        return SeriesPlayerStatCS2Entity.builder()
                .seriesId(seriesId)
                .gameNumber(gameNumber)
                .gridPlayerId(player.gridId())
                .kills(nullToZero(player.kills()))
                .deaths(nullToZero(player.deaths()))
                .seriesStartedAt(startedAt)
                .build();
    }

    private int nullToZero(Integer value) {
        return value != null ? value : 0;
    }
}
