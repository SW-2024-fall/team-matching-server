package swe.second.team_matching_server.domain.history.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.IllegalArgumentException;

public class HistoryInvalidAttendanceHistoryException extends IllegalArgumentException {
    public HistoryInvalidAttendanceHistoryException() {
        super(ResultCode.HISTORY_INVALID_ATTENDANCE_HISTORY);
    }
}
