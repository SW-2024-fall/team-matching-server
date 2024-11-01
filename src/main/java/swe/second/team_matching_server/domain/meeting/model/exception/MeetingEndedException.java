package swe.second.team_matching_server.domain.meeting.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.BadRequestException;

public class MeetingEndedException extends BadRequestException {
    public MeetingEndedException() {
        super(ResultCode.MEETING_ENDED);
    }
}
