package swe.second.team_matching_server.domain.meeting.model.dto;

import swe.second.team_matching_server.domain.meeting.model.enums.MeetingType;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberApplicationMethod;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class MeetingInfo {
    private String name;
    private MeetingType type;
    private String title;
    private String content;
    private List<String> thumbnailUrls;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private int currentParticipants;
    private int minParticipant;
    private int maxParticipant;
    private String meta;
    private List<MeetingCategory> categories;
    private List<String> features;
    private List<String> analyzedFeatures;
    private String analyzedIntroduction;
    private MeetingMemberApplicationMethod applicationMethod;
    private int likes;
    private int comments;
    private int scraps;
    private Set<DayOfWeek> days;
}
