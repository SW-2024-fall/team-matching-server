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
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberApplicationMethod;
import swe.second.team_matching_server.domain.meeting.model.exception.MeetingFullException;
import swe.second.team_matching_server.domain.meeting.repository.MeetingRepository;
import swe.second.team_matching_server.domain.meeting.model.exception.MeetingInvalidParticipantException;

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
    private final MeetingRepository meetingRepository;

    public MeetingMemberService(MeetingMemberRepository meetingMemberRepository, UserService userService, MeetingRepository meetingRepository) {
        this.meetingMemberRepository = meetingMemberRepository;
        this.userService = userService;
        this.meetingRepository = meetingRepository;
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

    public List<User> findMemberUsersByMeetingId(Long meetingId) {
        return meetingMemberRepository.findMemberUsersByMeetingId(meetingId);
    }

    public List<Meeting> findMeetingsByUserId(String userId) {
        return meetingMemberRepository.findMeetingsByUserId(userId);
    }

    public List<MeetingMember> findAllMembersByMeetingId(Long meetingId) {
        return meetingMemberRepository.findAllByMeetingId(meetingId).stream()
            .filter(member -> member.getRole() != MeetingMemberRole.EXTERNAL
                && member.getRole() != MeetingMemberRole.REQUESTED)
            .collect(Collectors.toList());
    }

    public List<MeetingMember> findAllByMeetingId(Long meetingId) {
        return meetingMemberRepository.findAllByMeetingId(meetingId).stream()
            .filter(member -> member.getRole() != MeetingMemberRole.EXTERNAL)
            .collect(Collectors.toList());
    }

    public boolean isRequested(Long meetingId, String userId) {
        MeetingMemberRole role = meetingMemberRepository.findRoleByMeetingIdAndUserId(meetingId, userId)
            .orElse(MeetingMemberRole.EXTERNAL);
        return role == MeetingMemberRole.REQUESTED;
    }

    public boolean isMember(Long meetingId, String userId) {
        MeetingMemberRole role = meetingMemberRepository.findRoleByMeetingIdAndUserId(meetingId, userId)
            .orElse(MeetingMemberRole.EXTERNAL);
        return role != MeetingMemberRole.EXTERNAL && role != MeetingMemberRole.REQUESTED;
    }

    public boolean isMember(MeetingMember meetingMember) {
        return meetingMember.getRole() != MeetingMemberRole.EXTERNAL && meetingMember.getRole() != MeetingMemberRole.REQUESTED;
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

    public boolean isExecutive(MeetingMember meetingMember) {
        return meetingMember.getRole() == MeetingMemberRole.LEADER || meetingMember.getRole() == MeetingMemberRole.CO_LEADER;
    }

    public int countMembersByMeetingId(Long meetingId) {
        return meetingMemberRepository.countMembersByMeetingId(meetingId);
    }

    @Transactional
    public MeetingMember create(Meeting meeting, User user, MeetingMemberRole role) {
        meeting.updateCurrentParticipants(meeting.getCurrentParticipants() + 1);
        meetingRepository.save(meeting);
        MeetingMember meetingMember;

        try {
            meetingMember = findByMeetingIdAndUserId(meeting.getId(), user.getId());
            if (meetingMember.getRole() != MeetingMemberRole.LEADER) {
                meetingMember.updateRole(role);
                meetingMemberRepository.save(meetingMember);
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
    public MeetingMember application(Meeting meeting, String userId) {
        if (isMember(meeting.getId(), userId)) {
            return findByMeetingIdAndUserId(meeting.getId(), userId);
        }
        User user = userService.findById(userId);

        if (countMembersByMeetingId(meeting.getId()) >= meeting.getMaxParticipant()) {
            throw new MeetingFullException();
        }
        if (meeting.getApplicationMethod() == MeetingMemberApplicationMethod.FIRST_COME_FIRST_SERVED) {
            return create(meeting, user, MeetingMemberRole.MEMBER);
        } else {
            return create(meeting, user, MeetingMemberRole.REQUESTED);
        }
    }

    @Transactional
    public List<MeetingMember> leave(Meeting meeting, String userId, String targetUserId) {
        MeetingMember requestedMember = findByMeetingIdAndUserId(meeting.getId(), userId);
        MeetingMember targetMember = findByMeetingIdAndUserId(meeting.getId(), targetUserId);

        if (targetMember.getRole() == MeetingMemberRole.LEADER) {
            throw new MeetingLeaderLeaveException();
        }
        if (!requestedMember.equals(targetMember) && !isExecutive(requestedMember)) {
            throw new MeetingNoPermissionException();
        }
        if (countMembersByMeetingId(meeting.getId()) <= meeting.getMinParticipant()) {
            throw new MeetingInvalidParticipantException();
        }
        meeting.updateCurrentParticipants(meeting.getCurrentParticipants() - 1);
        meetingRepository.save(meeting);
        meetingMemberRepository.delete(targetMember);

        return findAllByMeetingId(meeting.getId());
    }

    @Transactional
    public List<MeetingMember> updateRole(String executorId, Meeting meeting, String userId, MeetingMemberRole role) {
        if (!isExecutive(meeting.getId(), executorId)) {
            throw new MeetingNoPermissionException();
        }
        if (role == MeetingMemberRole.EXTERNAL) {
            return leave(meeting, executorId, userId);
        }

        MeetingMember meetingMember = meetingMemberRepository.findByMeetingIdAndUserId(meeting.getId(), userId)
            .orElseThrow(() -> new MeetingMemberNotFoundException());

        if (role == MeetingMemberRole.EXTERNAL) {
            meetingMemberRepository.delete(meetingMember);
        } else {
            meetingMember.updateRole(role);
        }

        return findAllByMeetingId(meeting.getId());
    }

    @Transactional
    public List<MeetingMember> upgradeToCoLeader(String executorId, Meeting meeting, String memberId) {
        if (!isMember(meeting.getId(), executorId)) {
            throw new MeetingMemberNotFoundException();
        }
        return updateRole(executorId, meeting, memberId, MeetingMemberRole.CO_LEADER);
    }

    @Transactional
    public List<MeetingMember> downgradeToMember(String executorId, Meeting meeting, String memberId) {
        if (!isMember(meeting.getId(), executorId)) {
            throw new MeetingMemberNotFoundException();
        }
        return updateRole(executorId, meeting, memberId, MeetingMemberRole.MEMBER);
    }

    @Transactional
    public List<MeetingMember> acceptApplication(String executorId, Meeting meeting, String applicantId) {
        if (!isRequested(meeting.getId(), applicantId)) {
            throw new MeetingRequestNotFoundException();
        }
        if (countMembersByMeetingId(meeting.getId()) >= meeting.getMaxParticipant()) {
            throw new MeetingFullException();
        }
        meeting.updateCurrentParticipants(meeting.getCurrentParticipants() + 1);
        meetingRepository.save(meeting);
        return updateRole(executorId, meeting, applicantId, MeetingMemberRole.MEMBER);
    }

    @Transactional
    public List<MeetingMember> rejectApplication(String executorId, Meeting meeting, String applicantId) {
        if (!isRequested(meeting.getId(), applicantId)) {
            throw new MeetingRequestNotFoundException();
        }
        
        if (!isExecutive(meeting.getId(), executorId)) {
            throw new MeetingMemberNotFoundException();
        }

        MeetingMember meetingMember = findByMeetingIdAndUserId(meeting.getId(), applicantId);
        meetingMemberRepository.delete(meetingMember);
        return findAllByMeetingId(meeting.getId());
    }

    @Transactional
    public void cancelApplication(Meeting meeting, String applicantId) {
        if (!isRequested(meeting.getId(), applicantId)) {
            throw new MeetingRequestNotFoundException();
        }
        meetingMemberRepository.delete(findByMeetingIdAndUserId(meeting.getId(), applicantId));
    }
}
