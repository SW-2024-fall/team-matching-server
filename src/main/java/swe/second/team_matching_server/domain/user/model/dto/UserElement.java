package swe.second.team_matching_server.domain.user.model.dto;

import swe.second.team_matching_server.domain.user.model.enums.Major;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;

@Data
@SuperBuilder
@AllArgsConstructor
public class UserElement {
    private Long id;
    private String name;
    private String profileUrl;
    private int attendenceScore;
    private Major major;
    private String studentId;
    private String phoneNumber;
    private List<String> features;
}
