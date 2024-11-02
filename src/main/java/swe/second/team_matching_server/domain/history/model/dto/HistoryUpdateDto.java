package swe.second.team_matching_server.domain.history.model.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryUpdateDto {
    @NotNull
    private Long id;
    private String title;
    private String content;
    private boolean isPublic;
    private LocalDate date;
    private String location;
    private List<MemberAttendanceState> attendanceStates;
}
