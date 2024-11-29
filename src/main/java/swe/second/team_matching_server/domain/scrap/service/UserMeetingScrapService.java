package swe.second.team_matching_server.domain.scrap.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import swe.second.team_matching_server.domain.scrap.repository.UserMeetingScrapRepository;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.scrap.model.entity.UserMeetingScrap;
import swe.second.team_matching_server.domain.meeting.service.MeetingService;
import swe.second.team_matching_server.domain.user.service.UserService;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingElement;
import swe.second.team_matching_server.domain.meeting.model.mapper.MeetingMapper;

@Service
@Transactional(readOnly = true)
public class UserMeetingScrapService {
    private final UserMeetingScrapRepository userMeetingScrapRepository;
    private final MeetingService meetingService;
    private final UserService userService;
    private final MeetingMapper meetingMapper;

    public UserMeetingScrapService(UserMeetingScrapRepository userMeetingScrapRepository, MeetingService meetingService, UserService userService, MeetingMapper meetingMapper) {
        this.userMeetingScrapRepository = userMeetingScrapRepository;
        this.meetingService = meetingService;
        this.userService = userService;
        this.meetingMapper = meetingMapper;
    }

    public int countByMeetingId(Long meetingId) {
        return userMeetingScrapRepository.countByMeetingId(meetingId);
    }

    public boolean isScraped(String userId, Long meetingId) {
        return userMeetingScrapRepository.existsByUserIdAndMeetingId(userId, meetingId);
    }

    @Transactional
    public void save(Meeting meeting, User user) {
        userMeetingScrapRepository.save(UserMeetingScrap.builder()
            .meeting(meeting)
            .user(user)
            .build());
    }

    @Transactional
    public void save(Long meetingId, String userId) {
        Meeting meeting = meetingService.findById(meetingId);
        User user = userService.findById(userId);
        save(meeting, user);
    }

    @Transactional
    public void delete(Meeting meeting, User user) {
        Optional<UserMeetingScrap> userMeetingScrap = userMeetingScrapRepository.findByMeetingAndUser(meeting, user);
        userMeetingScrap.ifPresent(userMeetingScrapRepository::delete);
    }

    @Transactional
    public void delete(Long meetingId, String userId) {
        Meeting meeting = meetingService.findById(meetingId);
        User user = userService.findById(userId);
        delete(meeting, user);
    }

    public List<MeetingElement> findAllByUserId(String userId) {
        List<Meeting> meetings = userMeetingScrapRepository.findMeetingsByUserId(userId);

        return meetings.stream()
            .map(meetingMapper::toMeetingElement)
            .collect(Collectors.toList());
    }
}
