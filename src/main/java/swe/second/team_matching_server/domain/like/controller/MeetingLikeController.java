package swe.second.team_matching_server.domain.like.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    public ApiResponse<Void> likeMeeting(@PathVariable Long meetingId) {
        // 추후 token에서 user 정보 가져오기. 지금은 그냥 예시
        String userId = "test";

        meetingFacadeService.likeMeeting(meetingId, userId);
        return ApiResponse.success();
    }

    @DeleteMapping
    public ApiResponse<Void> unlikeMeeting(@PathVariable Long meetingId) {
        // 추후 token에서 user 정보 가져오기. 지금은 그냥 예시
        String userId = "test";

        meetingFacadeService.unlikeMeeting(meetingId, userId);
        return ApiResponse.success();
    }
}
