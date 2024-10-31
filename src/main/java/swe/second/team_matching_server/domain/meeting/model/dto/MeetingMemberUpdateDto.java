package swe.second.team_matching_server.domain.meeting.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;

@Getter
@AllArgsConstructor
public class MeetingMemberUpdateDto {
    private MeetingMemberRole role;
    private String targetUserId;
}
