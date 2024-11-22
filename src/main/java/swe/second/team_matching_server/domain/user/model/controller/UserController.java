package swe.second.team_matching_server.domain.user.model.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.core.auth.dto.UserResponseDto;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;
import swe.second.team_matching_server.domain.user.service.UserService;

import java.util.List;

@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "특정 사용자 프로필 조회", description = "특정 사용자의 프로필을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{userId}")
    public ApiResponse<UserResponseDto> getUserProfile(
            @PathVariable
            @Parameter(description = "조회하려는 사용자 ID", required = true) String userId,
            @RequestHeader(value = "accessToken", required = false)
            @Parameter(description = "사용자 인증 토큰 (선택). 인증된 사용자일 경우 추가 정보를 볼 수 있습니다.") String accessToken) {
        UserResponseDto userResponse = userService.getUserProfile(userId);
        return ApiResponse.success(userResponse);
    }

    @Operation(summary = "내 프로필 조회", description = "사용자의 개인 프로필을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ApiResponse<UserResponseDto> getMyProfile(@RequestHeader("accessToken") @Parameter(description = "사용자 인증 토큰. 로그인을 통해 발급받은 토큰입니다.", required = true) String accessToken) {
        swe.second.team_matching_server.core.auth.dto.UserResponseDto userResponse = userService.getMyProfile(accessToken);
        return ApiResponse.success(userResponse);
    }

    @Operation(summary = "프로필 수정", description = "사용자의 프로필 정보를 수정합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 수정 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping
    public ApiResponse<UserResponseDto> updateUser(
            @RequestHeader("accessToken")
            @Parameter(description = "사용자 인증 토큰. 로그인을 통해 발급받은 토큰입니다.", required = true) String accessToken,
            @RequestParam("file")
            @Parameter(description = "수정할 사용자 프로필 사진 파일. Multipart 형식입니다.", required = true) MultipartFile file,
            @RequestParam("categories")
            @Parameter(description = "모임 카테고리 목록", required = true) List<MeetingCategory> categories) {
        UserResponseDto updatedUser = userService.updateUser(accessToken, file, categories);
        return ApiResponse.success(updatedUser);
    }

    @Operation(summary = "사용자 삭제", description = "사용자의 계정을 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping
    public ApiResponse<Void> deleteUser(
            @RequestHeader("accessToken")
            @Parameter(description = "사용자 인증 토큰. 로그인을 통해 발급받은 토큰입니다.", required = true) String accessToken) {
        userService.deleteUser(accessToken);
        return ApiResponse.success();
    }
}