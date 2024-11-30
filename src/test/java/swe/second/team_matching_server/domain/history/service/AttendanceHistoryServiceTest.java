package swe.second.team_matching_server.domain.history.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import swe.second.team_matching_server.common.enums.FileFolder;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.history.model.dto.MemberAttendanceState;
import swe.second.team_matching_server.domain.history.model.entity.AttendanceHistory;
import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.history.model.enums.AttendanceState;
import swe.second.team_matching_server.domain.history.repository.AttendanceHistoryRepository;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingType;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.enums.Major;
import swe.second.team_matching_server.domain.user.service.UserService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest // Spring Boot 테스트 환경을 설정
public class AttendanceHistoryServiceTest {

    @InjectMocks
    private AttendanceHistoryService attendanceHistoryService;
    // 테스트할 AttendanceHistoryService 클래스에 Mock 객체를 주입

    @Mock
    private AttendanceHistoryRepository attendanceHistoryRepository;
    // AttendanceHistoryRepository를 Mock 객체로 설정

    @Mock
    private UserService userService;
    // UserService를 Mock 객체로 설정

    private User mockUser;
    // 테스트용 User 객체
    private History mockHistory;
    // 테스트용 History 객체
    private List<MemberAttendanceState> mockAttendanceStates;
    // 테스트용 출석 상태 리스트
    private List<User> mockUsers;
    // 테스트용 사용자 리스트

    public static User createMockUser() {
        File mockProfileImage = File.builder()
                .id("file-user-1")
                .originalName("profile.jpg")
                .folder(FileFolder.USER)
                .mimeType("image/jpeg")
                .size(102400L) // 100KB
                .url("/files/profile/user1.jpg")
                .meta("Profile image for user1")
                .build();

        User user = User.builder()
                .id("userId")
                .username("mockUsername")
                .email("user@example.com")
                .password("encodedPassword")
                .major(Major.COMPUTER_SCIENCE)
                .studentId("20231234")
                .phoneNumber("010-1234-5678")
                .profileImage(mockProfileImage)
                .attendanceScore((byte) 50)
                .build();

        System.out.println("Mock User created: " + user);

        return user;
    }

    public static Meeting createMockMeeting(){
        return Meeting.builder()
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
    }

    public static History createMockHistory(User user) {
        Meeting mockMeeting = createMockMeeting();

        return History.builder()
                .id(1L)
                .user(user)
                .meeting(mockMeeting)
                .title("Mock History Title")
                .content("Mock content for the history.")
                .isPublic(true)
                .date(LocalDate.now())
                .location("Mock Location")
                .build();
    }

    public static AttendanceHistory createMockAttendanceHistory(User user, History history) {
        Meeting mockMeeting = createMockMeeting();
        return AttendanceHistory.builder()
                .user(user)
                .history(history)
                .meeting(mockMeeting)
                .awarded_score((byte) 1) // 초기 부여된 점수
                .state(AttendanceState.ATTENDED) // 기본 상태는 ATTENDED
                .build();
    }

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // 테스트용 User 객체 설정
//        mockUser = createMockUser();
//
//        // 테스트용 History 객체 설정
//        mockHistory = createMockHistory(mockUser);
//
//        // 테스트용 사용자 리스트 설정
//        mockUsers = new ArrayList<>();
//        mockUsers.add(mockUser);
//
//        // 테스트용 출석 상태 리스트 설정
//        mockAttendanceStates = new ArrayList<>();
//        mockAttendanceStates.add(new MemberAttendanceState("userId", AttendanceState.ATTENDED));
//        // User ID 1, 출석 상태는 ATTENDED로 설정
//    }

    @Test
    void testUpdateAllByHistoryIdUpdatesAttendanceScore() {
        // updateAllByHistoryId 메서드가 호출되었을 때 출석 점수가 올바르게 변경되는지 확인
        // 테스트용 User 객체 설정
        mockUser = createMockUser();

        System.out.println("Mock User created: " + mockUser);
        System.out.println("Mock User attendance score before update: " + mockUser.getAttendanceScore());


        // 테스트용 History 객체 설정
        mockHistory = createMockHistory(mockUser);

        System.out.println("Mock History created: " + mockHistory);

        // 테스트용 사용자 리스트 설정
        mockUsers = new ArrayList<>();
        mockUsers.add(mockUser);

        // 테스트용 출석 상태 리스트 설정
        mockAttendanceStates = new ArrayList<>();
        mockAttendanceStates.add(new MemberAttendanceState("userId", AttendanceState.ATTENDED));
        // User ID 1, 출석 상태는 ATTENDED로 설정

//        User mockUser = createMockUser(); // 초기 출석 점수 설정 50
//
//        History mockHistory = createMockHistory(mockUser);
        AttendanceHistory existingHistory = createMockAttendanceHistory(mockUser, mockHistory);

        System.out.println("AttendanceHistory created: " + existingHistory);

        List<AttendanceHistory> existingHistories = List.of(existingHistory);
        // 기존 출석 기록 리스트 생성

        System.out.println("Existing History: " + existingHistory);

        when(attendanceHistoryRepository.findAllByHistoryId(1L)).thenReturn(existingHistories);
        // 특정 historyId에 대해 기존 출석 기록 리스트 반환
        when(userService.findById("userId")).thenReturn(mockUser);

        System.out.println("userService.findById called, returning: " + mockUser);
        // userService.findById 호출 시 mockUser 반환
        when(attendanceHistoryRepository.save(existingHistory)).thenReturn(existingHistory);
        // attendanceHistoryRepository.save 호출 시 existingHistory 반환

        // Act: 상태 변경 및 메서드 실행
        mockAttendanceStates.get(0).setAttendanceState(AttendanceState.LATE);

        // 디버깅을 위한 출력
        System.out.println("Attendance state updated to: " + mockAttendanceStates.get(0).getAttendanceState());

        // 출석 상태를 LATE로 변경
        attendanceHistoryService.updateAllByHistoryId(mockHistory, mockAttendanceStates);

        // 디버깅을 위한 출력
        System.out.println("User after update: " + mockUser);
        System.out.println("User attendance score after update: " + mockUser.getAttendanceScore());


        // Assert: 결과 확인
        verify(userService, times(1)).save(mockUser);
        // userService.save가 한 번 호출되었는지 확인
        assertEquals(46, mockUser.getAttendanceScore());
        // mockUser의 출석 점수가 46으로 올바르게 업데이트되었는지 확인
        // (기존 점수 50 - 1 (기존 awarded_score) - 5 (LATE penalty))
    }

    @Test
    void testSaveAllUpdatesAttendanceScore() {
        // saveAll 메서드가 호출되었을 때 사용자의 출석 점수가 올바르게 업데이트되는지 확인

        // given
        mockUser = createMockUser();
        mockHistory = createMockHistory(mockUser);

        mockUsers = new ArrayList<>();
        mockUsers.add(mockUser);

        mockAttendanceStates = new ArrayList<>();
        mockAttendanceStates.add(new MemberAttendanceState("userId", AttendanceState.ATTENDED));


        when(userService.findById("userId")).thenReturn(mockUser);
        when(attendanceHistoryRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        // when
        attendanceHistoryService.saveAll(mockHistory, mockUsers, mockAttendanceStates);

        // then
        verify(userService, times(1)).save(mockUser);
        assertEquals(51, mockUser.getAttendanceScore()); // mockUser의 출석 점수가 51로 업데이트되었는지 확인 (기존 점수 50 + 1)
    }


}

