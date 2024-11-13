package swe.second.team_matching_server.domain.user.model.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swe.second.team_matching_server.domain.user.model.dto.UserResponseDto;
import swe.second.team_matching_server.domain.user.model.dto.ProfileResponseDto;
import swe.second.team_matching_server.domain.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponseDto> getMyProfile(@RequestHeader("accessToken") String accessToken) {
        swe.second.team_matching_server.core.auth.dto.UserResponseDto userResponse = userService.getMyProfile(accessToken);
        return ResponseEntity.ok(new UserResponseDto("SUCCESS", "성공", userResponse));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponseDto> getUserProfile(
            @PathVariable String userId,
            @RequestHeader(value = "accessToken", required = false) String accessToken) {
        ProfileResponseDto profileResponse = userService.getUserProfile(userId);
        return ResponseEntity.ok(new ProfileResponseDto("SUCCESS", "성공", profileResponse.getData()));
    }

    @PutMapping
    public ResponseEntity<UserResponseDto> updateUser(
            @RequestHeader("accessToken") String accessToken,
            @RequestParam("file") MultipartFile file,
            @RequestParam("categories") List<String> categories) {
        swe.second.team_matching_server.core.auth.dto.UserResponseDto updatedUser = userService.updateUser(accessToken, file, categories);
        return ResponseEntity.ok(new UserResponseDto("SUCCESS", "성공", updatedUser));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestHeader("accessToken") String accessToken) {
        userService.deleteUser(accessToken);
        return ResponseEntity.ok().build();
    }
}