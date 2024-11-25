package swe.second.team_matching_server.domain.scrap.controller;

import swe.second.team_matching_server.domain.scrap.service.UserMeetingScrapService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import swe.second.team_matching_server.common.dto.ApiResponse;

@RestController
@RequestMapping("/api/meetings/{meetingId}/scraps")
public class MeetingScrapController {
    private final UserMeetingScrapService userMeetingScrapService;

    public MeetingScrapController(UserMeetingScrapService userMeetingScrapService) {
        this.userMeetingScrapService = userMeetingScrapService;
    }

    @PostMapping
    public ApiResponse<Void> scrapMeeting(
        @PathVariable Long meetingId, @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();

        userMeetingScrapService.save(meetingId, userId);
        return ApiResponse.success();
    }

    @DeleteMapping
    public ApiResponse<Void> unscrapMeeting(
        @PathVariable Long meetingId, @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();

        userMeetingScrapService.delete(meetingId, userId);
        return ApiResponse.success();
    }
}
