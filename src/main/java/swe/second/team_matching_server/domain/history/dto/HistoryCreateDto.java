package swe.second.team_matching_server.domain.history.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryCreateDto {
    private String title;
    private String content;
    private boolean isPublic;
    private LocalDate date;
    private byte awardedScore;
    private List<MemberAttendanceState> attendance;
}
