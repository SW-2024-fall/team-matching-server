package swe.second.team_matching_server.domain.meeting.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.NoPermissionException;

public class MeetingNoPermissionException extends NoPermissionException {
    public MeetingNoPermissionException() {
        super(ResultCode.MEETING_NO_PERMISSION);
    }
}
