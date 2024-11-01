package swe.second.team_matching_server.domain.meeting.model.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class MeetingElement {
    private Long id;
    private String name;
    private List<String> features;
    private String preview;
    private int maxParticipant;
    private int currentParticipant;
    private String thumbnailUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private int likeCount;
    private int commentCount;
}
