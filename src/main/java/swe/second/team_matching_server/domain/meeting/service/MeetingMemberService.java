package swe.second.team_matching_server.domain.meeting.service;

import swe.second.team_matching_server.domain.meeting.repository.MeetingMemberRepository;
import swe.second.team_matching_server.domain.meeting.model.entity.MeetingMember;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.user.service.UserService;
import swe.second.team_matching_server.domain.meeting.model.exception.MeetingNoPermissionException;
import swe.second.team_matching_server.domain.meeting.model.exception.MeetingMemberNotFoundException;
import swe.second.team_matching_server.domain.meeting.model.exception.MeetingRequestNotFoundException;
import swe.second.team_matching_server.domain.meeting.model.exception.MeetingLeaderLeaveException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class MeetingMemberService {
    private final MeetingMemberRepository meetingMemberRepository;
    private final UserService userService;

    public MeetingMemberService(MeetingMemberRepository meetingMemberRepository, UserService userService) {
        this.meetingMemberRepository = meetingMemberRepository;
        this.userService = userService;
    }

    public MeetingMember findById(Long id) {
        return meetingMemberRepository.findById(id)
            .orElseThrow(() -> new MeetingMemberNotFoundException());
    }

    public MeetingMember findByMeetingIdAndUserId(Long meetingId, String userId) {
        return meetingMemberRepository.findByMeetingIdAndUserId(meetingId, userId)
            .orElseThrow(() -> new MeetingMemberNotFoundException());
    }

    public MeetingMemberRole findRoleByMeetingIdAndUserId(Long meetingId, String userId) {
        return meetingMemberRepository.findRoleByMeetingIdAndUserId(meetingId, userId)
            .orElse(MeetingMemberRole.EXTERNAL);
    }

    public List<User> findUsersByMeetingId(Long meetingId) {
        return meetingMemberRepository.findUsersByMeetingId(meetingId);
    }

    public List<Meeting> findMeetingsByUserId(String userId) {
        return meetingMemberRepository.findMeetingsByUserId(userId);
    }

    public List<MeetingMember> findAllByUserId(String userId) {
        return meetingMemberRepository.findAllByUserId(userId);
    }

    public List<MeetingMember> findAllByMeetingId(Long meetingId) {
        return meetingMemberRepository.findAllByMeetingId(meetingId).stream()
            .filter(member -> member.getRole() != MeetingMemberRole.EXTERNAL)
            .collect(Collectors.toList());
    }

    public boolean isRequested(Long meetingId, String userId) {
        return meetingMemberRepository.findRoleByMeetingIdAndUserId(meetingId, userId)
            .orElse(MeetingMemberRole.EXTERNAL) == MeetingMemberRole.REQUESTED;
    }

    public boolean isMember(Long meetingId, String userId) {
        return meetingMemberRepository.findRoleByMeetingIdAndUserId(meetingId, userId) != null;
    }

    public boolean isLeader(Long meetingId, String userId) {
        return meetingMemberRepository.findRoleByMeetingIdAndUserId(meetingId, userId)
            .orElse(MeetingMemberRole.EXTERNAL) == MeetingMemberRole.LEADER;
    }

    public boolean isExecutive(Long meetingId, String userId) {
        MeetingMemberRole role = meetingMemberRepository.findRoleByMeetingIdAndUserId(meetingId, userId)
            .orElse(MeetingMemberRole.EXTERNAL);
        return role == MeetingMemberRole.LEADER || role == MeetingMemberRole.CO_LEADER;
    }

    public int countMembersByMeetingId(Long meetingId) {
        return meetingMemberRepository.countMembersByMeetingId(meetingId);
    }

    @Transactional
    public MeetingMember create(Meeting meeting, User user, MeetingMemberRole role) {
        MeetingMember meetingMember;

        try {
            meetingMember = findByMeetingIdAndUserId(meeting.getId(), user.getId());
            if (meetingMember.getRole() != MeetingMemberRole.LEADER) {
                meetingMember.updateRole(role);
            }
        } catch (MeetingMemberNotFoundException e) {
            meetingMember = MeetingMember.builder()
                .meeting(meeting)
                .user(user)
                .role(role)
                .build();
            meetingMemberRepository.save(meetingMember);
        }
        return meetingMember;
    }

    public MeetingMember create(Meeting meeting, String userId, MeetingMemberRole role) {
        User user = userService.findById(userId);
        
        return create(meeting, user, role);
    }

    public MeetingMember createLeader(Meeting meeting, String userId) {
        return create(meeting, userId, MeetingMemberRole.LEADER);
    }

    @Transactional
    public void application(Meeting meeting, String userId) {
        User user = userService.findById(userId);

        create(meeting, user, MeetingMemberRole.REQUESTED);
    }

    @Transactional
    public void cancelApplication(Meeting meeting, String userId) {
        User user = userService.findById(userId);
        MeetingMember meetingMember = findByMeetingIdAndUserId(meeting.getId(), user.getId());
        if (meetingMember.getRole() != MeetingMemberRole.REQUESTED) {
            throw new MeetingRequestNotFoundException();
        }

        meetingMemberRepository.delete(meetingMember);
    }

    @Transactional
    public void leave(Meeting meeting, String userId) {
        User user = userService.findById(userId);
        MeetingMember meetingMember = findByMeetingIdAndUserId(meeting.getId(), user.getId());

        if (meetingMember.getRole() == MeetingMemberRole.LEADER) {
            throw new MeetingLeaderLeaveException();
        }

        meetingMemberRepository.delete(meetingMember);
    }

    @Transactional
    public List<MeetingMember> updateRole(String executorId, Long meetingId, String userId, MeetingMemberRole role) {
        if (!isExecutive(meetingId, executorId)) {
            throw new MeetingNoPermissionException();
        }
        MeetingMember meetingMember = findByMeetingIdAndUserId(meetingId, userId);
        meetingMember.updateRole(role);
        
        return findAllByMeetingId(meetingId);
    }

    public List<MeetingMember> upgradeCoLeader(String executorId, Long meetingId, String memberId) {
        if (!isMember(meetingId, executorId)) {
            throw new MeetingMemberNotFoundException();
        }
        return updateRole(executorId, meetingId, memberId, MeetingMemberRole.CO_LEADER);
    }

    public List<MeetingMember> downgradeMember(String executorId, Long meetingId, String memberId) {
        if (!isMember(meetingId, executorId)) {
            throw new MeetingMemberNotFoundException();
        }
        return updateRole(executorId, meetingId, memberId, MeetingMemberRole.MEMBER);
    }

    public List<MeetingMember> applicationAccept(String executorId, Long meetingId, String memberId) {
        if (!isRequested(meetingId, memberId)) {
            throw new MeetingRequestNotFoundException();
        }
        return updateRole(executorId, meetingId, memberId, MeetingMemberRole.MEMBER);
    }

    public List<MeetingMember> applicationReject(String executorId, Long meetingId, String memberId) {
        if (!isRequested(meetingId, memberId)) {
            throw new MeetingRequestNotFoundException();
        }
        return updateRole(executorId, meetingId, memberId, MeetingMemberRole.EXTERNAL);
    }
}
