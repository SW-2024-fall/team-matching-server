package swe.second.team_matching_server.domain.meeting.model.dto;

import lombok.Data;

import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;

import lombok.Builder;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class MeetingDetail {
    private Long id;
    private MeetingMemberRole role;
    private MeetingInfo info;
    private MeetingMembers members;
}