package swe.second.team_matching_server.domain.history.model.dto;

import swe.second.team_matching_server.domain.user.model.dto.UserElement;
import swe.second.team_matching_server.domain.file.model.dto.FileResponse;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoryResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime date;
    private String location;
    private Long meetingId;
    private String meetingName;
    private UserElement writer;
    private List<FileResponse> files;
    private List<UserElement> attendees;
}
