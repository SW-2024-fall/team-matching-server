package swe.second.team_matching_server.domain.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import swe.second.team_matching_server.domain.auth.model.entity.Refresh;

public interface RefreshRepository extends JpaRepository<Refresh, String> {
    boolean existsByUserId(String userId);
    Optional<Refresh> findByUserId(String userId);
}
