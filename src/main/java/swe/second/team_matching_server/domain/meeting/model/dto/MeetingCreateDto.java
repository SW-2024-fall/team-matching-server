package swe.second.team_matching_server.domain.meeting.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import swe.second.team_matching_server.domain.meeting.model.enums.MeetingType;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingCreateDto {
    private String name;
    private MeetingType type;
    private List<MeetingCategory> categories;
    private List<String> features;
    private int minParticipant;
    private int maxParticipant;
    private String content;
    private String description;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String meta;
}
