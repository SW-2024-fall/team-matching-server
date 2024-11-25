package swe.second.team_matching_server.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import swe.second.team_matching_server.domain.user.model.mapper.UserMapper;
import swe.second.team_matching_server.domain.user.model.dto.UserResponse;
import swe.second.team_matching_server.domain.user.model.dto.UserUpdateDto;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.file.service.FileUserService;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.user.model.entity.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacadeService {

    private final UserService userService;
    private final FileUserService fileUserService;

    public UserResponse findById(String userId) {
        return UserMapper.toUserResponse(userService.findById(userId));
    }

    public UserResponse update(UserUpdateDto userUpdateDto, MultipartFile profileImage) {
        User user = userService.findById(userUpdateDto.getUserId());

        if (profileImage != null) {
            FileCreateDto fileCreateDto =
                    FileCreateDto.builder().file(profileImage).user(user).build();
            File file =
                    fileUserService.updateFileByUserId(userUpdateDto.getUserId(), fileCreateDto);
            user.updateProfileImage(file);
        }

        user.updatePreferredCategories(userUpdateDto.getPreferredCategories());

        return UserMapper.toUserResponse(userService.update(user));
    }

    public void delete(String userId) {
        userService.delete(userId);
    }
}
