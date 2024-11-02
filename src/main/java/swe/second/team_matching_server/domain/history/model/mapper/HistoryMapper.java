package swe.second.team_matching_server.domain.history.model.mapper;

import swe.second.team_matching_server.domain.history.model.dto.HistoryElement;
import swe.second.team_matching_server.domain.history.model.entity.AttendanceHistory;
import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.user.model.mapper.UserMapper;
import swe.second.team_matching_server.domain.file.model.mapper.FileMapper;
import swe.second.team_matching_server.domain.history.model.dto.HistoryResponse;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.history.model.dto.HistoryCreateDto;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.history.model.enums.AttendanceState;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class HistoryMapper {
    private final UserMapper userMapper;
    private final FileMapper fileMapper;

    public HistoryMapper(UserMapper userMapper, FileMapper fileMapper) {
        this.userMapper = userMapper;
        this.fileMapper = fileMapper;
    }

    public History toHistory(HistoryCreateDto historyCreateDto, User user, Meeting meeting, List<File> photos) {
        return History.builder()
            .user(user)
            .meeting(meeting)
            .title(historyCreateDto.getTitle())
            .content(historyCreateDto.getContent())
            .isPublic(historyCreateDto.isPublic())
            .date(historyCreateDto.getDate())
            .location(historyCreateDto.getLocation())
            .photos(photos)
            .build();
    }

    public HistoryElement toHistoryElement(History history) {
        return HistoryElement.builder()
            .id(history.getId())
            .thumbnailUrl(history.getPhotos() != null && !history.getPhotos().isEmpty() 
                ? history.getPhotos().get(0).getUrl() 
                : null)
            .content(history.getContent())
            .writer(userMapper.toUserElement(history.getUser()))
            .meetingId(history.getMeeting().getId())
            .meetingName(history.getMeeting().getName())
            .build();
    }

    public HistoryResponse toHistoryResponse(History history, List<AttendanceHistory> attendanceHistories) {
        return HistoryResponse.builder()
            .id(history.getId())
            .title(history.getTitle())
            .content(history.getContent())
            .date(history.getDate())
            .location(history.getLocation())
            .meetingId(history.getMeeting().getId())
            .meetingName(history.getMeeting().getName())
            .writer(userMapper.toUserElement(history.getUser()))
            .files(history.getPhotos() != null ? history.getPhotos().stream()
                .map(fileMapper::toFileResponse)
                .collect(Collectors.toList()) : null)
            .attendees(attendanceHistories.stream()
                .filter(attendanceHistory -> attendanceHistory.getState() == AttendanceState.ATTENDED 
                    || attendanceHistory.getState() == AttendanceState.LATE)
                .map(attendanceHistory -> userMapper.toUserElement(attendanceHistory.getUser()))
                .collect(Collectors.toList()))
            .build();
    }
}
