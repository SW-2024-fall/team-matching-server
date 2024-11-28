package swe.second.team_matching_server.domain.meeting.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import swe.second.team_matching_server.common.enums.FileFolder;
import swe.second.team_matching_server.domain.comment.service.CommentService;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.file.service.FileMeetingService;
import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.like.service.UserMeetingLikeService;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberRole;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingType;
import swe.second.team_matching_server.domain.meeting.repository.MeetingRepository;
import swe.second.team_matching_server.domain.user.model.entity.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class MeetingServiceTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private UserMeetingLikeService userMeetingLikeService;

    @Mock
    private CommentService commentService;

    @Mock
    private FileMeetingService fileMeetingService;

    @Mock
    private  MeetingMemberService meetingMemberService;

    @InjectMocks
    private MeetingService meetingService;

    @Test
    @DisplayName("T4-1 전체 모임 리스트 조회")
    public void testFindAllWithConditions_NoConditions() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
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
        when(meetingRepository.findAll(pageable)).thenReturn(new PageImpl<>(mockMeetings));
        when(userMeetingLikeService.countByMeetingId(1L)).thenReturn(10);
        when(userMeetingLikeService.countByMeetingId(2L)).thenReturn(5);
        when(commentService.countByMeetingId(1L)).thenReturn(3);
        when(commentService.countByMeetingId(2L)).thenReturn(7);

        // When
        Page<Meeting> result = meetingService.findAllWithConditions(pageable, null, null, 2, 99);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(10, result.getContent().get(0).getLikeCount());
        assertEquals(5, result.getContent().get(1).getLikeCount());
        assertEquals(3, result.getContent().get(0).getCommentCount());
        assertEquals(7, result.getContent().get(1).getCommentCount());

        verify(meetingRepository, times(1)).findAll(pageable);
        verify(userMeetingLikeService, times(2)).countByMeetingId(anyLong());
        verify(commentService, times(2)).countByMeetingId(anyLong());
    }


    @Test
    @DisplayName("T4-2 카테고리 필터 적용")
    public void testFindAllWithConditions_CategoryOnly(){
        // Given
        Pageable pageable = PageRequest.of(0, 10);
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
        List<MeetingCategory> categories = List.of(MeetingCategory.LANGUAGE);
        List<Meeting> filteredMeetings = mockMeetings.stream()
                .filter(meeting -> meeting.getCategories().stream().anyMatch(categories::contains))
                .collect(Collectors.toList());

        when(meetingRepository.findAllWithCategoriesAndMinAndMax(categories, 2, 99, pageable))
                .thenReturn(new PageImpl<>(filteredMeetings));
        when(userMeetingLikeService.countByMeetingId(1L)).thenReturn(10);
        when(userMeetingLikeService.countByMeetingId(2L)).thenReturn(5);
        when(commentService.countByMeetingId(1L)).thenReturn(3);
        when(commentService.countByMeetingId(2L)).thenReturn(7);

        // When
        Page<Meeting> result = meetingService.findAllWithConditions(pageable, categories, null, 2, 99);
        assertNotNull(result);

        // Then
        assertEquals(1, result.getTotalElements()); // 반환된 모임 수 검증 (1개)
        assertEquals(10, result.getContent().get(0).getLikeCount());
        assertEquals(3, result.getContent().get(0).getCommentCount());

        verify(meetingRepository, times(1)).findAllWithCategoriesAndMinAndMax(categories, 2, 99, pageable);
        verify(userMeetingLikeService, times(1)).countByMeetingId(anyLong());
        verify(commentService, times(1)).countByMeetingId(anyLong());
    }


    @Test
    @DisplayName("T4-3 모임 유형 필터 적용")
    public void testFindAllWithConditions_TypeOnly(){
        // Given
        Pageable pageable = PageRequest.of(0, 10);
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
                        .type(MeetingType.ONE_TIME)
                        .categories(List.of(MeetingCategory.TRAVEL))
                        .currentParticipants(3)
                        .build()
        );
        MeetingType type = MeetingType.REGULAR;

        List<Meeting> filteredMeetings = mockMeetings.stream()
                .filter(meeting -> meeting.getType() == type)
                .collect(Collectors.toList());

        when(meetingRepository.findAllWithTypeAndMinAndMax(type, 2, 99, pageable))
                .thenReturn(new PageImpl<>(filteredMeetings));
        when(userMeetingLikeService.countByMeetingId(1L)).thenReturn(10);
        when(commentService.countByMeetingId(1L)).thenReturn(3);


        // When
        Page<Meeting> result = meetingService.findAllWithConditions(pageable, null, type, 2, 99);
        assertNotNull(result);

        // Then
        assertEquals(1, result.getTotalElements()); // 반환된 모임 수 검증 (1개)
        verify(meetingRepository, times(1)).findAllWithTypeAndMinAndMax(type, 2, 99, pageable); // 리포지토리 호출 횟수 검증
    }

    @Test
    @DisplayName("T4-4 다중 필터 적용")
    public void testFindAllWithConditions_CategoryAndTypeAndHeadCount(){
        // Given
        Pageable pageable = PageRequest.of(0, 10);
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
                        .type(MeetingType.ONE_TIME)
                        .categories(List.of(MeetingCategory.TRAVEL))
                        .currentParticipants(3)
                        .build()
        );

        int min = 11;
        int max = 20;

        MeetingType type = MeetingType.REGULAR;

        List<MeetingCategory> categories = List.of(MeetingCategory.LANGUAGE);

        List<Meeting> filteredMeetings = mockMeetings.stream()
                .filter(meeting -> meeting.getType() == type && meeting.getMinParticipant() >= min && meeting.getMaxParticipant() <= max && meeting.getCategories().stream().anyMatch(categories::contains))
                .toList();

        when(meetingRepository.findAllWithConditions(categories, type, min, max, pageable))
                .thenReturn(new PageImpl<>(filteredMeetings));

        // When
        Page<Meeting> result = meetingService.findAllWithConditions(pageable, categories, type, min, max);
        assertNotNull(result);

        // Then
        assertEquals(0, result.getTotalElements()); // 반환된 모임 수 검증 (0개)
        verify(meetingRepository, times(1)).findAllWithConditions(categories, type, min, max, pageable); // 리포지토리 호출 횟수 검증

    }

    @Test
    @DisplayName("T8 미팅 생성")
    void testCreateMeeting() {
        // given
        Meeting mockMeeting = Meeting.builder()
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

        List<FileCreateDto> fileCreateDtos = List.of(
                FileCreateDto.builder()
                .file(Mockito.mock(MultipartFile.class))  // Mock MultipartFile
                .meta("meta data")                        // meta 필드
                .folder(Mockito.mock(FileFolder.class))   // Mock FileFolder
                .meeting(Mockito.mock(Meeting.class))     // Mock Meeting
                .user(Mockito.mock(User.class))           // Mock User
                .history(Mockito.mock(History.class))     // Mock History
                .build());

        List<File> mockFiles = List.of(new File());

        when(meetingRepository.save(mockMeeting)).thenReturn(mockMeeting);
        when(fileMeetingService.saveAllByMeeting(mockMeeting, fileCreateDtos)).thenReturn(mockFiles);
        when(meetingRepository.findByIdWithThumbnailFiles(1L)).thenReturn(Optional.of(mockMeeting));

        // when
        Meeting createdMeeting = meetingService.create(fileCreateDtos, mockMeeting, "leaderId");

        // then
        assertNotNull(createdMeeting);
        assertEquals(mockMeeting.getId(), createdMeeting.getId());
        verify(meetingRepository).save(mockMeeting);
        verify(fileMeetingService).saveAllByMeeting(mockMeeting, fileCreateDtos);
        verify(meetingMemberService).createLeader(mockMeeting, "leaderId");
    }

    @Test
    @DisplayName("T9 미팅 삭제")
    void testDeleteMeeting() {
        // given
        Meeting mockMeeting = Meeting.builder()
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

        when(meetingRepository.findById(1L)).thenReturn(Optional.of(mockMeeting));
        when(meetingMemberService.findRoleByMeetingIdAndUserId(1L, "leaderId"))
                .thenReturn(MeetingMemberRole.LEADER);

        // when
        assertDoesNotThrow(() -> meetingService.delete(1L, "leaderId"));

        // then
        verify(meetingRepository).findById(1L);
        verify(meetingRepository).delete(mockMeeting);
    }

}
