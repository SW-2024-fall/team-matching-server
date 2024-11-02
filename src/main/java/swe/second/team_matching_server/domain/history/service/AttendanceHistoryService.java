package swe.second.team_matching_server.domain.history.service;

import swe.second.team_matching_server.domain.history.repository.AttendanceHistoryRepository;
import swe.second.team_matching_server.domain.history.model.dto.MemberAttendanceState;
import swe.second.team_matching_server.domain.history.model.enums.AttendanceState;
import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.history.model.entity.AttendanceHistory;
import swe.second.team_matching_server.domain.user.service.UserService;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional(readOnly = true)
public class AttendanceHistoryService {
    private final AttendanceHistoryRepository attendanceHistoryRepository;
    private final UserService userService;
    private final byte MAX_ATTENDANCE_SCORE = 100;
    private final byte MIN_ATTENDANCE_SCORE = 0;
    private final byte ATTENDED_SCORE = 1;
    private final byte LATE_PENALTY = -5;
    private final byte TRUANCY_PENALTY = -10;

    public AttendanceHistoryService(AttendanceHistoryRepository attendanceHistoryRepository, UserService userService) {
        this.attendanceHistoryRepository = attendanceHistoryRepository;
        this.userService = userService;
    }

    public List<AttendanceHistory> findAllByHistoryId(Long historyId) {
        return attendanceHistoryRepository.findAllByHistoryId(historyId);
    }

    public int countAllByUserId(String userId) {
        return attendanceHistoryRepository.countAllByUserId(userId);
    }

    public Page<AttendanceHistory> findAllByUserId(String userId, Pageable pageable) {
        return attendanceHistoryRepository.findAllByUserId(userId, pageable);
    }

    public Page<AttendanceHistory> findAllByMeetingId(Long meetingId, Pageable pageable) {
        return attendanceHistoryRepository.findAllByMeetingId(meetingId, pageable);
    }

    @Transactional
    private byte updateAttendanceScore(User user, AttendanceState attendanceState) {
        byte currentScore = user.getAttendanceScore();
        byte awardedScore = 0;

        if (attendanceState == AttendanceState.ATTENDED) {
            awardedScore = currentScore < MAX_ATTENDANCE_SCORE ? ATTENDED_SCORE : 0;
        } else if (attendanceState == AttendanceState.LATE) {
            awardedScore = (currentScore + LATE_PENALTY < MIN_ATTENDANCE_SCORE) ? currentScore : LATE_PENALTY;
        } else if (attendanceState == AttendanceState.TRUANCY) {
            awardedScore = (currentScore + TRUANCY_PENALTY < MIN_ATTENDANCE_SCORE) ? currentScore : TRUANCY_PENALTY;
        }
        user.updateAttendanceScore((byte) (user.getAttendanceScore() + awardedScore));
        userService.save(user);
        return awardedScore;
    }

    @Transactional
    public byte updateAttendanceScore(User user, byte previousRewardedScore, AttendanceState currentState) {
        int initialScore = user.getAttendanceScore() - previousRewardedScore;
        if (initialScore < MIN_ATTENDANCE_SCORE) {
            initialScore = MIN_ATTENDANCE_SCORE;
        } else if (initialScore > MAX_ATTENDANCE_SCORE) {
            initialScore = MAX_ATTENDANCE_SCORE;
        }
        user.updateAttendanceScore((byte) initialScore);

        User savedUser = userService.save(user);
        return updateAttendanceScore(savedUser, currentState);
    }

    @Transactional
    public List<AttendanceHistory> saveAll(History history, List<User> members, List<MemberAttendanceState> attendanceStates) {
        List<AttendanceHistory> attendanceHistories = new ArrayList<>(members.size());
        for (int i = 0; i < members.size(); i++) {
            User user = userService.findById(members.get(i).getId());
            byte awardedScore = updateAttendanceScore(user, attendanceStates.get(i).getAttendanceState());

            AttendanceHistory attendanceHistory = AttendanceHistory.builder()
                .history(history)
                .meeting(history.getMeeting())
                .user(members.get(i))
                .state(attendanceStates.get(i).getAttendanceState())
                .awarded_score(awardedScore)
                .build();
            attendanceHistories.add(attendanceHistory);
        }
        return attendanceHistoryRepository.saveAll(attendanceHistories);
    }

    @Transactional
    public AttendanceHistory save(History history, MemberAttendanceState attendanceState) {
        User user = userService.findById(attendanceState.getUserId());
        byte awardedScore = updateAttendanceScore(user, attendanceState.getAttendanceState());

        return attendanceHistoryRepository.save(AttendanceHistory.builder()
            .history(history)
            .meeting(history.getMeeting())
            .user(user)
            .state(attendanceState.getAttendanceState())
            .awarded_score(awardedScore)
            .build());
    }

    @Transactional
    public List<AttendanceHistory> updateAllByHistoryId(History history, List<MemberAttendanceState> attendanceStates) {
        List<AttendanceHistory> attendanceHistories = attendanceHistoryRepository.findAllByHistoryId(history.getId());
        List<AttendanceHistory> updatedAttendanceHistories = new ArrayList<>(attendanceHistories.size());

        for (int i = 0; i < attendanceStates.size(); i++) {
            final int index = i;
            AttendanceHistory attendanceHistory = attendanceHistories.stream()
                .filter(attendance -> attendance.getUser().getId().equals(attendanceStates.get(index).getUserId()))
                .findFirst().get();

            if (attendanceHistory != null) {
                byte awardedScore = updateAttendanceScore(attendanceHistory.getUser(), 
                    attendanceHistory.getAwarded_score(), attendanceStates.get(index).getAttendanceState());

                attendanceHistory.updateState(attendanceStates.get(index).getAttendanceState());
                attendanceHistory.updateAwardedScore(awardedScore);

                updatedAttendanceHistories.add(attendanceHistoryRepository.save(attendanceHistory));
            } else {
                updatedAttendanceHistories.add(save(history, attendanceStates.get(index)));
            }
        }
        return updatedAttendanceHistories;
    }

    @Transactional
    public void deleteAllByHistoryId(Long historyId) {
        List<AttendanceHistory> attendanceHistories = attendanceHistoryRepository.findAllByHistoryId(historyId);

        for (AttendanceHistory attendanceHistory : attendanceHistories) {
            User user = attendanceHistory.getUser();
            int initialScore = user.getAttendanceScore() - attendanceHistory.getAwarded_score();
            if (initialScore < MIN_ATTENDANCE_SCORE) {
                initialScore = MIN_ATTENDANCE_SCORE;
            } else if (initialScore > MAX_ATTENDANCE_SCORE) {
                initialScore = MAX_ATTENDANCE_SCORE;
            }
            user.updateAttendanceScore((byte) initialScore);
            userService.save(user);
            attendanceHistory.setDeletedAt(LocalDateTime.now());
        }
        attendanceHistoryRepository.saveAll(attendanceHistories);
    }
}
