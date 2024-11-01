package swe.second.team_matching_server.domain.history.service;

import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.history.repository.HistoryRepository;
import swe.second.team_matching_server.domain.history.model.mapper.HistoryMapper;
import swe.second.team_matching_server.domain.meeting.service.MeetingMemberService;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.history.model.dto.HistoryResponse;
import swe.second.team_matching_server.domain.user.service.UserService;
import swe.second.team_matching_server.domain.history.model.dto.HistoryElement;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;
import swe.second.team_matching_server.domain.history.model.exception.HistoryNoAccessException;
import swe.second.team_matching_server.domain.history.model.exception.HistoryNotFoundException;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.history.model.dto.HistoryCreateDto;
import swe.second.team_matching_server.domain.file.service.FileService;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.meeting.service.MeetingService;
import swe.second.team_matching_server.domain.history.service.AttendanceHistoryService;
import swe.second.team_matching_server.domain.history.model.entity.AttendanceHistory;
import swe.second.team_matching_server.domain.history.model.enums.AttendanceState;
import swe.second.team_matching_server.domain.history.model.exception.HistoryNoPermissionException;
import swe.second.team_matching_server.domain.history.model.dto.HistoryUpdateDto;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

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

    public Page<HistoryElement> findAllByUserId(Pageable pageable, String userId) {
        int attendanceCount = attendanceHistoryService.countAllByUserId(userId);
        List<AttendanceHistory> attendanceHistories = attendanceHistoryService.findAllByUserId(pageable,userId).getData();

        List<HistoryElement> histories = historyRepository.findAllByHistoryIds(attendanceHistories.stream()
            .map(attendanceHistory -> historyMapper.toHistoryElement(attendanceHistory.getHistory()))
            .collect(Collectors.toList()));

        return new PageImpl<>(histories, pageable, attendanceCount);
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

    @Transactional
    public HistoryResponse save(HistoryCreateDto historyCreateDto, List<FileCreateDto> fileCreateDtos, String userId) {
        Meeting meeting = meetingService.findById(historyCreateDto.getMeetingId());
        if (!meetingMemberService.isExecutive(historyCreateDto.getMeetingId(), userId)) {
            throw new HistoryNoPermissionException();
        }
        
        List<File> photos = fileService.saveAll(fileCreateDtos);
        User user = userService.findById(userId);
        History history = historyMapper.toHistory(historyCreateDto, user, meeting, photos);

        History savedHistory = historyRepository.save(history);

        List<User> members = meetingMemberService.findMemberUsersByMeetingId(historyCreateDto.getMeetingId());
        List<AttendanceHistory> attendanceHistories = attendanceHistoryService.saveAll(savedHistory, members, historyCreateDto.getAttendanceStates());
        savedHistory.updateAttendanceHistories(attendanceHistories);

        return historyMapper.toHistoryResponse(savedHistory, attendanceHistories);
    }

    @Transactional
    public HistoryResponse update(Long historyId, HistoryUpdateDto historyUpdateDto, List<FileCreateDto> fileCreateDtos, String userId) {
        History history = historyRepository.findById(historyId)
            .orElseThrow(() -> new HistoryNotFoundException());
        if (!meetingMemberService.isExecutive(history.getMeeting().getId(), userId)) {
            throw new HistoryNoPermissionException();
        }

        List<AttendanceHistory> attendanceHistories = attendanceHistoryService.updateAllByHistoryId(historyId, historyUpdateDto.getAttendance());
        history.updateAttendanceHistories(attendanceHistories);
        history.updateTitle(historyUpdateDto.getTitle());
        history.updateContent(historyUpdateDto.getContent());
        history.updateDate(historyUpdateDto.getDate());
        history.updateLocation(historyUpdateDto.getLocation());
        history.updateIsPublic(historyUpdateDto.isPublic());

        List<File> photos = fileService.updateAll(history.getPhotos(), fileCreateDtos);
        User user = userService.findById(userId);

        History updatedHistory = historyRepository.save(history);

        return historyMapper.toHistoryResponse(updatedHistory, attendanceHistories);
    }

    @Transactional
    public void delete(Long historyId, String userId) {
        History history = historyRepository.findById(historyId)
            .orElseThrow(() -> new HistoryNotFoundException());
        if (!meetingMemberService.isExecutive(history.getMeeting().getId(), userId)) {
            throw new HistoryNoPermissionException();
        }

        history.setDeletedAt(LocalDateTime.now());
        historyRepository.save(history);
    }
}
