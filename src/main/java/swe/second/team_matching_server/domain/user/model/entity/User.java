package swe.second.team_matching_server.domain.user.model.entity;

import lombok.Getter;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.ArrayList;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.user.model.enums.Major;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Major major;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    @Builder.Default
    private byte attendenceScore = 80;

    @JoinColumn(name = "profile_image_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private File profileImage;

    @Column(nullable = false)
    @Builder.Default
    private List<String> features = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserMeeting> meetings = new ArrayList<>();

    public void updateAttendenceScore(byte attendenceScore) {
        this.attendenceScore = attendenceScore;
    }

    public void updateProfileImage(File profileImage) {
        this.profileImage = profileImage;
    }

    public void addFeatures(List<String> features) {
        this.features.addAll(features);
    }

    public void removeFeatures(List<String> features) {
        this.features.removeAll(features);
    }
}
