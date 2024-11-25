package swe.second.team_matching_server.domain.user.model.dto;

import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;

import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;

@Getter
@Builder
public class UserResponse {

    private String id;
    private String name;
    private String email;
    private String profileUrl;
    private String major;
    private String studentId;
    private String phoneNumber;
    private Set<MeetingCategory> preferredCategories;
    private List<String> features;
    private int attendanceScore;
}
