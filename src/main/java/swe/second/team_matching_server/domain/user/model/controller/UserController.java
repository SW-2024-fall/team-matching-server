package swe.second.team_matching_server.domain.user.model.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.core.auth.dto.UserResponseDto;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;
import swe.second.team_matching_server.domain.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<UserResponseDto> getMyProfile(@RequestHeader("accessToken") String accessToken) {
        swe.second.team_matching_server.core.auth.dto.UserResponseDto userResponse = userService.getMyProfile(accessToken);
        return ApiResponse.success(userResponse);
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponseDto> getUserProfile(
            @PathVariable String userId,
            @RequestHeader(value = "accessToken", required = false) String accessToken) {
        UserResponseDto userResponse = userService.getUserProfile(userId);
        return ApiResponse.success(userResponse);
    }

    @PutMapping
    public ApiResponse<UserResponseDto> updateUser(
            @RequestHeader("accessToken") String accessToken,
            @RequestParam("file") MultipartFile file,
            @RequestParam("categories") List<MeetingCategory> categories) {
        UserResponseDto updatedUser = userService.updateUser(accessToken, file, categories);
        return ApiResponse.success(updatedUser);
    }

    @DeleteMapping
    public ApiResponse<Void> deleteUser(@RequestHeader("accessToken") String accessToken) {
        userService.deleteUser(accessToken);
        return ApiResponse.success();
    }
}