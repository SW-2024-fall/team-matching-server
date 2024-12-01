package swe.second.team_matching_server.domain.like.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import swe.second.team_matching_server.domain.like.model.entity.UserMeetingLike;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.user.model.entity.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface UserMeetingLikeRepository extends JpaRepository<UserMeetingLike, Long>{
  @Query("SELECT ums.meeting FROM UserMeetingLike ums WHERE ums.user.id = :userId")
  List<Meeting> findLikedMeetingsByUserId(@Param("userId") String userId);

  @Query("SELECT ums.user FROM UserMeetingLike ums WHERE ums.meeting.id = :meetingId")
  List<User> findLikedUsersByMeetingId(@Param("meetingId") Long meetingId);

  @Query("SELECT ums FROM UserMeetingLike ums WHERE ums.meeting = :meeting AND ums.user = :user")
  Optional<UserMeetingLike> findByMeetingAndUser(@Param("meeting") Meeting meeting, @Param("user") User user);

  int countByMeetingId(Long meetingId);

  boolean existsByUserIdAndMeetingId(String userId, Long meetingId);

  @Query("SELECT ums.meeting FROM UserMeetingLike ums WHERE ums.user.id = :userId")
  List<Meeting> findMeetingsByUserId(@Param("userId") String userId);
}
