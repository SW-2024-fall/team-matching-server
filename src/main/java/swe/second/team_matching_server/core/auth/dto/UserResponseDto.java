package swe.second.team_matching_server.core.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.enums.Major;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String id;
    private String email;
    private String username;
    private Major major;
    private String studentId;
    private String phoneNumber;
    private byte attendanceScore;
    private List<String> features;
    private String profileImageUrl;

    public static UserResponseDto of(User member) {
        return UserResponseDto.builder()
            .id(member.getId())
            .email(member.getEmail())
            .username(member.getUsername())
            .major(member.getMajor())
            .studentId(member.getStudentId())
            .phoneNumber(member.getPhoneNumber())
            .attendanceScore(member.getAttendanceScore())
            .features(member.getFeatures())
            .profileImageUrl(member.getProfileImage() != null ? member.getProfileImage().getUrl() : null)
            .build();
    }
}