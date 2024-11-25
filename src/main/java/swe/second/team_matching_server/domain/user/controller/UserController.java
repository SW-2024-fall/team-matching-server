package swe.second.team_matching_server.domain.user.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import swe.second.team_matching_server.domain.user.service.UserFacadeService;
import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.domain.user.model.dto.UserResponse;
import swe.second.team_matching_server.domain.user.model.dto.UserUpdateDto;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserFacadeService userFacadeService;

    @GetMapping
    public ApiResponse<UserResponse> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();

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
}
