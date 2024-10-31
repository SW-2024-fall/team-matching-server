package swe.second.team_matching_server.domain.meeting.service;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import swe.second.team_matching_server.domain.meeting.service.MeetingCommentService;
import swe.second.team_matching_server.domain.meeting.service.MeetingHistoryService;
import swe.second.team_matching_server.domain.meeting.service.MeetingLikeService;
import swe.second.team_matching_server.domain.meeting.service.MeetingMemberService;
import swe.second.team_matching_server.domain.meeting.service.MeetingScrapService;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingResponse;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingCreateDto;
import swe.second.team_matching_server.domain.meeting.model.mapper.MeetingMapper;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingType;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingElement;
import swe.second.team_matching_server.domain.meeting.model.entity.MeetingMember;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingUpdateDto;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingMembers;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingFacadeService {
  private final MeetingService meetingService;
  private final MeetingCommentService meetingCommentService;
  private final MeetingScrapService meetingScrapService;
  private final MeetingHistoryService meetingHistoryService;
  private final MeetingLikeService meetingLikeService;
  private final MeetingMemberService meetingMemberService;
  private final MeetingMapper meetingMapper;

  public MeetingFacadeService(
    MeetingService meetingService, 
    MeetingCommentService meetingCommentService, 
    MeetingScrapService meetingScrapService, 
    MeetingHistoryService meetingHistoryService, 
    MeetingLikeService meetingLikeService, 
    MeetingMemberService meetingMemberService, 
    MeetingMapper meetingMapper) {
    this.meetingService = meetingService;
    this.meetingCommentService = meetingCommentService;
    this.meetingScrapService = meetingScrapService;
    this.meetingHistoryService = meetingHistoryService;
    this.meetingLikeService = meetingLikeService;
    this.meetingMemberService = meetingMemberService;
    this.meetingMapper = meetingMapper;
  }

  public Page<MeetingElement> findAllWithConditions(Pageable pageable, List<MeetingCategory> categories, MeetingType type, int min, int max) {
    Page<Meeting> meetings = meetingService.findAllWithConditions(pageable, categories, type, min, max);
    return meetings.map(meetingMapper::toMeetingElement);
  }

  public MeetingResponse findById(Long meetingId, String userId) {
    Meeting meeting = meetingService.findByIdWithThumbnailFiles(meetingId);
    List<MeetingMember> members = meetingMemberService.findAllByMeetingId(meetingId);
    MeetingMemberRole userRole = meetingMemberService.findRoleByMeetingIdAndUserId(meetingId, userId);

    if (userRole != MeetingMemberRole.LEADER && userRole != MeetingMemberRole.CO_LEADER) {
      members = members.stream()
        .filter(member -> member.getRole() != MeetingMemberRole.REQUESTED)
        .collect(Collectors.toList());
    }

    MeetingResponse meetingResponse = meetingMapper.toResponse(meeting, members);
    meetingResponse.setUserRole(userRole);

    return meetingResponse;
  }

  public MeetingResponse create(List<FileCreateDto> fileCreateDtos, MeetingCreateDto meetingCreateDto, String userId) {
    Meeting meeting = meetingMapper.toEntity(meetingCreateDto);
    Meeting savedMeeting = meetingService.create(fileCreateDtos, meeting, userId);
    List<MeetingMember> members = meetingMemberService.findAllByMeetingId(savedMeeting.getId());

    MeetingResponse meetingResponse = meetingMapper.toResponse(savedMeeting, members);
    meetingResponse.setUserRole(MeetingMemberRole.LEADER);
    return meetingResponse;
  }

  public MeetingResponse update(Long meetingId, List<String> deletedFileIds, List<FileCreateDto> fileCreateDtos, MeetingUpdateDto meetingUpdateDto, String userId) {
    Meeting updatedMeeting = meetingService.update(meetingId, deletedFileIds, fileCreateDtos, meetingUpdateDto, userId);
    List<MeetingMember> members = meetingMemberService.findAllByMeetingId(updatedMeeting.getId());

    MeetingResponse meetingResponse = meetingMapper.toResponse(updatedMeeting, members);
    meetingResponse.setUserRole(meetingMemberService.findRoleByMeetingIdAndUserId(meetingId, userId));
    return meetingResponse;
  }

  public void delete(Long meetingId, String userId) {
    meetingService.delete(meetingId, userId);
  }

  public MeetingMembers getMembersByMeetingId(Long meetingId) {
    meetingService.isExistOrThrow(meetingId);

    List<MeetingMember> members = meetingMemberService.findAllByMeetingId(meetingId);
    return meetingMapper.toMeetingMembers(members);
  }

  public void application(Long meetingId, String userId) {
    Meeting meeting = meetingService.findById(meetingId);

    meetingMemberService.application(meeting, userId);
  }

  public void cancelApplication(Long meetingId, String userId) {
    Meeting meeting = meetingService.findById(meetingId);

    meetingMemberService.cancelApplication(meeting, userId);
  }
}
