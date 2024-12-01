package swe.second.team_matching_server.domain.history.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import swe.second.team_matching_server.domain.history.model.entity.History;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("SELECT h FROM History h WHERE h.meeting.id = :meetingId AND h.deletedAt is null")
    Page<History> findAllByMeetingId(Pageable pageable, Long meetingId);

    @Query("SELECT h FROM History h WHERE h.meeting.id = :meetingId AND h.isPublic = true AND h.deletedAt is null")
    Page<History> findAllPublicByMeetingId(Pageable pageable, Long meetingId);
}
