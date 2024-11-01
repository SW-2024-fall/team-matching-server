package swe.second.team_matching_server.domain.meeting.model.entity;

import swe.second.team_matching_server.common.entity.Base;
import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingType;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberApplicationMethod;
import swe.second.team_matching_server.domain.meeting.model.exception.MeetingInvalidParticipantException;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.BatchSize;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;

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
import java.util.HashSet;


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
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<DayOfWeek> days = new HashSet<>();

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private byte minParticipant;

    @Column(nullable = false)
    private byte maxParticipant;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeetingType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MeetingMemberApplicationMethod applicationMethod = MeetingMemberApplicationMethod.LEADER_ACCEPT;

    @Column(nullable = true)
    private String meta;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<MeetingCategory> categories = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "meeting", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<File> thumbnailFiles = new ArrayList<>();

    @Column(nullable = true)
    @Builder.Default
    private List<String> features = new ArrayList();

    @Column(nullable = true)
    @Builder.Default
    private List<String> analyzedFeatures = new ArrayList();

    @Column(nullable = true)
    private String analyzedIntroduction;

    @OneToMany(mappedBy = "meeting", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<History> histories = new ArrayList<>();

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "meeting", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<MeetingMember> members = new ArrayList<>();

    @Transient
    private int currentParticipant;

    @Transient
    private int likeCount;

    @Transient
    private int commentCount;

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

    public void updateDays(Set<DayOfWeek> days) {
        this.days = days;
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

    public void updateParticipants(byte minParticipant, byte maxParticipant) {
        if (minParticipant > maxParticipant) {
            throw new MeetingInvalidParticipantException();
        }
        if (minParticipant < 2 || maxParticipant > 99) {
            throw new MeetingInvalidParticipantException();
        }
        this.minParticipant = minParticipant;
        this.maxParticipant = maxParticipant;
    }

    public void updateMeta(String meta) {
        this.meta = meta;
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

    public void setCurrentParticipant(int currentParticipant) {
        this.currentParticipant = currentParticipant;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
