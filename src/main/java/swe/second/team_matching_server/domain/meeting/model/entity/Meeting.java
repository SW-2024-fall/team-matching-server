package swe.second.team_matching_server.domain.meeting.model.entity;

import swe.second.team_matching_server.common.entity.Base;
import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingType;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;

import org.hibernate.annotations.Filter;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.PreUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import swe.second.team_matching_server.domain.file.model.entity.File;

@Entity
@Table(name = "meetings")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, of = "id")
@Filter(name = "deletedMeetingFilter", condition = "deleted_at is null")
public class Meeting extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String title;

    @Column(nullable = true)
    private String content;

    @Column(nullable = true)
    private Set<DayOfWeek> days;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private byte min_participants;

    @Column(nullable = false)
    private byte max_participants;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    @Builder.Default
    private boolean isRecurring = true;
    
    @Column(nullable = false)
    private MeetingType type;

    @Column(nullable = true)
    private String meta;

    @Column(nullable = false)
    @Builder.Default
    private int views = 0;

    @Column(nullable = false)
    @Builder.Default
    private List<MeetingCategory> categories = new ArrayList<>();

    @JoinColumn(nullable = false, name = "thumbnail_ids")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<File> thumbnailFiles;

    @Column(nullable = true)
    @Builder.Default
    private List<String> features = new ArrayList();

    @Column(nullable = true)
    @Builder.Default
    private List<String> analyzedFeatures = new ArrayList();

    @Column(nullable = true)
    private String analyzedIntroduction;

    @OneToMany(mappedBy = "meeting", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<History> histories;

    @OneToMany(mappedBy = "meeting", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MeetingMember> memebers;

    public void updateThumbnailFiles(List<File> thumbnailFiles) {
        this.thumbnailFiles = thumbnailFiles;
    }

    public void updateFeatures(List<String> features) {
        this.features = features;
    }

    public void updateAnalyzedFeatures(List<String> analyzedFeatures) {
        this.analyzedFeatures = analyzedFeatures;
    }

    public void updateAnalyzedIntroduction(String analyzedIntroduction) {
        this.analyzedIntroduction = analyzedIntroduction;
    }
    
    public void updateStatus(boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public void updateDate(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateTime(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void updateLocation(String location) {
        this.location = location;
    }

    public void updateParticipants(byte min_participants, byte max_participants) {
        if (min_participants > max_participants) {
            throw new IllegalArgumentException("Min participants must be less than max participants");
        }
        if (min_participants < 2 || max_participants > 99) {
            throw new IllegalArgumentException("Min participants must be more than 1 and max participants must be less than 100");
        }
        this.min_participants = min_participants;
        this.max_participants = max_participants;
    }

    public void updateMeta(String meta) {
        this.meta = meta;
    }

    public void updateViews() {
        this.views += 1;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateCategories(List<MeetingCategory> categories) {
        this.categories = categories;
    }

    @PrePersist
    @PreUpdate
    private void isValid() {
        if (this.startDate.isAfter(this.endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        if (this.min_participants > this.max_participants) {
            throw new IllegalArgumentException("Min participants must be less than max participants");
        }
        if (this.min_participants < 2 || this.max_participants > 99) {
            throw new IllegalArgumentException("Min participants must be more than 1 and max participants must be less than 100");
        }
    }
}
