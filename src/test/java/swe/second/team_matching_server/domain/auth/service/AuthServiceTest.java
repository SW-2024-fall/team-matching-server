package swe.second.team_matching_server.domain.auth.service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import swe.second.team_matching_server.common.enums.FileFolder;
import swe.second.team_matching_server.core.jwt.JwtProvider;
import swe.second.team_matching_server.domain.auth.model.dto.LoginRequest;
import swe.second.team_matching_server.domain.auth.model.dto.LogoutRequest;
import swe.second.team_matching_server.domain.auth.model.dto.SignupRequest;
import swe.second.team_matching_server.domain.auth.model.dto.TokenResponse;
import swe.second.team_matching_server.domain.auth.model.exception.WrongPasswordException;
import swe.second.team_matching_server.domain.auth.repository.RefreshRepository;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.file.service.FileUserService;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.enums.Major;
import swe.second.team_matching_server.domain.user.service.UserService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RefreshRepository refreshRepository;

    @Mock
    private FileUserService fileUserService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("T1 회원가입 테스트")
    public void testSignupSuccess() {
        // given
        SignupRequest signupRequest = new SignupRequest("username", "user@example.com", "password123", Major.COMPUTER_SCIENCE, "20231234", "010-1234-5678", Set.of(MeetingCategory.TRAVEL, MeetingCategory.MUSIC));
        File mockProfileImage = File.builder().id("1").originalName("default.png").folder(FileFolder.USER).mimeType("image/png").size(1024L).url("https://example.com/files/default.png").build();
        TokenResponse mockTokenResponse = new TokenResponse("accessToken", "refreshToken");
        User user = User.builder().id("userId").username("user123").email("user@example.com").password("encodedPassword").major(Major.COMPUTER_SCIENCE).studentId("20230001").phoneNumber("010-1234-5678").profileImage(File.builder().id("defaultFileId").originalName("default.png").folder(FileFolder.USER).mimeType("image/png").size(1024L).url("https://example.com/files/default.png").build()).build();

        when(fileUserService.getDefaultProfileImage()).thenReturn(mockProfileImage);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userService.save(any(User.class))).thenReturn(user);
        when(jwtProvider.createToken(user.getId())).thenReturn(mockTokenResponse);

        // when
        TokenResponse response = authService.signup(signupRequest, null);

        // then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken(), "AccessToken 확인");
        assertEquals("refreshToken", response.getRefreshToken(), "RefreshToken 확인");

        verify(jwtProvider, times(1)).createToken("userId");
        verify(fileUserService, times(1)).getDefaultProfileImage();
        verify(userService, times(1)).save(user);
        verify(refreshRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("T2 로그아웃 테스트")
    public void testLogoutSuccess() {
        // given
        LogoutRequest logoutRequest = new LogoutRequest("userId");

        doNothing().when(refreshRepository).deleteById(logoutRequest.getUserId());

        // when
        assertDoesNotThrow(() -> authService.logout(logoutRequest));

        // then
        verify(refreshRepository).deleteById(logoutRequest.getUserId());
    }

    @Test
    @DisplayName("T3-1 로그인 테스트 성공")
    public void testLoginSuccess() {
        // given
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password123");
        User user = User.builder().id("userId").username("user123").email("user@example.com").password("encodedPassword").major(Major.COMPUTER_SCIENCE).studentId("20230001").phoneNumber("010-1234-5678").profileImage(File.builder().id("defaultFileId").originalName("default.png").folder(FileFolder.USER).mimeType("image/png").size(1024L).url("https://example.com/files/default.png").build()).build();

        when(userService.findByEmail(loginRequest.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtProvider.createToken(user.getId())).thenReturn(new TokenResponse("accessToken", "refreshToken"));

        // when
        TokenResponse response = authService.login(loginRequest);

        // then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
    }

    @Test
    @DisplayName("T3-2 로그인 테스트 실패")
    public void testLoginWrongPassword() {
        // given
        LoginRequest loginRequest = new LoginRequest("user@example.com", "wrongPassword");
        User user = User.builder().id("userId").username("user123").email("user@example.com").password("encodedPassword").major(Major.COMPUTER_SCIENCE).studentId("20230001").phoneNumber("010-1234-5678").profileImage(File.builder().id("defaultFileId").originalName("default.png").folder(FileFolder.USER).mimeType("image/png").size(1024L).url("https://example.com/files/default.png").build()).build();

        when(userService.findByEmail(loginRequest.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        // when & then
        assertThrows(WrongPasswordException.class, () -> authService.login(loginRequest));
    }

}
