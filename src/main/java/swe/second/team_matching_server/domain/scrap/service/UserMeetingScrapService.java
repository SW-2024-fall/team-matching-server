package swe.second.team_matching_server.domain.scrap.service;

import org.springframework.stereotype.Service;

import swe.second.team_matching_server.domain.scrap.repository.UserMeetingScrapRepository;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.scrap.model.entity.UserMeetingScrap;

@Service
public class UserMeetingScrapService {
    private final UserMeetingScrapRepository userMeetingScrapRepository;

    public UserMeetingScrapService(UserMeetingScrapRepository userMeetingScrapRepository) {
        this.userMeetingScrapRepository = userMeetingScrapRepository;
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

    public void delete(Meeting meeting, User user) {
        userMeetingScrapRepository.delete(UserMeetingScrap.builder()
            .meeting(meeting)
            .user(user)
            .build());
    }
}
