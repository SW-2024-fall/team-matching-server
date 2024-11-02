package swe.second.team_matching_server.domain.history.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.NoPermissionException;

public class HistoryNoPermissionException extends NoPermissionException {
    public HistoryNoPermissionException() {
        super(ResultCode.HISTORY_NO_PERMISSION);
    }
}
