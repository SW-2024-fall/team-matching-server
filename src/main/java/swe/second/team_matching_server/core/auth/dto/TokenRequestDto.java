package swe.second.team_matching_server.core.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequestDto {
    private String email;
    private String password;
    private String refreshToken;
    private String accessToken;

    public TokenRequestDto(String email, String password, String refreshToken, String accessToken) {
        this.email = email;
        this.password = password;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

}