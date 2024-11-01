package swe.second.team_matching_server.domain.meeting.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.NotFoundException;

public class MeetingMemberNotFoundException extends NotFoundException {
    public MeetingMemberNotFoundException() {
        super(ResultCode.MEETING_MEMBER_NOT_FOUND);
    }
}
