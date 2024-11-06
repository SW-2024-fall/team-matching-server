package swe.second.team_matching_server.domain.meeting.model.mapper;

import swe.second.team_matching_server.domain.meeting.model.dto.MeetingCreateDto;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.meeting.model.entity.MeetingMember;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingResponse;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingInfo;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingMembers;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingMemberElement;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingElement;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Component
public class MeetingMapper {
    public Meeting toEntity(MeetingCreateDto meetingCreateDto) {
        return Meeting.builder()
            .name(meetingCreateDto.getName())
            .title(meetingCreateDto.getTitle())
            .content(meetingCreateDto.getContent())
            .type(meetingCreateDto.getType())
            .categories(meetingCreateDto.getCategories())
            .features(meetingCreateDto.getFeatures())
            .minParticipant((byte) meetingCreateDto.getMinParticipant())
            .maxParticipant((byte) meetingCreateDto.getMaxParticipant())
            .days(meetingCreateDto.getDays())
            .location(meetingCreateDto.getLocation())
            .startDate(meetingCreateDto.getStartDate())
            .endDate(meetingCreateDto.getEndDate())
            .startTime(meetingCreateDto.getStartTime())
            .endTime(meetingCreateDto.getEndTime())
            .meta(meetingCreateDto.getMeta())
            .applicationMethod(meetingCreateDto.getApplicationMethod())
            .build();
    }

    public MeetingElement toMeetingElement(Meeting meeting) {
        return MeetingElement.builder()
            .id(meeting.getId())
            .name(meeting.getName())
            .features(meeting.getFeatures())
            .preview(meeting.getContent())
            .maxParticipant(meeting.getMaxParticipant())
            .currentParticipants(meeting.getCurrentParticipants())
            .thumbnailUrl(meeting.getThumbnailFiles().size() > 0 ? meeting.getThumbnailFiles().get(0).getUrl() : null)
            .startDate(meeting.getStartDate())
            .endDate(meeting.getEndDate())
            .build();
    }

    public MeetingResponse toResponse(Meeting meeting, List<MeetingMember> members, boolean isExecutive, int likes, int comments, int scraps) {
        return MeetingResponse.builder()
            .id(meeting.getId())
            .info(toMeetingInfo(meeting, likes, comments, scraps))
            .members(toMeetingMembers(members, isExecutive))
            .build();
    }

    private MeetingInfo toMeetingInfo(Meeting meeting, int likes, int comments, int scraps) {
        return MeetingInfo.builder()
            .name(meeting.getName())
            .title(meeting.getTitle())
            .content(meeting.getContent())
            .thumbnailUrls(meeting.getThumbnailFiles().stream()
                .map(File::getUrl)
                .collect(Collectors.toList()))
            .type(meeting.getType())
            .categories(meeting.getCategories())
            .features(meeting.getFeatures())
            .minParticipant(meeting.getMinParticipant())
            .maxParticipant(meeting.getMaxParticipant())
            .location(meeting.getLocation())
            .startDate(meeting.getStartDate())
            .endDate(meeting.getEndDate())
            .startTime(meeting.getStartTime())
            .endTime(meeting.getEndTime())
            .meta(meeting.getMeta())
            .applicationMethod(meeting.getApplicationMethod())
            .likes(likes)
            .comments(comments)
            .scraps(scraps)
            .build();
    }

    public MeetingMembers toMeetingMembers(List<MeetingMember> members, boolean isExecutive) {
        return MeetingMembers.builder()
            .member(members.stream()
                .filter(m -> (isExecutive || m.getRole() != MeetingMemberRole.REQUESTED)
                    && m.getRole() != MeetingMemberRole.EXTERNAL)
                .map(m -> toMeetingMemberElement(m, isExecutive))
                .collect(Collectors.toList()))
            .requested(isExecutive ? members.stream()
                .filter(m -> m.getRole() == MeetingMemberRole.REQUESTED)
                .map(m -> toMeetingMemberElement(m, isExecutive))
                .collect(Collectors.toList()) : new ArrayList<>())
            .build();
    }

    private MeetingMemberElement toMeetingMemberElement(MeetingMember member, boolean isExecutive) {
        return MeetingMemberElement.builder()
            .id(member.getUser().getId())
            .name(member.getUser().getUsername())
            .profileUrl(member.getUser().getProfileImage().getUrl())
            .attendenceScore(member.getUser().getAttendanceScore())
            .major(member.getUser().getMajor())
            .studentId(member.getUser().getStudentId())
            .phoneNumber(isExecutive ? member.getUser().getPhoneNumber() : null)
            .role(member.getRole())
            .build();
    }    
}
