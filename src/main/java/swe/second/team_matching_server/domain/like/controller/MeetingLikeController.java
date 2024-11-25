package swe.second.team_matching_server.domain.like.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.domain.meeting.service.MeetingFacadeService;

@RestController
@RequestMapping("/api/meetings/{meetingId}/likes")
public class MeetingLikeController {
    private final MeetingFacadeService meetingFacadeService;

    public MeetingLikeController(MeetingFacadeService meetingFacadeService) {
        this.meetingFacadeService = meetingFacadeService;
    }

    @PostMapping
    public ApiResponse<Void> likeMeeting(
        @PathVariable Long meetingId, @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();

        meetingFacadeService.likeMeeting(meetingId, userId);
        return ApiResponse.success();
    }

    @DeleteMapping
    public ApiResponse<Void> unlikeMeeting(
        @PathVariable Long meetingId, @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();

        meetingFacadeService.unlikeMeeting(meetingId, userId);
        return ApiResponse.success();
    }
}
