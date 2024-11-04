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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MeetingMember", description = "모임원 관리 API")
@RestController
@RequestMapping("/api/meetings/{meetingId}/members")
public class MeetingMemberController {
    private final MeetingFacadeService meetingFacadeService;

    public MeetingMemberController(MeetingFacadeService meetingFacadeService) {
        this.meetingFacadeService = meetingFacadeService;
    }

    @GetMapping
    @Operation(summary = "모임원 조회", description = "모임원을 조회합니다. 리더/부리더와 다른이들에게 보여지는 게 다릅니다. (리더/부리더: requested 공개, phonenumber 공개)")
    public ApiResponse<MeetingMembers> getMembers(@PathVariable Long meetingId) {
        // TODO: 로그인 구현 후 수정
        String userId = "test2";

        return ApiResponse.success(meetingFacadeService.getMembersByMeetingId(meetingId, userId));
    }

    @PostMapping("/application")
    @Operation(summary = "모임원 신청", description = "모임원을 신청합니다.")
    public ApiResponse<String> application(@PathVariable Long meetingId) {
        // TODO: 로그인 구현 후 수정
        String userId = "test2";

        String result = meetingFacadeService.application(meetingId, userId);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/application")
    @Operation(summary = "모임원 신청 취소", description = "모임원 신청을 취소합니다. (본인이 신청한 모임에 한함)")
    public ApiResponse<Void> cancelApplication(@PathVariable Long meetingId) {
        // TODO: 로그인 구현 후 수정
        String userId = "test2";

        meetingFacadeService.cancelApplication(meetingId, userId);
        return ApiResponse.success();
    }

    @PutMapping("application/accept")
    @Operation(summary = "모임원 신청 수락", description = "모임원 신청을 수락합니다. (리더/부리더만 가능)")
    @Parameter(name = "targetUserId", description = "수락할 대상 유저의 userId", required = true)
    public ApiResponse<MeetingMembers> acceptApplication(@PathVariable Long meetingId, @RequestBody MeetingMemberTargetDto targetDto) {
        // TODO: 로그인 구현 후 수정
        String userId = "test";

        MeetingMembers meetingMembers = meetingFacadeService.acceptApplication(meetingId, userId, targetDto.getTargetUserId());
        return ApiResponse.success(meetingMembers);
    }

    @PutMapping("/application/reject")
    @Operation(summary = "모임원 신청 거절", description = "모임원 신청을 거절합니다. (리더/부리더만 가능)")
    @Parameter(name = "targetUserId", description = "거절할 대상 유저의 userId", required = true)
    public ApiResponse<MeetingMembers> rejectApplication(@PathVariable Long meetingId, @RequestBody MeetingMemberTargetDto dto) {
        // TODO: 로그인 구현 후 수정
        String userId = "test";

        MeetingMembers meetingMembers = meetingFacadeService.rejectApplication(meetingId, userId, dto.getTargetUserId());
        return ApiResponse.success(meetingMembers);
    }

    @PutMapping("/upgrade")
    @Operation(summary = "부모임장 승급", description = "멤버를 부모임장으로 승급합니다.(리더/부리더만 가능)")
    @Parameter(name = "targetUserId", description = "승급할 대상 유저의 userId", required = true)
    public ApiResponse<MeetingMembers> upgradeToCoLeader(@PathVariable Long meetingId, @RequestBody MeetingMemberTargetDto dto) {
        // TODO: 로그인 구현 후 수정
        String userId = "test";

        MeetingMembers meetingMembers = meetingFacadeService.upgradeToCoLeader(meetingId, userId, dto.getTargetUserId());
        return ApiResponse.success(meetingMembers);
    }

    @PutMapping("/downgrade")
    @Operation(summary = "멤버 강등", description = "부모임장을 멤버로 강등합니다.(리더/부리더만 가능)")
    @Parameter(name = "targetUserId", description = "강등할 대상 유저의 userId", required = true)
    public ApiResponse<MeetingMembers> downgradeToMember(@PathVariable Long meetingId, @RequestBody MeetingMemberTargetDto dto) {
        // TODO: 로그인 구현 후 수정
        String userId = "test";

        MeetingMembers meetingMembers = meetingFacadeService.downgradeToMember(meetingId, userId, dto.getTargetUserId());
        return ApiResponse.success(meetingMembers);
    }

    @PutMapping("/leave")
    @Operation(summary = "모임원 내보내기", description = "모임원을 내보냅니다.(리더/부리더만 가능)")
    @Parameter(name = "targetUserId", description = "내보낼 대상 유저의 userId", required = true)
    public ApiResponse<MeetingMembers> leave(@PathVariable Long meetingId, @RequestBody MeetingMemberTargetDto dto) {
        // TODO: 로그인 구현 후 수정
        String userId = "test";

        MeetingMembers meetingMembers = meetingFacadeService.leave(meetingId, userId, dto.getTargetUserId());
        return ApiResponse.success(meetingMembers);
    }

    @GetMapping("/my-role")
    @Operation(summary = "내 권한 조회", description = "내 권한을 조회합니다.")
    public ApiResponse<MeetingMemberRole> getMyRole(@PathVariable Long meetingId) {
        // TODO: 로그인 구현 후 수정
        String userId = "test2";

        return ApiResponse.success(meetingFacadeService.getRoleByMeetingIdAndUserId(meetingId, userId));
    }

    @PutMapping("/role")
    @Operation(summary = "직접 role 변경", description = "명시한 role로 직접 변경합니다. (리더/부리더만 가능)")
    public ApiResponse<MeetingMembers> updateRole(@PathVariable Long meetingId, @RequestBody MeetingMemberUpdateDto dto) {
        // TODO: 로그인 구현 후 수정
        String userId = "test";

        MeetingMembers meetingMembers = meetingFacadeService.updateRole(userId, meetingId, dto.getTargetUserId(), dto.getRole());
        return ApiResponse.success(meetingMembers);
    }

    @DeleteMapping
    @Operation(summary = "모임 탈퇴", description = "모임을 탈퇴합니다. (본인이 가입한 모임)")
    public ApiResponse<MeetingMembers> leave(@PathVariable Long meetingId) {
        // TODO: 로그인 구현 후 수정. 본인에 대한 것만 가능
        String userId = "test2";

        return ApiResponse.success(meetingFacadeService.leave(meetingId, userId, userId));
    }
}
