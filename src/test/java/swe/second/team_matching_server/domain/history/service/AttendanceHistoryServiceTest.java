package swe.second.team_matching_server.domain.history.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import swe.second.team_matching_server.common.enums.FileFolder;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.file.service.FileService;
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
import static org.mockito.Mockito.*;

@SpringBootTest
public class AttendanceHistoryServiceTest {

    @InjectMocks
    private AttendanceHistoryService attendanceHistoryService;

    @Mock
    private AttendanceHistoryRepository attendanceHistoryRepository;

    @Mock
    private static UserService userService;

    @Mock
    private static FileService fileService;

    private User mockUser;
    private History mockHistory;
    private List<MemberAttendanceState> mockAttendanceStates;
    private List<User> mockUsers;

    public static User createMockUser() {

        byte[] fileContent = new byte[102400]; // 100KB 크기의 더미 파일 데이터
        MultipartFile mockMultipartFile = new MockMultipartFile("profile.jpg", "profile.jpg", "image/jpeg", fileContent);

        FileCreateDto fileCreateDto = FileCreateDto.builder()
                .file(mockMultipartFile) // MultipartFile
                .meta("Profile image for user1") // 메타 정보
                .folder(FileFolder.USER) // 파일 폴더 정보
                .build();

        File savedFile = fileService.save(fileCreateDto);

        return User.builder()
                .id("userId")
                .username("mockUsername")
                .email("user@example.com")
                .password("encodedPassword")
                .major(Major.COMPUTER_SCIENCE)
                .studentId("20231234")
                .phoneNumber("010-1234-5678")
                .profileImage(savedFile)
                .attendanceScore((byte) 50)
                .build();
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
                .awarded_score((byte) 1)
                .state(AttendanceState.ATTENDED) // 기본 상태는 ATTENDED
                .build();
    }

    @Test
    public void testUpdateAllByHistoryId() {

        // given
        mockUser = createMockUser();
        mockHistory = createMockHistory(mockUser);
        Meeting mockMeeting = createMockMeeting();
        AttendanceHistory mockAttendanceHistory = createMockAttendanceHistory(mockUser, mockHistory);

        List<AttendanceHistory> mockAttendanceHistories = List.of(mockAttendanceHistory);
        List<MemberAttendanceState> mockAttendanceStates = List.of(
                new MemberAttendanceState(mockUser.getId(), AttendanceState.LATE)
        );
        AttendanceHistory updatedAttendanceHistory = AttendanceHistory.builder()
                .user(mockUser)
                .history(mockHistory)
                .meeting(mockMeeting)
                .awarded_score((byte) 1)
                .state(AttendanceState.LATE)
                .build();


        when(attendanceHistoryRepository.findAllByHistoryId(1L))
                .thenReturn(mockAttendanceHistories);
        when(attendanceHistoryRepository.save(updatedAttendanceHistory)).thenReturn(updatedAttendanceHistory);

        // when
        List<AttendanceHistory> result = attendanceHistoryService.updateAllByHistoryId(mockHistory, mockAttendanceStates);

        // then
        assertEquals(1, result.size());
        AttendanceHistory updatedHistory = result.get(0);
        assertEquals(AttendanceState.LATE, updatedHistory.getState());

        verify(attendanceHistoryRepository, times(1)).save(mockAttendanceHistory);
        verify(attendanceHistoryRepository, times(1)).findAllByHistoryId(mockHistory.getId());
    }


    @Test
    public void testSaveAllUpdatesAttendanceScore() {
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

