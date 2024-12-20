package swe.second.team_matching_server.domain.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swe.second.team_matching_server.domain.comment.repository.CommentRepository;
import swe.second.team_matching_server.domain.comment.model.dto.CommentResponse;
import swe.second.team_matching_server.domain.comment.model.entity.Comment;
import swe.second.team_matching_server.domain.user.service.UserService;
import swe.second.team_matching_server.common.exception.UnauthorizedException;
import swe.second.team_matching_server.domain.comment.model.dto.CommentCreateDto;
import swe.second.team_matching_server.domain.comment.model.exception.CommentNotFoundException;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingElement;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.meeting.model.mapper.MeetingMapper;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Transactional(readOnly = true)
@Service
public class CommentService {
  private final CommentRepository commentRepository;
  private final UserService userService;
  private final MeetingMapper meetingMapper;

  public CommentService(CommentRepository commentRepository, UserService userService, MeetingMapper meetingMapper) {
    this.commentRepository = commentRepository;
    this.userService = userService;
    this.meetingMapper = meetingMapper;
  }

  public int countByMeetingId(Long meetingId) {
    return commentRepository.countByMeetingId(meetingId);
  }

  public Comment findById(Long commentId) {
    return commentRepository.findById(commentId)
      .orElseThrow(() -> new CommentNotFoundException());
  }

  public List<CommentResponse> findAllByMeetingId(Long meetingId) {
    return commentRepository.findAllByMeetingId(meetingId)
      .stream()
      .map(CommentResponse::from)
      .collect(Collectors.toList());
  }

  @Transactional
  public CommentResponse createComment(CommentCreateDto commentCreateDto, Meeting meeting) {
    Comment comment = commentRepository.save(Comment.builder()
        .meeting(meeting)
        .user(userService.findById(commentCreateDto.getUserId()))
        .content(commentCreateDto.getContent())
        .parentComment(commentCreateDto.getParentCommentId() != null ? findById(commentCreateDto.getParentCommentId()) : null)
      .build());

    return CommentResponse.from(comment);
  }

  @Transactional
  public CommentResponse updateComment(Long commentId, CommentCreateDto commentCreateDto, Meeting meeting) {
    Comment comment = findById(commentId);
    comment.updateContent(commentCreateDto.getContent());
    commentRepository.save(comment);

    return CommentResponse.from(comment);
  }

  @Transactional
  public void deleteComment(Long commentId, String userId) {
    Comment comment = findById(commentId);
    if (!comment.getUser().getId().equals(userId)) {
      throw new UnauthorizedException();
    }
    comment.setDeletedAt(LocalDateTime.now());
    commentRepository.save(comment);
  }

  public List<MeetingElement> findAllByUserId(String userId) {
    List<Meeting> meetings = commentRepository.findMeetingsByUserId(userId);
    return meetings.stream()
      .map(meetingMapper::toMeetingElement)
      .collect(Collectors.toList());
  }

}
