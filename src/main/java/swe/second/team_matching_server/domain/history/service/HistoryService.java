package swe.second.team_matching_server.domain.history.service;

import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.history.repository.HistoryRepository;
import swe.second.team_matching_server.domain.history.model.mapper.HistoryMapper;
import swe.second.team_matching_server.domain.meeting.service.MeetingMemberService;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.history.model.dto.HistoryResponse;
import swe.second.team_matching_server.domain.user.service.UserService;
import swe.second.team_matching_server.domain.history.model.dto.HistoryElement;
import swe.second.team_matching_server.domain.history.model.exception.HistoryNoAccessException;
import swe.second.team_matching_server.domain.history.model.exception.HistoryNotFoundException;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.history.model.dto.HistoryCreateDto;
import swe.second.team_matching_server.domain.file.service.FileService;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.meeting.service.MeetingService;
import swe.second.team_matching_server.domain.history.model.entity.AttendanceHistory;
import swe.second.team_matching_server.domain.history.model.exception.HistoryNoPermissionException;
import swe.second.team_matching_server.domain.history.model.dto.HistoryUpdateDto;
import swe.second.team_matching_server.common.enums.FileFolder;
import swe.second.team_matching_server.common.exception.BadRequestException;
import swe.second.team_matching_server.domain.history.model.exception.HistoryInvalidAttendanceHistoryException;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final HistoryMapper historyMapper;
    private final MeetingMemberService meetingMemberService;
    private final UserService userService;
    private final FileService fileService;
    private final MeetingService meetingService;
    private final AttendanceHistoryService attendanceHistoryService;

    public HistoryService(
        HistoryRepository historyRepository, 
        HistoryMapper historyMapper, 
        MeetingMemberService meetingMemberService,
        UserService userService,
        FileService fileService,
        MeetingService meetingService,
        AttendanceHistoryService attendanceHistoryService
    ) {
        this.historyRepository = historyRepository;
        this.historyMapper = historyMapper;
        this.meetingMemberService = meetingMemberService;
        this.userService = userService;
        this.fileService = fileService;
        this.meetingService = meetingService;
        this.attendanceHistoryService = attendanceHistoryService;
    }

    public Page<HistoryElement> findAllByUserEmail(Pageable pageable, String userEmail) {
        User user = userService.findByEmail(userEmail);
        return findAllByUserId(pageable, user.getId());
    }

    public Page<HistoryElement> findAllByUserId(Pageable pageable, String userId) {
        int attendanceCount = attendanceHistoryService.countAllByUserId(userId);
        List<AttendanceHistory> attendanceHistories 
            = attendanceHistoryService.findAllByUserId(userId, pageable).getContent();

        List<HistoryElement> histories = attendanceHistories.stream()
            .map(attendanceHistory -> historyMapper.toHistoryElement(attendanceHistory.getHistory()))
            .collect(Collectors.toList());

        return new PageImpl<>(histories, pageable, attendanceCount);
    }

    public Page<HistoryElement> findAllByMeetingIdAndUserEmail(Pageable pageable, Long meetingId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        return findAllByMeetingId(pageable, meetingId, user.getId());
    }

    public Page<HistoryElement> findAllByMeetingId(Pageable pageable, Long meetingId, String userId) {
        boolean isMember = meetingMemberService.isMember(meetingId, userId);

        if (isMember) {
            return historyRepository.findAllByMeetingId(pageable, meetingId)
                .map(history -> historyMapper.toHistoryElement(history));
        }
        return historyRepository.findAllPublicByMeetingId(pageable, meetingId)
            .map(history -> historyMapper.toHistoryElement(history));
    }

    public HistoryResponse findByIdAndUserEmail(Long historyId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        return findById(historyId, user.getId());
    }

    public HistoryResponse findById(Long historyId, String userId) {
        List<AttendanceHistory> attendanceHistories = attendanceHistoryService.findAllByHistoryId(historyId);
        History history = historyRepository.findById(historyId)
            .orElseThrow(() -> new HistoryNotFoundException());

        if (!meetingMemberService.isMember(history.getMeeting().getId(), userId) && !history.isPublic()) {
            throw new HistoryNoAccessException();
        }
        return historyMapper.toHistoryResponse(history, attendanceHistories);
    }

    public HistoryResponse saveWithUserEmail(HistoryCreateDto historyCreateDto, List<MultipartFile> files, String userEmail) {
        User user = userService.findByEmail(userEmail);
        return save(historyCreateDto, files, user.getId());
    }

    @Transactional
    public HistoryResponse save(HistoryCreateDto historyCreateDto, List<MultipartFile> files, String userId) {
        Meeting meeting = meetingService.findById(historyCreateDto.getMeetingId());
        if (!meetingMemberService.isExecutive(historyCreateDto.getMeetingId(), userId)) {
            throw new HistoryNoPermissionException();
        }
        
        // members.user가 모두 attendeesUsers와 1대 1로 매칭되어야함. 아니면 예외 발생
        int memberCount = meetingMemberService.countMembersByMeetingId(historyCreateDto.getMeetingId());
        List<User> attendeesUsers = meetingMemberService.findMemberUsersByMeetingId(historyCreateDto.getMeetingId());

        if (memberCount != attendeesUsers.size() 
            || memberCount != historyCreateDto.getAttendanceStates().size()) {
            throw new HistoryInvalidAttendanceHistoryException();
        }

        List<FileCreateDto> fileCreateDtos = files == null ? new ArrayList<>()
            : files.stream().map(file -> FileCreateDto.builder()
            .file(file)
            .folder(FileFolder.HISTORY)
            .build())
            .collect(Collectors.toList());

        User user = userService.findById(userId);
        List<File> photos = fileService.saveAll(fileCreateDtos);
        History history = historyMapper.toHistory(historyCreateDto, user, meeting, photos);

        History savedHistory = historyRepository.save(history);

        List<AttendanceHistory> attendanceHistories = attendanceHistoryService.saveAll(savedHistory, attendeesUsers, historyCreateDto.getAttendanceStates());
        savedHistory.updateAttendanceHistories(attendanceHistories);

        return historyMapper.toHistoryResponse(savedHistory, attendanceHistories);
    }

    public HistoryResponse updateWithUserEmail(Long historyId, HistoryUpdateDto historyUpdateDto, List<MultipartFile> files, String userEmail) {
        User user = userService.findByEmail(userEmail);
        return update(historyId, historyUpdateDto, files, user.getId());
    }

    @Transactional
    public HistoryResponse update(Long historyId, HistoryUpdateDto historyUpdateDto, List<MultipartFile> files, String userId) {
        History history = historyRepository.findById(historyId)
            .orElseThrow(() -> new HistoryNotFoundException());
        if (!meetingMemberService.isExecutive(history.getMeeting().getId(), userId)) {
            throw new HistoryNoPermissionException();
        }

        // members.user가 모두 attendeesUsers와 1대 1로 매칭되어야함. 아니면 예외 발생
        int memberCount = meetingMemberService.countMembersByMeetingId(history.getMeeting().getId());
        List<User> attendeesUsers = meetingMemberService.findMemberUsersByMeetingId(history.getMeeting().getId());

        if (memberCount != attendeesUsers.size() 
            || memberCount != historyUpdateDto.getAttendanceStates().size()) {
            throw new HistoryInvalidAttendanceHistoryException();
        }

        List<FileCreateDto> fileCreateDtos = files == null ? new ArrayList<>()
            : files.stream().map(file -> FileCreateDto.builder()
            .file(file)
            .folder(FileFolder.HISTORY)
            .build())
            .collect(Collectors.toList());
        List<File> photos = fileService.updateAll(history.getPhotos(), fileCreateDtos);

        List<AttendanceHistory> attendanceHistories = attendanceHistoryService
            .updateAllByHistoryId(history, historyUpdateDto.getAttendanceStates());
        history.updateAttendanceHistories(attendanceHistories);
        history.updateTitle(historyUpdateDto.getTitle());
        history.updateContent(historyUpdateDto.getContent());
        history.updateDate(historyUpdateDto.getDate());
        history.updateLocation(historyUpdateDto.getLocation());
        history.updateIsPublic(historyUpdateDto.isPublic());
        history.updatePhotos(photos);

        History updatedHistory = historyRepository.save(history);

        return historyMapper.toHistoryResponse(updatedHistory, attendanceHistories);
    }

    public void deleteWithUserEmail(Long historyId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        delete(historyId, user.getId());
    }

    @Transactional
    public void delete(Long historyId, String userId) {
        History history = historyRepository.findById(historyId)
            .orElseThrow(() -> new HistoryNotFoundException());

        if (history.getDeletedAt() != null) {
            throw new BadRequestException();
        }

        if (!meetingMemberService.isExecutive(history.getMeeting().getId(), userId)) {
            throw new HistoryNoPermissionException();
        }

        attendanceHistoryService.deleteAllByHistoryId(historyId);

        history.setDeletedAt(LocalDateTime.now());
        historyRepository.save(history);
    }
}
