package swe.second.team_matching_server.domain.meeting.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.NotFoundException;

public class MeetingNotFoundException extends NotFoundException {
    public MeetingNotFoundException() {
        super(ResultCode.MEETING_NOT_FOUND);
    }
}
