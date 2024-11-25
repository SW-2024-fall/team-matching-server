package swe.second.team_matching_server.domain.auth.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import swe.second.team_matching_server.domain.user.model.enums.Major;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    private String username;
    private String email;
    private String password;
    private Major major;
    private String studentId;
    private String phoneNumber;
}
