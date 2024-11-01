package swe.second.team_matching_server.domain.meeting.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.IllegalArgumentException;

public class MeetingInvalidCategoryException extends IllegalArgumentException {
    public MeetingInvalidCategoryException() {
        super(ResultCode.MEETING_INVALID_CATEGORY);
    }
}
