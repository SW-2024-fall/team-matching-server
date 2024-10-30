package swe.second.team_matching_server.domain.meeting.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.IllegalArgumentException;

public class MeetingInvalidParticipantException extends IllegalArgumentException {
    public MeetingInvalidParticipantException() {
        super(ResultCode.MEETING_INVALID_PARTICIPANTS);
    }
}