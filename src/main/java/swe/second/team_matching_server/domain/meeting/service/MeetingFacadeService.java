package swe.second.team_matching_server.domain.meeting.service;

import org.springframework.stereotype.Service;
import swe.second.team_matching_server.domain.meeting.service.MeetingCommentService;
import swe.second.team_matching_server.domain.meeting.service.MeetingHistoryService;
import swe.second.team_matching_server.domain.meeting.service.MeetingLikeService;
import swe.second.team_matching_server.domain.meeting.service.MeetingMemberService;
import swe.second.team_matching_server.domain.meeting.service.MeetingScrapService;

@Service
public class MeetingFacadeService {
  private final MeetingService meetingService;
  private final MeetingCommentService meetingCommentService;
  private final MeetingScrapService meetingScrapService;
  private final MeetingHistoryService meetingHistoryService;
  private final MeetingLikeService meetingLikeService;
  private final MeetingMemberService meetingMemberService;

  public MeetingFacadeService(MeetingService meetingService, MeetingCommentService meetingCommentService, MeetingScrapService meetingScrapService, MeetingHistoryService meetingHistoryService, MeetingLikeService meetingLikeService, MeetingMemberService meetingMemberService) {
    this.meetingService = meetingService;
    this.meetingCommentService = meetingCommentService;
    this.meetingScrapService = meetingScrapService;
    this.meetingHistoryService = meetingHistoryService;
    this.meetingLikeService = meetingLikeService;
    this.meetingMemberService = meetingMemberService;
  }
}
