package swe.second.team_matching_server.domain.history.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swe.second.team_matching_server.domain.history.model.entity.History;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    Page<History> findAllByMeetingId(Long meetingId, Pageable pageable);
}
