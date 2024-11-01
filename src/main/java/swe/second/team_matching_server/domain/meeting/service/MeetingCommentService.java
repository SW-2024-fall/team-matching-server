package swe.second.team_matching_server.domain.meeting.service;

import org.springframework.stereotype.Service;
import swe.second.team_matching_server.domain.comment.service.CommentService;
import swe.second.team_matching_server.domain.comment.model.entity.Comment;

import java.util.List;

@Service
public class MeetingCommentService {
  private final CommentService commentService;

  public MeetingCommentService(CommentService commentService) {
    this.commentService = commentService;
  }

  public List<Comment> getCommentsByMeetingId(Long meetingId) {
    return commentService.getCommentsByMeetingId(meetingId);
  }
}
