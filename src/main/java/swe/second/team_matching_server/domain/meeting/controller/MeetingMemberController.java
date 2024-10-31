package swe.second.team_matching_server.domain.meeting.controller;

import swe.second.team_matching_server.domain.meeting.service.MeetingFacadeService;
import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingMembers;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meetings/{meetingId}/members")
public class MeetingMemberController {
    private final MeetingFacadeService meetingFacadeService;

    public MeetingMemberController(MeetingFacadeService meetingFacadeService) {
        this.meetingFacadeService = meetingFacadeService;
    }

    @GetMapping
    public ApiResponse<MeetingMembers> getMembers(@PathVariable Long meetingId) {
        return ApiResponse.success(meetingFacadeService.getMembersByMeetingId(meetingId));
    }

    @PostMapping("/application")
    public ApiResponse<Void> application(@PathVariable Long meetingId) {
        // TODO: 로그인 구현 후 수정
        String userId = "test2";

        meetingFacadeService.application(meetingId, userId);
        return ApiResponse.success();
    }

    @DeleteMapping("/application")
    public ApiResponse<Void> cancelApplication(@PathVariable Long meetingId) {
        // TODO: 로그인 구현 후 수정
        String userId = "test2";

        meetingFacadeService.cancelApplication(meetingId, userId);
        return ApiResponse.success();
    }

    // @GetMapping("/my-role")
    // public ApiResponse<MeetingMemberRole> getMyRole(@PathVariable Long meetingId) {
    //     // TODO: 로그인 구현 후 수정
    //     String userId = "test";

    //     return ApiResponse.success(meetingFacadeService.getRoleByMeetingIdAndUserId(meetingId, userId));
    // }
}
