package swe.second.team_matching_server.domain.auth.model.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshRequest {

    private String refreshToken;
}
