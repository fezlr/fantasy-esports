package com.fezlr.fantasy.grid.validator;

import com.fezlr.fantasy.grid.dto.CS2PlayerSeriesStatisticsDto;
import org.springframework.stereotype.Component;

@Component
public class GridValidator {

    public boolean hasMatchData(CS2PlayerSeriesStatisticsDto dto) {
        return dto.count() != null && dto.count() > 0;
    }
}
