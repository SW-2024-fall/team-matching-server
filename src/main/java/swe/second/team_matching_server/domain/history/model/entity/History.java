package swe.second.team_matching_server.domain.history.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.file.model.entity.File;

@Entity
@Getter
@Table(name = "histories")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "history", fetch = FetchType.LAZY)
    @Builder.Default
    private List<AttendenceHistory> attendenceHistories = new ArrayList();

    @JoinColumn(name = "history_files")
    @OneToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private List<File> photos = new ArrayList();

    @Column(nullable = false)
    private LocalDateTime date;
}
