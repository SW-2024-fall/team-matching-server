package swe.second.team_matching_server.domain.user.model.entity;

import swe.second.team_matching_server.common.entity.Base;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.user.model.enums.UserMeetingRole;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.Filter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_meeting", uniqueConstraints = {
  @UniqueConstraint(name="user_meeting_unique", columnNames = {"user_id", "meeting_id"})
})
@Filter(name = "deletedUserMeetingFilter", condition = "deleted_at is null")
public class UserMeeting extends Base {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(nullable = false, name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(nullable = false, name = "meeting_id")
  private Meeting meeting;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserMeetingRole role;

  public void updateRole(UserMeetingRole role) {
    this.role = role;
  }
}
  
  
