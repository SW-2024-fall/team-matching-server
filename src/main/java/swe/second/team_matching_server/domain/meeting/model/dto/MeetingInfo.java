package swe.second.team_matching_server.domain.meeting.model.dto;

import swe.second.team_matching_server.domain.meeting.model.enums.MeetingType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
    private int currentParticipant;
    private int minParticipant;
    private int maxParticipant;
    private String meta;
    private boolean isRecruiting;
    private List<String> features;
    private List<String> analyzedFeatures;
    private String analyzedIntroduction;
}
