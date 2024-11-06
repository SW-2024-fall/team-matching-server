package swe.second.team_matching_server.domain.scrap.controller;

import swe.second.team_matching_server.domain.scrap.service.UserMeetingScrapService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import swe.second.team_matching_server.common.dto.ApiResponse;


@RestController
@RequestMapping("/api/meetings/{meetingId}/scraps")
public class MeetingScrapController {
    private final UserMeetingScrapService userMeetingScrapService;

    public MeetingScrapController(UserMeetingScrapService userMeetingScrapService) {
        this.userMeetingScrapService = userMeetingScrapService;
    }

    @PostMapping
    public ApiResponse<Void> scrapMeeting(@PathVariable Long meetingId) {
        // 후에 인증 추가 필요
        String userId = "test";

        userMeetingScrapService.save(meetingId, userId);
        return ApiResponse.success();
    }

    @DeleteMapping
    public ApiResponse<Void> unscrapMeeting(@PathVariable Long meetingId) {
        // 후에 인증 추가 필요
        String userId = "test";

        userMeetingScrapService.delete(meetingId, userId);
        return ApiResponse.success();
    }
}
