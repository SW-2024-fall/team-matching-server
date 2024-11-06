package swe.second.team_matching_server.domain.like.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.domain.like.service.UserMeetingLikeService;

@RestController
@RequestMapping("/api/meetings/{meetingId}/likes")
public class MeetingLikeController {
    private final UserMeetingLikeService userMeetingLikeService;

    public MeetingLikeController(UserMeetingLikeService userMeetingLikeService) {
        this.userMeetingLikeService = userMeetingLikeService;
    }

    @PostMapping
    public ApiResponse<Void> likeMeeting(@PathVariable Long meetingId) {
        // 추후 token에서 user 정보 가져오기. 지금은 그냥 예시
        String userId = "test";

        userMeetingLikeService.save(meetingId, userId);
        return ApiResponse.success();
    }

    @DeleteMapping
    public ApiResponse<Void> unlikeMeeting(@PathVariable Long meetingId) {
        // 추후 token에서 user 정보 가져오기. 지금은 그냥 예시
        String userId = "test";

        userMeetingLikeService.delete(meetingId, userId);
        return ApiResponse.success();
    }
}
