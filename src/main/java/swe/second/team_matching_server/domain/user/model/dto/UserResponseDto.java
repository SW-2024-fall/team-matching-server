package swe.second.team_matching_server.domain.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDto {
    private String resultCode;
    private String message;
    private UserElement data;

    public UserResponseDto(String success, String 성공, swe.second.team_matching_server.core.auth.dto.UserResponseDto userResponse) {
    }
}


