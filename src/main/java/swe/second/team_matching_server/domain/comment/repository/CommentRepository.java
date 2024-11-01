package swe.second.team_matching_server.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import swe.second.team_matching_server.domain.comment.model.entity.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findAllByMeetingId(Long meetingId);

  List<Comment> findAllByUserId(String userId);

  List<Comment> findAllByHistoryId(Long historyId);
  
  List<Comment> findAllByParentCommentId(Long parentCommentId);

  @Query("SELECT c.parentComment FROM Comment c WHERE c.id = :childCommentId")
  Comment findParentCommentByChildCommentId(Long childCommentId);
}
