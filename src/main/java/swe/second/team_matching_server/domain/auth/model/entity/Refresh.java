package swe.second.team_matching_server.domain.auth.model.entity;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refresh {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;
}
