package swe.second.team_matching_server.domain.history.model.dto;

import swe.second.team_matching_server.domain.user.model.dto.UserElement;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryElement {
    private Long id;
    private String thumbnailUrl;
    private String content;
    private UserElement writer;
    private Long meetingId;
    private String meetingName;
}
