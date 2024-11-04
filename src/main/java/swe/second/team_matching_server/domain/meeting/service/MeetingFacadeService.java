package swe.second.team_matching_server.domain.meeting.service;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

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
import swe.second.team_matching_server.common.enums.FileFolder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class MeetingFacadeService {
  private final MeetingService meetingService;
  private final MeetingCommentService meetingCommentService;
  private final MeetingScrapService meetingScrapService;
  private final MeetingLikeService meetingLikeService;
  private final MeetingMemberService meetingMemberService;
  private final MeetingMapper meetingMapper;

  public MeetingFacadeService(
    MeetingService meetingService, 
    MeetingCommentService meetingCommentService, 
    MeetingScrapService meetingScrapService, 
    MeetingLikeService meetingLikeService, 
    MeetingMemberService meetingMemberService, 
    MeetingMapper meetingMapper) {
    this.meetingService = meetingService;
    this.meetingCommentService = meetingCommentService;
    this.meetingScrapService = meetingScrapService;
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

  @Transactional
  public MeetingResponse create(List<MultipartFile> files, MeetingCreateDto meetingCreateDto, String userId) {
    List<FileCreateDto> fileCreateDtos = files == null ? new ArrayList<>() 
      : files.stream().map(file -> FileCreateDto.builder()
        .file(file)
        .folder(FileFolder.MEETING)
        .build())
      .collect(Collectors.toList());

    Meeting meeting = meetingMapper.toEntity(meetingCreateDto);
    Meeting savedMeeting = meetingService.create(fileCreateDtos, meeting, userId);

    List<MeetingMember> members = meetingMemberService.findAllByMeetingId(savedMeeting.getId());

    MeetingResponse meetingResponse = meetingMapper.toResponse(savedMeeting, members);
    meetingResponse.setUserRole(MeetingMemberRole.LEADER);
    return meetingResponse;
  }

  @Transactional
  public MeetingResponse update(Long meetingId, List<MultipartFile> files, MeetingUpdateDto meetingUpdateDto, String userId) {
    List<FileCreateDto> fileCreateDtos = files == null ? new ArrayList<>() 
      : files.stream().map(file -> FileCreateDto.builder()
        .file(file)
        .folder(FileFolder.MEETING)
        .build())
      .collect(Collectors.toList());

    Meeting updatedMeeting = meetingService.update(meetingId, fileCreateDtos, meetingUpdateDto, userId);
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

  public String application(Long meetingId, String userId) {
    Meeting meeting = meetingService.findById(meetingId);

    return meetingMemberService.application(meeting, userId).getRole() 
      == MeetingMemberRole.REQUESTED ? "requested" : "accepted";
  }

  public void cancelApplication(Long meetingId, String userId) {
    Meeting meeting = meetingService.findById(meetingId);

    meetingMemberService.cancelApplication(meeting, userId);
  }

  public MeetingMemberRole getRoleByMeetingIdAndUserId(Long meetingId, String userId) {
    return meetingMemberService.findRoleByMeetingIdAndUserId(meetingId, userId);
  }

  public MeetingMembers updateRole(String userId, Long meetingId, String targetUserId, MeetingMemberRole role) {
    Meeting meeting = meetingService.findById(meetingId);

    return meetingMapper.toMeetingMembers(meetingMemberService.updateRole(userId, meeting, targetUserId, role));
  }

  public MeetingMembers acceptApplication(Long meetingId, String userId, String targetUserId) {
    Meeting meeting = meetingService.findById(meetingId);

    return meetingMapper.toMeetingMembers(meetingMemberService.acceptApplication(userId, meeting, targetUserId));
  }

  public MeetingMembers rejectApplication(Long meetingId, String userId, String targetUserId) {
    Meeting meeting = meetingService.findById(meetingId);

    return meetingMapper.toMeetingMembers(meetingMemberService.rejectApplication(userId, meeting, targetUserId));
  }

  public MeetingMembers leave(Long meetingId, String userId, String targetUserId) {
    Meeting meeting = meetingService.findById(meetingId);

    return meetingMapper.toMeetingMembers(meetingMemberService.leave(meeting, userId, targetUserId));
  }

  public MeetingMembers upgradeToCoLeader(Long meetingId, String userId, String targetUserId) {
    Meeting meeting = meetingService.findById(meetingId);

    return meetingMapper.toMeetingMembers(meetingMemberService.upgradeToCoLeader(userId, meeting, targetUserId));
  }

  public MeetingMembers downgradeToMember(Long meetingId, String userId, String targetUserId) {
    Meeting meeting = meetingService.findById(meetingId);

    return meetingMapper.toMeetingMembers(meetingMemberService.downgradeToMember(userId, meeting, targetUserId));
  }
}
