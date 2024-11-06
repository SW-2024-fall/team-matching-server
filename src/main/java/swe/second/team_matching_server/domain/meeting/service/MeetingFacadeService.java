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
import swe.second.team_matching_server.domain.like.service.UserMeetingLikeService;
import swe.second.team_matching_server.domain.scrap.service.UserMeetingScrapService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class MeetingFacadeService {
  private final MeetingService meetingService;
  private final MeetingCommentService meetingCommentService;
  private final UserMeetingScrapService userMeetingScrapService;
  private final UserMeetingLikeService userMeetingLikeService;
  private final MeetingMemberService meetingMemberService;
  private final MeetingMapper meetingMapper;

  public MeetingFacadeService(
    MeetingService meetingService,  
    MeetingCommentService meetingCommentService, 
    UserMeetingScrapService userMeetingScrapService, 
    UserMeetingLikeService userMeetingLikeService,
    MeetingMemberService meetingMemberService, 
    MeetingMapper meetingMapper) {
    this.meetingService = meetingService;
    this.meetingCommentService = meetingCommentService;
    this.userMeetingScrapService = userMeetingScrapService;
    this.userMeetingLikeService = userMeetingLikeService;
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
    boolean isExecutive = meetingMemberService.isExecutive(meetingId, userId);
    if (!isExecutive) {
      members = members.stream()
        .filter(member -> member.getRole() != MeetingMemberRole.REQUESTED)
        .collect(Collectors.toList());
    }

    int likes = userMeetingLikeService.countByMeetingId(meetingId);
    int comments = meetingCommentService.countByMeetingId(meetingId);
    int scraps = userMeetingScrapService.countByMeetingId(meetingId);
    boolean isLiked = userMeetingLikeService.isLiked(userId, meetingId);
    boolean isScraped = userMeetingScrapService.isScraped(userId, meetingId);

    meeting.setLikeCount(likes);
    meeting.setCommentCount(comments);
    meeting.setScrapCount(scraps);

    MeetingResponse meetingResponse = meetingMapper.toResponse(meeting, members, isExecutive, isLiked, isScraped);
    meetingResponse.setUserRole(meetingMemberService.findRoleByMeetingIdAndUserId(meetingId, userId));

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

    MeetingResponse meetingResponse = meetingMapper.toResponse(savedMeeting, members, true, false, false);
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
    boolean isLiked = userMeetingLikeService.isLiked(userId, meetingId);
    boolean isScraped = userMeetingScrapService.isScraped(userId, meetingId);

    MeetingResponse meetingResponse = meetingMapper.toResponse(updatedMeeting, members, true, isLiked, isScraped);
    meetingResponse.setUserRole(meetingMemberService.findRoleByMeetingIdAndUserId(meetingId, userId));
    return meetingResponse;
  }

  public void delete(Long meetingId, String userId) {
    meetingService.delete(meetingId, userId);
  }

  public MeetingMembers getMembersByMeetingId(Long meetingId, String userId) {
    meetingService.isExistOrThrow(meetingId);

    List<MeetingMember> members;
    boolean isExecutive = meetingMemberService.isExecutive(meetingId, userId);
    if (!isExecutive) {
      members = meetingMemberService.findAllByMeetingId(meetingId);
    } else {
      members = meetingMemberService.findAllMembersByMeetingId(meetingId);
    }

    return meetingMapper.toMeetingMembers(members, isExecutive);
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

    return meetingMapper.toMeetingMembers(meetingMemberService.updateRole(userId, meeting, targetUserId, role), true);
  }

  public MeetingMembers acceptApplication(Long meetingId, String userId, String targetUserId) {
    Meeting meeting = meetingService.findById(meetingId);

    return meetingMapper.toMeetingMembers(meetingMemberService.acceptApplication(userId, meeting, targetUserId), true);
  }

  public MeetingMembers rejectApplication(Long meetingId, String userId, String targetUserId) {
    Meeting meeting = meetingService.findById(meetingId);

    return meetingMapper.toMeetingMembers(meetingMemberService.rejectApplication(userId, meeting, targetUserId), true);
  }

  public MeetingMembers leave(Long meetingId, String userId, String targetUserId) {
    Meeting meeting = meetingService.findById(meetingId);

    return meetingMapper.toMeetingMembers(meetingMemberService.leave(meeting, userId, targetUserId), true);
  }

  public MeetingMembers upgradeToCoLeader(Long meetingId, String userId, String targetUserId) {
    Meeting meeting = meetingService.findById(meetingId);

    return meetingMapper.toMeetingMembers(meetingMemberService.upgradeToCoLeader(userId, meeting, targetUserId), true);
  }

  public MeetingMembers downgradeToMember(Long meetingId, String userId, String targetUserId) {
    Meeting meeting = meetingService.findById(meetingId);

    return meetingMapper.toMeetingMembers(meetingMemberService.downgradeToMember(userId, meeting, targetUserId), true);
  }
}
