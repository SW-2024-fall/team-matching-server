package swe.second.team_matching_server.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import swe.second.team_matching_server.common.enums.FileFolder;
import swe.second.team_matching_server.core.auth.dto.*;
import swe.second.team_matching_server.core.auth.jwt.TokenProvider;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.file.service.FileService;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.exception.UserNotFoundException;
import swe.second.team_matching_server.domain.user.repository.UserRepository;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final FileService fileService;

    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public String findEmailByID(String userId) {
        return userRepository.findById(userId)
                .map(User::getEmail) // User 엔티티에서 이메일 가져오기
                .orElseThrow(() -> new IllegalArgumentException("해당 ID에 해당하는 사용자가 없습니다."));
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getMyProfile(String accessToken) {
        // Validate the access token
        if (!tokenProvider.validateToken(accessToken)) {
            throw new RuntimeException("Invalid Access Token.");
        }
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found."));
        return UserResponseDto.of(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found."));
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));
        return UserResponseDto.of(user);
    }

    @Transactional
    public UserResponseDto updateUser(String accessToken, MultipartFile file, List<MeetingCategory> categories) {
        // Access Token 유효성 검증
        if (!tokenProvider.validateToken(accessToken)) {
            throw new RuntimeException("Invalid Access Token.");
        }

        // Access Token 으로부터 인증 정보 가져오기
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String email = authentication.getName();

        // 이메일을 통해 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        // 파일 처리 로직
        if (file != null && !file.isEmpty()) {
            FileCreateDto fileCreateDto = FileCreateDto.builder()
                    .file(file)
                    .folder(FileFolder.USER)
                    .user(user)  // 파일을 소유하는 사용자 지정
                    .build();

            File profileImage = fileService.save(fileCreateDto);  // FileService를 통한 파일 저장
            user.updateProfileImage(profileImage);  // User 엔티티의 프로필 이미지 업데이트
        }

        // 카테고리 처리 로직
        if (categories != null && !categories.isEmpty()) {
            user.setCategories(categories);
        }

        // 사용자 정보를 저장하고 UserResponseDto 반환
        return UserResponseDto.of(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(String accessToken) {
        // Validate the access token
        if (!tokenProvider.validateToken(accessToken)) {
            throw new RuntimeException("Invalid Access Token.");
        }

        // Retrieve the user's authentication details
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String email = authentication.getName();

        // Find and delete the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));
        userRepository.delete(user);
    }
}