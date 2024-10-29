package swe.second.team_matching_server.domain.meeting.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.NotFoundException;

public class MeetingRequestNotFoundException extends NotFoundException {
    public MeetingRequestNotFoundException() {
        super(ResultCode.MEETING_REQUEST_NOT_FOUND);
    }
}
