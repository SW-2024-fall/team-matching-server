package swe.second.team_matching_server.domain.user.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import swe.second.team_matching_server.domain.user.service.UserFacadeService;
import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.domain.user.model.dto.UserResponse;
import swe.second.team_matching_server.domain.user.model.dto.UserUpdateDto;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingElement;
import swe.second.team_matching_server.domain.user.model.dto.UserSelfResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserFacadeService userFacadeService;

    @GetMapping
    public ApiResponse<UserSelfResponse> getSelf(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();

        return ApiResponse.success(userFacadeService.findSelf(userId));
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable String userId) {
        return ApiResponse.success(userFacadeService.findById(userId));
    }

    @DeleteMapping
    public ApiResponse<Void> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        userFacadeService.delete(userId);
        return ApiResponse.success();
    }

    @PatchMapping
    public ApiResponse<UserResponse> updateUser(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestPart UserUpdateDto user,
            @RequestPart(required = false) MultipartFile profileImage) {
        String userId = userDetails.getUsername();
        user.setUserId(userId);

        return ApiResponse.success(userFacadeService.update(user, profileImage));
    }
    
    @GetMapping("/likes")
    public ApiResponse<List<MeetingElement>> getMeeting(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        return ApiResponse.success(userFacadeService.findMeeting(userId));
    }

    @GetMapping("/scraped")
    public ApiResponse<List<MeetingElement>> getScrapedMeeting(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        return ApiResponse.success(userFacadeService.findScrapedMeeting(userId));
    }

    @GetMapping("/liked")
    public ApiResponse<List<MeetingElement>> getLikedMeeting(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        return ApiResponse.success(userFacadeService.findLikedMeeting(userId));
    }
}
