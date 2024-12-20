package swe.second.team_matching_server.domain.history.model.entity;

import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.common.entity.Base;
import swe.second.team_matching_server.domain.history.model.enums.AttendanceState;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "attendance_history")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceHistory extends Base{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "history_id", nullable = false)
    private History history;

    @ManyToOne
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private byte awarded_score;
    
    @Column(nullable = false)
    private AttendanceState state;

    public void updateState(AttendanceState state) {
        this.state = state;
    }

    public void updateAwardedScore(byte awardedScore) {
        this.awarded_score = awardedScore;
    }
}
