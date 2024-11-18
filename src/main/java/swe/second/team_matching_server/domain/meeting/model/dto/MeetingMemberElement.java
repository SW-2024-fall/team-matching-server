package swe.second.team_matching_server.domain.meeting.model.dto;

import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;
import swe.second.team_matching_server.domain.user.model.dto.UserElement;
import swe.second.team_matching_server.domain.user.model.enums.Major;

import java.util.List;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class MeetingMemberElement extends UserElement {
    private MeetingMemberRole role;

    public MeetingMemberElement(String id, String name, String profileUrl, byte attendenceScore,
            Major major, String studentId, String phoneNumber, List<String> features,
            MeetingMemberRole role) {

        super(id, name, profileUrl, attendenceScore, major, studentId, phoneNumber, features);
        this.role = role;
    }
}

