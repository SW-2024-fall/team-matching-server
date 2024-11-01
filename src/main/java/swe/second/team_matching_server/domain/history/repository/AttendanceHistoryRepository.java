package swe.second.team_matching_server.domain.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import swe.second.team_matching_server.domain.history.model.entity.AttendanceHistory;

public interface AttendanceHistoryRepository extends JpaRepository<AttendanceHistory, Long> {
    @Query("SELECT ah FROM AttendanceHistory ah WHERE ah.meeting.id = :meetingId")
    Page<AttendanceHistory> findAllByMeetingId(@Param("meetingId") Long meetingId, Pageable pageable);
}
