package swe.second.team_matching_server.core.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {
    @Builder.Default
    private String grantType = "ROLE_USER";
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
    @Builder.Default
    private String tokenType = "Bearer";  // 기본값은 "Bearer"로 설정

    public TokenDto(String grantType, String accessToken, String refreshToken, Long accessTokenExpiresIn, String tokenType) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.tokenType = tokenType;
    }
}
