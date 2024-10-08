package swe.second.team_matching_server.domain.category.model.entity;

import swe.second.team_matching_server.common.entity.Base;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
@Entity
@Table(name = "category_meetings")
@Getter
@AllArgsConstructor
@Builder
public class CategoryMeeting extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;
}