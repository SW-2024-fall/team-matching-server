package swe.second.team_matching_server.core.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDto {
    private String accessToken;
    private String refreshToken;
}
