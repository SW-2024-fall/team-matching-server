package swe.second.team_matching_server.domain.like.service;

import org.springframework.stereotype.Service;

import swe.second.team_matching_server.domain.like.repository.UserMeetingLikeRepository;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.like.model.entity.UserMeetingLike;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingElement;
import swe.second.team_matching_server.domain.meeting.model.mapper.MeetingMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
        userMeetingLikeRepository.delete(UserMeetingLike.builder()
            .meeting(meeting)
            .user(user)
            .build());
    }

    public Page<MeetingElement> findAllByUserId(User user, Pageable pageable) {
        Page<Meeting> meetings = userMeetingLikeRepository.findMeetingsByUserId(user.getId(), pageable);
        return meetings.map(meetingMapper::toMeetingElement);
    }
}
