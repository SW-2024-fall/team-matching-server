package swe.second.team_matching_server.domain.meeting.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.IllegalArgumentException;

public class MeetingInvalidDateException extends IllegalArgumentException {
    public MeetingInvalidDateException() {
        super(ResultCode.MEETING_INVALID_DATE);
    }
}
