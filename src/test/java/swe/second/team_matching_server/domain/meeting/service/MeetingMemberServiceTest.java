package swe.second.team_matching_server.domain.meeting.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import swe.second.team_matching_server.common.enums.FileFolder;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.meeting.model.entity.MeetingMember;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingType;
import swe.second.team_matching_server.domain.meeting.model.exception.MeetingFullException;
import swe.second.team_matching_server.domain.meeting.repository.MeetingMemberRepository;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.enums.Major;
import swe.second.team_matching_server.domain.user.service.UserService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@Transactional
public class MeetingMemberServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private MeetingMemberRepository meetingMemberRepository;

    @InjectMocks
    private MeetingMemberService meetingMemberService;

    @Test
    @DisplayName("T5 유저가 참여하는 모든 모임 리스트 반환")
    void testFindAllMeetingsByUserId() {
        // given
        String userId = "testUserId";
        List<Meeting> mockMeetings = List.of(
                Meeting.builder()
                        .id(1L)
                        .name("토익스터디")
                        .title("토익스터디 모집")
                        .content("모집합니다.")
                        .days(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
                        .location("시립대")
                        .startDate(LocalDate.of(2024, 12, 1))
                        .endDate(LocalDate.of(2024, 12, 31))
                        .minParticipant((byte) 4)
                        .maxParticipant((byte) 10)
                        .startTime(LocalTime.of(18, 0))
                        .endTime(LocalTime.of(20, 0))
                        .type(MeetingType.REGULAR)
                        .categories(List.of(MeetingCategory.LANGUAGE))
                        .currentParticipants(5)
                        .build(),

                Meeting.builder()
                        .id(2L)
                        .name("정기모임")
                        .title("정기모임 설명")
                        .content("월 1회 정기모임")
                        .days(Set.of(DayOfWeek.FRIDAY))
                        .location("서울역")
                        .startDate(LocalDate.of(2024, 11, 15))
                        .endDate(LocalDate.of(2024, 11, 30))
                        .minParticipant((byte) 3)
                        .maxParticipant((byte) 8)
                        .startTime(LocalTime.of(19, 0))
                        .endTime(LocalTime.of(21, 0))
                        .type(MeetingType.REGULAR)
                        .categories(List.of(MeetingCategory.TRAVEL))
                        .currentParticipants(3)
                        .build()
        );

        when(meetingMemberRepository.findAllMeetingsByUserId(userId)).thenReturn(mockMeetings);

        // when
        List<Meeting> result = meetingMemberService.findAllMeetingsByUserId(userId);

        // then
        assertEquals(2, result.size());
        assertEquals("토익스터디", result.get(0).getName());
        assertEquals("정기모임", result.get(1).getName());
    }

    @Test
    @DisplayName("T6 모임 신청")
    public void testApplication() {
        // given: 모임 멤버 수가 최대 인원에 도달한 경우
        Meeting meeting = Meeting.builder()
                .id(1L)
                .name("토익스터디")
                .title("토익스터디 모집")
                .content("모집합니다.")
                .days(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
                .location("시립대")
                .startDate(LocalDate.of(2024, 12, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .minParticipant((byte) 4)
                .maxParticipant((byte) 10)
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(20, 0))
                .type(MeetingType.REGULAR)
                .categories(List.of(MeetingCategory.LANGUAGE))
                .currentParticipants(5)
                .build();

        User user = User.builder().id("userId").username("user123").email("user@example.com").password("encodedPassword").major(Major.COMPUTER_SCIENCE).studentId("20230001").phoneNumber("010-1234-5678").profileImage(File.builder().id("defaultFileId").originalName("default.png").folder(FileFolder.USER).mimeType("image/png").size(1024L).url("https://example.com/files/default.png").build()).build();

        when(userService.findById("userId")).thenReturn(user);
        when(meetingMemberRepository.countMembersByMeetingId(1L)).thenReturn((int) meeting.getMaxParticipant());

        // when: MeetingFullException 예외가 발생해야 함
        assertThrows(MeetingFullException.class, () -> meetingMemberService.application(meeting, "userId"));

        // then: 멤버 수 조회 메서드가 호출되었는지 확인
        verify(meetingMemberRepository).countMembersByMeetingId(1L);
    }

    @Test
    @DisplayName("T7 모임 신청 해제")
    public void testCancelApplication() {
        // Given
        Long meetingId = 1L;
        String applicantId = "user1";

        Meeting meeting = Meeting.builder()
                .id(meetingId)
                .name("토익스터디")
                .title("토익스터디 모집")
                .content("모집합니다.")
                .days(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
                .location("시립대")
                .startDate(LocalDate.of(2024, 12, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .minParticipant((byte) 4)
                .maxParticipant((byte) 10)
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(20, 0))
                .type(MeetingType.REGULAR)
                .categories(List.of(MeetingCategory.LANGUAGE))
                .currentParticipants(5)
                .build();
        User applicant = User.builder().id(applicantId).build();
        MeetingMember mockMeetingMember = MeetingMember.builder()
                .meeting(meeting)
                .user(applicant)
                .role(MeetingMemberRole.REQUESTED)
                .build();

        when(meetingMemberService.isRequested(meetingId, applicantId)).thenReturn(true);
        when(meetingMemberRepository.findByMeetingIdAndUserId(meetingId, applicantId))
                .thenReturn(Optional.of(mockMeetingMember));

        // When
        meetingMemberService.cancelApplication(meeting, applicantId);

        // Then
        verify(meetingMemberRepository, times(1)).delete(mockMeetingMember);
    }
}
