package swe.second.team_matching_server.domain.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import swe.second.team_matching_server.domain.user.model.entity.User;

@Data
@AllArgsConstructor
public class ProfileResponseDto {
    private String resultCode;
    private String message;
    private UserElement data;

    public static ProfileResponseDto of(User user) {
        return new ProfileResponseDto(
                "SUCCESS",
                "Profile fetched successfully.",
                UserElement.of(user)
        );
    }
}