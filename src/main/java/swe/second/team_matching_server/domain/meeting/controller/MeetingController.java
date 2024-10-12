package swe.second.team_matching_server.domain.meeting.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import swe.second.team_matching_server.domain.meeting.service.MeetingFacadeService;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {
  private final MeetingFacadeService meetingFacadeService;

  public MeetingController(MeetingFacadeService meetingFacadeService) {
    this.meetingFacadeService = meetingFacadeService;
  }
}
