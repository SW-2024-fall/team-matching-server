package swe.second.team_matching_server.domain.history.model.dto;

import lombok.Getter;
import lombok.Setter;
import swe.second.team_matching_server.domain.history.model.enums.AttendanceState;

@Getter
@Setter
public class MemberAttendanceState {
    private String userId;
    private AttendanceState state;

    public MemberAttendanceState(String userId, AttendanceState state) {
        this.userId = userId;
        this.state = state;
    }
}