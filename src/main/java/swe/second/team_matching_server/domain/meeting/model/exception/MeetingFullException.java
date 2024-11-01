package swe.second.team_matching_server.domain.meeting.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.BadRequestException;

public class MeetingFullException extends BadRequestException {
    public MeetingFullException() {
        super(ResultCode.MEETING_FULL);
    }
}
