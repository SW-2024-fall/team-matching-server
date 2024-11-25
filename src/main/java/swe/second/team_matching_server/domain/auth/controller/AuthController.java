package swe.second.team_matching_server.domain.auth.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.domain.auth.model.dto.TokenResponse;
import swe.second.team_matching_server.domain.auth.model.dto.LoginRequest;
import swe.second.team_matching_server.domain.auth.model.dto.SignupRequest;
import swe.second.team_matching_server.domain.auth.service.AuthService;
import swe.second.team_matching_server.domain.auth.model.dto.RefreshRequest;
import swe.second.team_matching_server.domain.auth.model.dto.LogoutRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
        return ApiResponse.success(authService.refreshToken(refreshRequest));
    }

    @PostMapping("/signup")
    public ApiResponse<TokenResponse> signup(
        @RequestParam(value = "profile", required = false) MultipartFile profile, 
        @RequestPart SignupRequest signupRequest) {
        return ApiResponse.success(authService.signup(signupRequest, profile));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        return ApiResponse.success(authService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest logoutRequest) {
        authService.logout(logoutRequest);
        return ApiResponse.success();
    }


}
