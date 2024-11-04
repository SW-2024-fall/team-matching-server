package swe.second.team_matching_server.domain.meeting.controller;

import swe.second.team_matching_server.domain.meeting.service.MeetingFacadeService;
import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingMembers;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingMemberUpdateDto;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingMemberTargetDto;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.extern.slf4j.Slf4j;
@RestController
@Slf4j
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
    public ApiResponse<String> application(@PathVariable Long meetingId) {
        // TODO: 로그인 구현 후 수정
        String userId = "test2";

        String result = meetingFacadeService.application(meetingId, userId);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/application")
    public ApiResponse<Void> cancelApplication(@PathVariable Long meetingId) {
        // TODO: 로그인 구현 후 수정
        String userId = "test2";

        meetingFacadeService.cancelApplication(meetingId, userId);
        return ApiResponse.success();
    }

    @PutMapping("application/accept")
    public ApiResponse<MeetingMembers> acceptApplication(@PathVariable Long meetingId, @RequestBody MeetingMemberTargetDto targetDto) {
        // TODO: 로그인 구현 후 수정
        String userId = "test";

        MeetingMembers meetingMembers = meetingFacadeService.acceptApplication(meetingId, userId, targetDto.getTargetUserId());
        return ApiResponse.success(meetingMembers);
    }

    @PutMapping("/application/reject")
    public ApiResponse<MeetingMembers> rejectApplication(@PathVariable Long meetingId, @RequestBody MeetingMemberUpdateDto dto) {
        // TODO: 로그인 구현 후 수정
        String userId = "test";

        MeetingMembers meetingMembers = meetingFacadeService.rejectApplication(meetingId, userId, dto.getTargetUserId());
        return ApiResponse.success(meetingMembers);
    }

    @PutMapping("/upgrade")
    public ApiResponse<MeetingMembers> upgradeToCoLeader(@PathVariable Long meetingId, @RequestBody MeetingMemberUpdateDto dto) {
        // TODO: 로그인 구현 후 수정
        String userId = "test";

        MeetingMembers meetingMembers = meetingFacadeService.upgradeToCoLeader(meetingId, userId, dto.getTargetUserId());
        return ApiResponse.success(meetingMembers);
    }

    @PutMapping("/downgrade")
    public ApiResponse<MeetingMembers> downgradeToMember(@PathVariable Long meetingId, @RequestBody MeetingMemberUpdateDto dto) {
        // TODO: 로그인 구현 후 수정
        String userId = "test";

        MeetingMembers meetingMembers = meetingFacadeService.downgradeToMember(meetingId, userId, dto.getTargetUserId());
        return ApiResponse.success(meetingMembers);
    }

    @PutMapping("/leave")
    public ApiResponse<MeetingMembers> leave(@PathVariable Long meetingId, @RequestBody MeetingMemberUpdateDto dto) {
        // TODO: 로그인 구현 후 수정
        String userId = "test";

        MeetingMembers meetingMembers = meetingFacadeService.leave(meetingId, userId, dto.getTargetUserId());
        return ApiResponse.success(meetingMembers);
    }

    @GetMapping("/my-role")
    public ApiResponse<MeetingMemberRole> getMyRole(@PathVariable Long meetingId) {
        // TODO: 로그인 구현 후 수정
        String userId = "test2";

        return ApiResponse.success(meetingFacadeService.getRoleByMeetingIdAndUserId(meetingId, userId));
    }

    @PutMapping("/role")
    public ApiResponse<MeetingMembers> updateRole(@PathVariable Long meetingId, @RequestBody MeetingMemberUpdateDto dto) {
        // TODO: 로그인 구현 후 수정
        String userId = "test";

        MeetingMembers meetingMembers = meetingFacadeService.updateRole(userId, meetingId, dto.getTargetUserId(), dto.getRole());
        return ApiResponse.success(meetingMembers);
    }

    @DeleteMapping
    public ApiResponse<MeetingMembers> leave(@PathVariable Long meetingId) {
        // TODO: 로그인 구현 후 수정. 본인에 대한 것만 가능
        String userId = "test2";

        return ApiResponse.success(meetingFacadeService.leave(meetingId, userId, userId));
    }
}
