package swe.second.team_matching_server.domain.history.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import swe.second.team_matching_server.domain.history.model.enums.AttendanceState;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberAttendanceState {
    private String userId;
    private AttendanceState attendanceState;
}