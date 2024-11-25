package swe.second.team_matching_server.domain.auth.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import swe.second.team_matching_server.domain.auth.model.dto.SignupRequest;
import swe.second.team_matching_server.domain.auth.model.dto.TokenResponse;
import swe.second.team_matching_server.domain.auth.repository.RefreshRepository;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.mapper.UserMapper;
import swe.second.team_matching_server.domain.file.service.FileUserService;
import swe.second.team_matching_server.common.enums.FileFolder;
import swe.second.team_matching_server.core.jwt.JwtProvider;
import swe.second.team_matching_server.domain.auth.model.dto.LogoutRequest;
import swe.second.team_matching_server.domain.auth.model.entity.Refresh;
import swe.second.team_matching_server.domain.user.service.UserService;
import swe.second.team_matching_server.domain.auth.model.dto.RefreshRequest;
import swe.second.team_matching_server.domain.auth.model.dto.LoginRequest;
import swe.second.team_matching_server.domain.auth.model.exception.WrongPasswordException;
import swe.second.team_matching_server.domain.auth.model.exception.RefreshTokenNofoundExeption;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final RefreshRepository refreshRepository;
    private final FileUserService fileUserService;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public TokenResponse createToken(String userId) {
        TokenResponse token = jwtProvider.createToken(userId);

        refreshRepository.save(
            Refresh.builder()
                .userId(userId)
                .refreshToken(token.getRefreshToken())
                .build()
        );
        return token;
    }

    public TokenResponse refreshToken(RefreshRequest refreshRequest) {
        jwtProvider.validateToken(refreshRequest.getRefreshToken());
        String userId = jwtProvider.getUserId(refreshRequest.getRefreshToken());

        if (!refreshRepository.existsByUserId(userId)) {
            throw new RefreshTokenNofoundExeption();
        }

        return createToken(userId);
    }

    public TokenResponse login(LoginRequest loginRequest) {
        User user = userService.findByEmail(loginRequest.getEmail());
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new WrongPasswordException();
        }

        return createToken(user.getId());
    }

    @Transactional
    public TokenResponse signup(SignupRequest signupRequest, MultipartFile profileImageFile) {
        File profileImage;
        if (profileImageFile != null) {
            FileCreateDto fileCreateDto =
                    FileCreateDto.builder().file(profileImageFile).folder(FileFolder.USER).build();
            profileImage = fileUserService.saveProfileImage(fileCreateDto);
        } else {
            profileImage = fileUserService.getDefaultProfileImage();
        }

        User user = UserMapper.toEntity(signupRequest, profileImage);
        user.updatePassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);

        return createToken(user.getId());
    }

    @Transactional
    public void logout(LogoutRequest logoutRequest) {
        refreshRepository.deleteById(logoutRequest.getUserId());
    }
}
