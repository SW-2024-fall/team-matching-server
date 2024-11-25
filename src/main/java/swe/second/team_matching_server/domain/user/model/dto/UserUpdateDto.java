package swe.second.team_matching_server.domain.user.model.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    private String userId;
    private Set<MeetingCategory> preferredCategories = new HashSet<>();

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
