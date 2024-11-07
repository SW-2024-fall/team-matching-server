package swe.second.team_matching_server.core.auth.dto;

import lombok.Getter;
import lombok.Setter;
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

    public UserResponseDto(String id, String email, String username, Major major, String studentId, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.major = major;
        this.studentId = studentId;
        this.phoneNumber = phoneNumber;
    }
}

