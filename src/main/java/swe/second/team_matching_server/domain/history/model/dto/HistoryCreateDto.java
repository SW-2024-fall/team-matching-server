package swe.second.team_matching_server.domain.history.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryCreateDto {
    @NotNull
    private Long meetingId;
    @NotNull
    private String title;
    @NotNull
    private String writerId;
    private String content;
    @Builder.Default
    private boolean isPublic = true;
    @NotNull
    private LocalDateTime date;
    @NotNull
    private String location;
    @Builder.Default
    private List<MemberAttendanceState> attendanceStates = new ArrayList<>();
}
