package swe.second.team_matching_server.domain.meeting.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
  
}
