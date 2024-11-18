package swe.second.team_matching_server.domain.user.model.dto;

import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.enums.Major;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;

@Data
@SuperBuilder
@AllArgsConstructor
public class UserElement {
    private String id;
    private String name;
    private String profileUrl;
    private int attendanceScore;
    private Major major;
    private String studentId;
    private String phoneNumber;
    private List<String> features;

    public static UserElement of(User user) {
        return UserElement.builder()
                .id(user.getId())
                .name(user.getUsername())
                .profileUrl(user.getProfileImage() != null ? user.getProfileImage().getUrl() : null)
                .attendanceScore(user.getAttendanceScore())
                .major(user.getMajor())
                .studentId(user.getStudentId())
                .phoneNumber(user.getPhoneNumber())
                .features(user.getFeatures())
                .build();
    }
}
