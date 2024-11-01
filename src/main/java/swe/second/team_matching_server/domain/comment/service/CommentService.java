package swe.second.team_matching_server.domain.comment.service;

import org.springframework.stereotype.Service;

import swe.second.team_matching_server.domain.comment.repository.CommentRepository;
import swe.second.team_matching_server.domain.comment.model.entity.Comment;

import java.util.List;

@Service
public class CommentService {
  private final CommentRepository commentRepository;

  public CommentService(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  } 

  public List<Comment> getCommentsByMeetingId(Long meetingId) {
    return commentRepository.findAllByMeetingId(meetingId);
  }
}
