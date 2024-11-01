package swe.second.team_matching_server.domain.meeting.model.dto;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MeetingMemberTargetDto {
    private String targetUserId;
}
