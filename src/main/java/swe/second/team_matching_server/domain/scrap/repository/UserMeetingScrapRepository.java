package swe.second.team_matching_server.domain.scrap.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swe.second.team_matching_server.domain.scrap.model.entity.UserMeetingScrap;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.user.model.entity.User;

@Repository
public interface UserMeetingScrapRepository extends JpaRepository<UserMeetingScrap, Long>{
  @Query("SELECT ums.meeting FROM UserMeetingScrap ums WHERE ums.user.id = :userId")
  List<Meeting> findScrapedMeetingsByUserId(@Param("userId") String userId);

  @Query("SELECT ums.user FROM UserMeetingScrap ums WHERE ums.meeting.id = :meetingId")
  List<User> findScrapedUsersByMeetingId(@Param("meetingId") Long meetingId);

  @Query("SELECT ums FROM UserMeetingScrap ums WHERE ums.meeting = :meeting AND ums.user = :user")
  Optional<UserMeetingScrap> findByMeetingAndUser(@Param("meeting") Meeting meeting,
      @Param("user") User user);

  int countByMeetingId(Long meetingId);
  
  boolean existsByUserIdAndMeetingId(String userId, Long meetingId);

  @Query("SELECT ums.meeting FROM UserMeetingScrap ums WHERE ums.user.id = :userId")
  List<Meeting> findMeetingsByUserId(@Param("userId") String userId);
}
