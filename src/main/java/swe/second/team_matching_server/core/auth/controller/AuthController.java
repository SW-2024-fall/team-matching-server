package swe.second.team_matching_server.core.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swe.second.team_matching_server.core.auth.dto.TokenDto;
import swe.second.team_matching_server.core.auth.dto.TokenRequestDto;
import swe.second.team_matching_server.core.auth.dto.UserRequestDto;
import swe.second.team_matching_server.core.auth.dto.UserResponseDto;
import swe.second.team_matching_server.core.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody UserRequestDto memberRequestDto) {
        return ResponseEntity.ok(authService.signup(memberRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody UserRequestDto memberRequestDto) {
        return ResponseEntity.ok(authService.login(memberRequestDto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(authService.reissue(tokenRequestDto));
    }
}
