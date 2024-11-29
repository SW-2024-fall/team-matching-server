package swe.second.team_matching_server.domain.like.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import swe.second.team_matching_server.domain.like.repository.UserMeetingLikeRepository;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.like.model.entity.UserMeetingLike;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingElement;
import swe.second.team_matching_server.domain.meeting.model.mapper.MeetingMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserMeetingLikeService {
    private final UserMeetingLikeRepository userMeetingLikeRepository;
    private final MeetingMapper meetingMapper;

    public int countByMeetingId(Long meetingId) {
        return userMeetingLikeRepository.countByMeetingId(meetingId);
    }

    public boolean isLiked(String userId, Long meetingId) {
        return userMeetingLikeRepository.existsByUserIdAndMeetingId(userId, meetingId);
    }

    public void save(Meeting meeting, User user) {
        userMeetingLikeRepository.save(UserMeetingLike.builder()
            .meeting(meeting)
            .user(user)
            .build());
    }

    public void delete(Meeting meeting, User user) {
        Optional<UserMeetingLike> userMeetingLike = userMeetingLikeRepository.findByMeetingAndUser(meeting, user);
        userMeetingLike.ifPresent(userMeetingLikeRepository::delete);
    }

    public List<MeetingElement> findAllByUserId(String userId) {
        List<Meeting> meetings = userMeetingLikeRepository.findMeetingsByUserId(userId);
        return meetings.stream()
            .map(meetingMapper::toMeetingElement)
            .collect(Collectors.toList());
    }
}
