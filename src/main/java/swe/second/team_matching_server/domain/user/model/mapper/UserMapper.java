package swe.second.team_matching_server.domain.user.model.mapper;

import org.springframework.stereotype.Component;
import swe.second.team_matching_server.domain.auth.model.dto.SignupRequest;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.user.model.dto.UserElement;
import swe.second.team_matching_server.domain.user.model.entity.User;

@Component
public class UserMapper {
    public UserElement toUserElement(User user) {
        return UserElement.builder().id(user.getId()).name(user.getUsername())
                .profileUrl(user.getProfileImage() != null ? user.getProfileImage().getUrl() : null)
                .attendenceScore(user.getAttendanceScore()).major(user.getMajor())
                .studentId(user.getStudentId()).phoneNumber(user.getPhoneNumber())
                .features(user.getFeatures()).build();
    }

    public static User toEntity(SignupRequest signupRequest, File profileImage) {
        return User.builder().username(signupRequest.getUsername())
            .email(signupRequest.getEmail())
            .password(signupRequest.getPassword())
            .major(signupRequest.getMajor())
            .studentId(signupRequest.getStudentId())
            .phoneNumber(signupRequest.getPhoneNumber())
            .profileImage(profileImage).build();
    }
}
