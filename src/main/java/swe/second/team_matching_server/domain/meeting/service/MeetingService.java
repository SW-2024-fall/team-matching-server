package swe.second.team_matching_server.domain.meeting.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import swe.second.team_matching_server.domain.meeting.repository.MeetingRepository;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.file.service.FileMeetingService;
import swe.second.team_matching_server.domain.meeting.model.exception.MeetingNotFoundException;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingType;
import swe.second.team_matching_server.domain.meeting.model.exception.MeetingInvalidParticipantException;
import swe.second.team_matching_server.domain.meeting.model.exception.MeetingInvalidDateException;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.meeting.model.entity.MeetingMember;
import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.meeting.model.exception.MeetingInvalidDaysException;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;
import swe.second.team_matching_server.domain.meeting.model.exception.MeetingNoPermissionException;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingUpdateDto;
import swe.second.team_matching_server.domain.file.service.FileService;

import java.util.List;
import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final FileMeetingService fileMeetingService;
    private final MeetingMemberService meetingMemberService;
    private final FileService fileService;

    public MeetingService(MeetingRepository meetingRepository, 
        FileMeetingService fileMeetingService, 
        MeetingMemberService meetingMemberService,
        FileService fileService) {
        this.meetingRepository = meetingRepository;
        this.fileMeetingService = fileMeetingService;
        this.meetingMemberService = meetingMemberService;
        this.fileService = fileService;
    }

    public Page<Meeting> findAllWithConditions(Pageable pageable, List<MeetingCategory> categories, MeetingType type, int min, int max) {
        // List<MeetingElement> meetings = meetingRepository.findAllWithConditions(categories, type, min, max);
        Page<Meeting> meetings = meetingRepository.findAll(pageable);
        // int likeCount = meetingLikeService.countLikesByMeetingId(meetings.getContent().get(0).getId());
        // int commentCount = meetingCommentService.countCommentsByMeetingId(meetings.getContent().get(0).getId());

        meetings.getContent().forEach(meeting -> {
            int currentParticipant = meetingMemberService.countMembersByMeetingId(meeting.getId()); 
            int likeCount = 1;
            int commentCount = 1;
            
            meeting.setCurrentParticipant(currentParticipant);
            meeting.setLikeCount(likeCount);
            meeting.setCommentCount(commentCount);
        });

        return meetings;
    }

    public boolean isExistOrThrow(Long meetingId) {
        if (!meetingRepository.existsById(meetingId)) {
            throw new MeetingNotFoundException();
        }
        return true;
    }

    public Meeting findById(Long meetingId) {
        return meetingRepository.findById(meetingId)
            .orElseThrow(() -> new MeetingNotFoundException());
    }

    public Meeting findByIdWithThumbnailFiles(Long meetingId) {
        return meetingRepository.findByIdWithThumbnailFiles(meetingId)
            .orElseThrow(() -> new MeetingNotFoundException());
    }

    public List<MeetingMember> findMembersById(Long meetingId) {
        return meetingRepository.findMembersById(meetingId);
    }

    public List<History> findHistoriesById(Long meetingId) {
        return meetingRepository.findHistoriesById(meetingId);
    }

    private boolean isValidMeeting(Meeting meeting) {
        if (meeting.getMinParticipant() < 2 || meeting.getMaxParticipant() > 99) {
            throw new MeetingInvalidParticipantException();
        }
        if (meeting.getMinParticipant() > meeting.getMaxParticipant()) {
            throw new MeetingInvalidParticipantException();
        }
        if (meeting.getStartDate().isAfter(meeting.getEndDate())) {
            throw new MeetingInvalidDateException();
        }
        if (meeting.getStartTime().isAfter(meeting.getEndTime())) {
            throw new MeetingInvalidDateException();
        }
        if (meeting.getEndDate().isBefore(LocalDate.now())) {
            throw new MeetingInvalidDateException();
        }
        if (meeting.getType() == MeetingType.REGULAR && meeting.getDays().size() == 0) {
            throw new MeetingInvalidDaysException();
        }
        return true;
    }

    @Transactional
    public Meeting update(Long meetingId, List<FileCreateDto> updateFileDtos, MeetingUpdateDto meetingUpdateDto, String userId) {
        MeetingMemberRole role = meetingMemberService.findRoleByMeetingIdAndUserId(meetingId, userId);
        if (role != MeetingMemberRole.LEADER) {
            throw new MeetingNoPermissionException();
        }

        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new MeetingNotFoundException());
        List<File> existingFiles = meeting.getThumbnailFiles();
        List<File> files = fileService.updateAll(existingFiles, updateFileDtos);
        
        meeting.updateThumbnailFiles(files);
        if (meetingUpdateDto.getName() != null) {
            meeting.updateName(meetingUpdateDto.getName());
        }
        if (meetingUpdateDto.getTitle() != null) {
            meeting.updateTitle(meetingUpdateDto.getTitle());
        }
        if (meetingUpdateDto.getCategories() != null) {
            meeting.updateCategories(meetingUpdateDto.getCategories());
        }
        if (meetingUpdateDto.getContent() != null) {
            meeting.updateContent(meetingUpdateDto.getContent());
        }
        if (meetingUpdateDto.getFeatures() != null) {
            meeting.updateFeatures(meetingUpdateDto.getFeatures());
        }
        if (meetingUpdateDto.getLocation() != null) {
            meeting.updateLocation(meetingUpdateDto.getLocation());
        }
        if (meetingUpdateDto.getDays() != null) {
            meeting.updateDays(meetingUpdateDto.getDays());
        }
        if (meetingUpdateDto.getEndDate() != null) {
            meeting.updateDate(meeting.getStartDate(), meetingUpdateDto.getEndDate());
        }
        if (meetingUpdateDto.getStartTime() != null) {
            meeting.updateTime(meetingUpdateDto.getStartTime(), meetingUpdateDto.getEndTime());
        }
        if (meetingUpdateDto.getMeta() != null) {
            meeting.updateMeta(meetingUpdateDto.getMeta());
        }

        return meeting;
    }

    @Transactional
    public Meeting create(List<FileCreateDto> fileCreateDtos, Meeting meeting, String leaderId) {
        isValidMeeting(meeting);

        fileMeetingService.saveAllByMeeting(meeting, fileCreateDtos);
        meetingRepository.save(meeting);

        meetingMemberService.createLeader(meeting, leaderId);
        Meeting savedMeeting = meetingRepository.findByIdWithThumbnailFiles(meeting.getId())
            .orElseThrow(() -> new MeetingNotFoundException());

        return savedMeeting;
    }

    @Transactional
    public void delete(Long meetingId, String userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new MeetingNotFoundException());

        MeetingMemberRole role = meetingMemberService.findRoleByMeetingIdAndUserId(meetingId, userId);
        if (role != MeetingMemberRole.LEADER) {
            log.error("Meeting delete failed. User {} does not have permission to delete meeting {}: {}", userId, meetingId, role);
            throw new MeetingNoPermissionException();
        }

        meeting.delete();
    }
}
