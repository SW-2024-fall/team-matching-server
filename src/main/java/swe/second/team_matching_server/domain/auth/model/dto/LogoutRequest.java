package swe.second.team_matching_server.domain.auth.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {

    private String userId;
}
