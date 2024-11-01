package swe.second.team_matching_server.domain.history.service;

import swe.second.team_matching_server.domain.history.repository.AttendanceHistoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
public class AttendanceHistoryService {
    private final AttendanceHistoryRepository attendanceHistoryRepository;

    public AttendanceHistoryService(AttendanceHistoryRepository attendanceHistoryRepository) {
        this.attendanceHistoryRepository = attendanceHistoryRepository;
    }
}
