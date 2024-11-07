package swe.second.team_matching_server.core.auth.dto;

import lombok.Getter;
import lombok.Setter;
import swe.second.team_matching_server.domain.user.model.enums.Major;

@Getter
@Setter
public class UserRequestDto {
    private String email;
    private String password;
    private String username;
    private Major major;
    private String studentId;
    private String phoneNumber;

    public UserRequestDto(String email, String password, String username, Major major, String studentId, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.major = major;
        this.studentId = studentId;
        this.phoneNumber = phoneNumber;
    }
}
