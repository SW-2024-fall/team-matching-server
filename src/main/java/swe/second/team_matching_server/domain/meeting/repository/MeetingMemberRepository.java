package swe.second.team_matching_server.domain.meeting.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import swe.second.team_matching_server.domain.meeting.model.entity.MeetingMember;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long>{
  @Query("SELECT mm.user FROM MeetingMember mm WHERE mm.meeting.id = :meetingId")
  List<User> findUsersByMeetingId(@Param("meetingId") Long meetingId);

  List<MeetingMember> findAllByMeetingId(Long meetingId);
  List<MeetingMember> findAllByUserId(String userId);

  @Query("SELECT mm.role FROM MeetingMember mm WHERE mm.meeting.id = :meetingId AND mm.user.id = :userId")
  Optional<MeetingMemberRole> findRoleByMeetingIdAndUserId(@Param("meetingId") Long meetingId, @Param("userId") String userId);

  @Query("SELECT mm.meeting FROM MeetingMember mm WHERE mm.user.id = :userId")
  List<Meeting> findMeetingsByUserId(@Param("userId") String userId);

  Optional<MeetingMember> findByMeetingIdAndUserId(Long meetingId, String userId);

  @Query("SELECT COUNT(mm) FROM MeetingMember mm WHERE mm.meeting.id = :meetingId AND mm.role IN (LEADER, CO_LEADER, MEMBER)")
  int countMembersByMeetingId(@Param("meetingId") Long meetingId);
}
