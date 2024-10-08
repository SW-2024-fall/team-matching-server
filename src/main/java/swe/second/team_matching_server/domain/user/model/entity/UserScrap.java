package swe.second.team_matching_server.domain.user.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.common.entity.Base;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;
@Entity
@Table(name = "user_scraps", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "meeting_id"})
})
@Getter
@AllArgsConstructor
@Builder
public class UserScrap extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;
}
