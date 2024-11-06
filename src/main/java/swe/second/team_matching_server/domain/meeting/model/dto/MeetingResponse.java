package swe.second.team_matching_server.domain.meeting.model.dto;

import lombok.Data;

import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;

import lombok.Builder;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class MeetingResponse {
    private Long id;
    private MeetingMemberRole userRole;
    private MeetingInfo info;
    private MeetingMembers members;
    private boolean isLiked;
    private boolean isScraped;
}
