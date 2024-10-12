package swe.second.team_matching_server.domain.history.model.dto;

import java.time.LocalDateTime;
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
    private LocalDateTime date;
    private byte awardedScore;
    private String location;
    private List<MemberAttendanceState> attendance;
}
