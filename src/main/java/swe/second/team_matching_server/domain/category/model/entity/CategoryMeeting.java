package swe.second.team_matching_server.domain.category.model.entity;

import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;

import jakarta.persistence.CascadeType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
@Entity
@Table(name = "category_meetings", uniqueConstraints = @UniqueConstraint(columnNames = {"category_id", "meeting_id"}))
@Getter
@AllArgsConstructor
@Builder
public class CategoryMeeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;
}