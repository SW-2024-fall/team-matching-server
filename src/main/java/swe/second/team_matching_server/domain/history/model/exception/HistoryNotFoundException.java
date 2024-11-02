package swe.second.team_matching_server.domain.history.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.BadRequestException;

public class HistoryNotFoundException extends BadRequestException {
    public HistoryNotFoundException() {
        super(ResultCode.HISTORY_NOT_FOUND);
    }
}
