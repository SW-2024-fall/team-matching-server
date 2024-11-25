package swe.second.team_matching_server.domain.user.model.mapper;

import org.springframework.stereotype.Component;
import swe.second.team_matching_server.domain.auth.model.dto.RegisterRequest;
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
    
    public static User toEntity(RegisterRequest registerRequest, File profileImage) {
        return User.builder().username(registerRequest.getUsername())
            .email(registerRequest.getEmail())
            .password(registerRequest.getPassword())
            .major(registerRequest.getMajor())
            .studentId(registerRequest.getStudentId())
            .profileImage(profileImage).build();
    }
}
