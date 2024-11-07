package swe.second.team_matching_server.core.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";  // 기본값은 "Bearer"로 설정

    public TokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
