package swe.second.team_matching_server.domain.history.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import swe.second.team_matching_server.domain.history.model.entity.AttendanceHistory;

public interface AttendanceHistoryRepository extends JpaRepository<AttendanceHistory, Long> {
    @Query("SELECT ah FROM AttendanceHistory ah WHERE ah.meeting.id = :meetingId AND ah.deletedAt IS NULL")
    Page<AttendanceHistory> findAllByMeetingId(@Param("meetingId") Long meetingId, Pageable pageable);

    @Query("SELECT ah FROM AttendanceHistory ah WHERE ah.history.id = :historyId AND ah.deletedAt IS NULL")
    List<AttendanceHistory> findAllByHistoryId(@Param("historyId") Long historyId);

    @Query("SELECT COUNT(ah) FROM AttendanceHistory ah WHERE ah.history.user.id = :userId AND ah.deletedAt IS NULL")
    int countAllByUserId(@Param("userId") String userId);

    @Query("SELECT ah FROM AttendanceHistory ah WHERE ah.history.user.id = :userId AND ah.deletedAt IS NULL")
    Page<AttendanceHistory> findAllByUserId(@Param("userId") String userId, Pageable pageable);
}
