package swe.second.team_matching_server.domain.user.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import swe.second.team_matching_server.domain.user.model.mapper.UserMapper;
import swe.second.team_matching_server.domain.user.model.dto.UserResponse;
import swe.second.team_matching_server.domain.user.model.dto.UserUpdateDto;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.file.service.FileUserService;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.scrap.service.UserMeetingScrapService;
import swe.second.team_matching_server.domain.like.service.UserMeetingLikeService;
import swe.second.team_matching_server.domain.comment.service.CommentService;
import swe.second.team_matching_server.domain.meeting.service.MeetingFacadeService;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingElement;

@Service
@RequiredArgsConstructor
public class UserFacadeService {

    private final UserService userService;
    private final FileUserService fileUserService;
    private final UserMeetingScrapService userMeetingScrapService;
    private final UserMeetingLikeService userMeetingLikeService;
    private final CommentService commentService;
    private final MeetingFacadeService meetingFacadeService;

    public UserResponse findById(String userId, boolean isSelf) {
        User user = userService.findById(userId);
        if (isSelf) {
            return UserMapper.toUserResponse(user);
        }
        return UserMapper.toUserResponse(user);
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

    public List<MeetingElement> findMeeting(String userId) {
        return meetingFacadeService.findAllByUserId(userId);
    }

    public List<MeetingElement> findScrapedMeeting(String userId) {
        return userMeetingScrapService.findAllByUserId(userId);
    }

    public List<MeetingElement> findLikedMeeting(String userId) {
        return userMeetingLikeService.findAllByUserId(userId);
    }

    public List<MeetingElement> findCommentedMeeting(String userId) {
        return commentService.findAllByUserId(userId);
    }
}
