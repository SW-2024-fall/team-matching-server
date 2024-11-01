package swe.second.team_matching_server.domain.meeting.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.meeting.model.entity.MeetingMember;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Query("SELECT m FROM Meeting m LEFT JOIN FETCH m.thumbnailFiles WHERE m.id = :meetingId")
    Optional<Meeting> findByIdWithThumbnailFiles(Long meetingId);

    @Query("SELECT m FROM Meeting m LEFT JOIN FETCH m.histories WHERE m.id = :meetingId")
    Optional<Meeting> findByIdWithHistories(Long meetingId);

    @Query("SELECT m FROM Meeting m LEFT JOIN FETCH m.members WHERE m.id = :meetingId")
    Optional<Meeting> findByIdWithMembers(Long meetingId);

    @Query("SELECT h FROM History h WHERE h.meeting.id = :meetingId")
    List<History> findHistoriesById(Long meetingId);

    @Query("SELECT m.members FROM Meeting m WHERE m.id = :meetingId")
    List<MeetingMember> findMembersById(Long meetingId);

    @Query("SELECT m FROM Meeting m LEFT JOIN FETCH m.thumbnailFiles")
    Page<Meeting> findAll(Pageable pageable);
}
