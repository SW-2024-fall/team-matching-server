package swe.second.team_matching_server.domain.like.service;

import org.springframework.stereotype.Service;

import swe.second.team_matching_server.domain.like.repository.UserMeetingLikeRepository;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.like.model.entity.UserMeetingLike;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserMeetingLikeService {
    private final UserMeetingLikeRepository userMeetingLikeRepository;

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
        userMeetingLikeRepository.delete(UserMeetingLike.builder()
            .meeting(meeting)
            .user(user)
            .build());
    }
}
