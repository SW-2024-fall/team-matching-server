package swe.second.team_matching_server.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swe.second.team_matching_server.domain.user.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
}
