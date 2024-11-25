package swe.second.team_matching_server.domain.meeting.model.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;
import swe.second.team_matching_server.domain.comment.model.dto.CommentResponse;

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
    @Builder.Default
    private boolean isLiked = false;
    @Builder.Default
    private boolean isScraped = false;
    @Builder.Default
    private List<CommentResponse> comments = new ArrayList<>();
}
