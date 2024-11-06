package swe.second.team_matching_server.domain.scrap.service;

import org.springframework.stereotype.Service;

import swe.second.team_matching_server.domain.scrap.repository.UserMeetingScrapRepository;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.scrap.model.entity.UserMeetingScrap;
import swe.second.team_matching_server.domain.meeting.service.MeetingService;
import swe.second.team_matching_server.domain.user.service.UserService;

@Service
public class UserMeetingScrapService {
    private final UserMeetingScrapRepository userMeetingScrapRepository;
    private final MeetingService meetingService;
    private final UserService userService;

    public UserMeetingScrapService(UserMeetingScrapRepository userMeetingScrapRepository, MeetingService meetingService, UserService userService) {
        this.userMeetingScrapRepository = userMeetingScrapRepository;
        this.meetingService = meetingService;
        this.userService = userService;
    }

    public int countByMeetingId(Long meetingId) {
        return userMeetingScrapRepository.countByMeetingId(meetingId);
    }

    public boolean isScraped(String userId, Long meetingId) {
        return userMeetingScrapRepository.existsByUserIdAndMeetingId(userId, meetingId);
    }

    public void save(Meeting meeting, User user) {
        userMeetingScrapRepository.save(UserMeetingScrap.builder()
            .meeting(meeting)
            .user(user)
            .build());
    }

    public void save(Long meetingId, String userId) {
        Meeting meeting = meetingService.findById(meetingId);
        User user = userService.findById(userId);
        save(meeting, user);
    }

    public void delete(Meeting meeting, User user) {
        userMeetingScrapRepository.delete(UserMeetingScrap.builder()
            .meeting(meeting)
            .user(user)
            .build());
    }

    public void delete(Long meetingId, String userId) {
        Meeting meeting = meetingService.findById(meetingId);
        User user = userService.findById(userId);
        delete(meeting, user);
    }
}
