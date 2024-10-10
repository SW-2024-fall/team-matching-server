package swe.second.team_matching_server.domain.history.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swe.second.team_matching_server.domain.history.model.entity.History;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByMeetingId(Long meetingId);
}
