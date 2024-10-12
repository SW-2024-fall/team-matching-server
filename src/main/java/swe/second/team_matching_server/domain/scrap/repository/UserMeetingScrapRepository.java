package swe.second.team_matching_server.domain.scrap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swe.second.team_matching_server.domain.scrap.model.entity.UserMeetingScrap;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.user.model.entity.User;

import java.util.List;

@Repository
public interface UserMeetingScrapRepository extends JpaRepository<UserMeetingScrap, Long>{
  @Query("SELECT ums.meeting FROM UserMeetingScrap ums WHERE ums.user.id = :userId")
  List<Meeting> findScrapedMeetingsByUserId(@Param("userId") String userId);

  @Query("SELECT ums.user FROM UserMeetingScrap ums WHERE ums.meeting.id = :meetingId")
  List<User> findScrapedUsersByMeetingId(@Param("meetingId") Long meetingId);

  long countByMeetingId(Long meetingId);
  
  boolean existsByUserIdAndMeetingId(String userId, Long meetingId);
}
