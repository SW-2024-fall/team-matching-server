package swe.second.team_matching_server.domain.user.model.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import swe.second.team_matching_server.domain.user.model.entity.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    private String id;
    private String name;
    private String profileUrl;

    public static UserProfile from(User user) {
        return UserProfile.builder()
            .id(user.getId())
            .name(user.getUsername())
            .profileUrl(user.getProfileImage() != null ? user.getProfileImage().getUrl() : null)
            .build();
    }
}
