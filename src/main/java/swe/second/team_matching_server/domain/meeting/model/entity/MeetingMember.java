package swe.second.team_matching_server.domain.meeting.model.entity;

import swe.second.team_matching_server.common.entity.Base;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;
import swe.second.team_matching_server.domain.user.model.entity.User;
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
@Table(name = "meeting_member", uniqueConstraints = {
  @UniqueConstraint(name="meeting_member_unique", columnNames = {"user_id", "meeting_id"})
})
@Filter(name = "deletedMeetingMemberFilter", condition = "deleted_at is null")
public class MeetingMember extends Base {
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
  private MeetingMemberRole role;

  public void updateRole(MeetingMemberRole role) {
    this.role = role;
  }
}
  
  
