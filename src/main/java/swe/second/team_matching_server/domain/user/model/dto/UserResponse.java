package swe.second.team_matching_server.domain.user.model.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private String id;
    private String name;
    private String profileUrl;
    private String major;
    private String studentId;
    private List<String> features;
    private int attendanceScore;
}
