package swe.second.team_matching_server.domain.meeting.model.dto;

import java.util.List;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingMembers {
    private List<MeetingMemberElement> member;
    private List<MeetingMemberElement> requested;
}
