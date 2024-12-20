package swe.second.team_matching_server.domain.meeting.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberApplicationMethod;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.time.DayOfWeek;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingUpdateDto {
    private String name;
    private String title;
    @Builder.Default
    private List<MeetingCategory> categories = new ArrayList<>();
    @Builder.Default
    private List<String> features = new ArrayList<>();
    @Builder.Default
    private Set<DayOfWeek> days = new HashSet<>();
    private int minParticipant;
    private int maxParticipant;
    private String content;
    private String location;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String meta;
    private MeetingMemberApplicationMethod applicationMethod;
}
