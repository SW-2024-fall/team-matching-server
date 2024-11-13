package swe.second.team_matching_server.core.auth.dto;

import lombok.Getter;
import lombok.Setter;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.enums.Major;

@Getter
@Setter
public class UserResponseDto {
    private String id;
    private String email;
    private String username;
    private Major major;
    private String studentId;
    private String phoneNumber;
    private String name;

    public UserResponseDto() {
        this.id = id;
        this.email = email;
        this.username = username;
        this.major = major;
        this.studentId = studentId;
        this.phoneNumber = phoneNumber;
    }

    public static UserResponseDto of(User member) {
        UserResponseDto dto = new UserResponseDto();
        dto.id = member.getId();
        dto.email = member.getEmail();
        dto.name = member.getName();
        return dto;
    }
}