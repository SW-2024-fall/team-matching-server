package swe.second.team_matching_server.domain.file.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import swe.second.team_matching_server.domain.file.model.entity.File;

@Repository
public interface FileRepository extends JpaRepository<File, String> {
  List<File> findAllByMeetingId(Long meetingId);

  List<File> findAllByHistoryId(Long historyId);

  Optional<File> findByUserId(String userId);
}
