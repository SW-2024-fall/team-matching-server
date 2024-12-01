package swe.second.team_matching_server.gemini;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import swe.second.team_matching_server.core.gemini.MeetingRecommender;
import swe.second.team_matching_server.core.gemini.dto.MeetingRecommendation;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingType;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.enums.Major;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;

@Slf4j
@SpringBootTest
public class MeetingRecommenderTest {

    @Autowired
    private MeetingRecommender recommender;

    @Test
    public void testMain() {
        // 테스트 데이터 생성
        List<Meeting> meetings = generateTestMeetings();
        List<User> users = generateTestUsers();

        // 각 사용자에 대한 추천 실행
        for (User user : users) {
            try {
                log.info("\n=== " + user.getUsername() + "님을 위한 추천 ===");
                List<MeetingRecommendation> recommendations = recommender.recommendMeetings(user, meetings);
                assertNotNull(recommendations, "추천 목록이 null이 아닙니다.");
                assertFalse(recommendations.isEmpty(), "추천 목록이 비어있지 않아야 합니다.");
                recommendations.forEach(recommendation -> log.info(recommendation.toString()));
            } catch (Exception e) {
                fail("추천 생성 중 오류 발생: " + e.getMessage());
            }
        }
    }

    private List<Meeting> generateTestMeetings() {
        List<Meeting> meetings = new ArrayList<>();

        // Meeting 1: 알고리즘 스터디
        Meeting meeting1 = Meeting.builder()
        .id(1L)
        .name("알고리즘 스터디")
        .type(MeetingType.REGULAR)
        .title("코딩테스트 준비 스터디원 모집")
        .content("매주 알고리즘 문제를 풀고 코드리뷰를 진행합니다.")
        .meta("")
        .categories(List.of(MeetingCategory.RESEARCH))
        .analyzedFeatures(List.of("알고리즘", "코딩테스트", "코딩"))
        .analyzedIntroduction("알고리즘 문제를 풀고 코드리뷰를 진행합니다.")
        .build();
        meetings.add(meeting1);

        // Meeting 2: 영어회화 클럽
        Meeting meeting2 = Meeting.builder()
        .id(2L)
        .name("영어회화 클럽")
        .type(MeetingType.REGULAR)
        .title("원어민과 함께하는 영어회화")
        .content("매주 수요일 원어민 선생님과 함께 다양한 주제로 영어 토론을 진행합니다.")
        .meta("")
        .categories(List.of(MeetingCategory.RESEARCH))
        .analyzedFeatures(List.of("영어회화", "원어민", "토론"))
        .analyzedIntroduction("원어민과 함께 영어 토론을 진행합니다.")
        .build();
        meetings.add(meeting2);

        // 배드민턴 동아리
        Meeting meeting3 = Meeting.builder()
        .id(3L)
        .name("배드민턴 동아리")
        .type(MeetingType.REGULAR)
        .title("배드민턴 동아리 모집")
        .content("매주 수요일 배드민턴을 즐기는 동아리원들과 함께 운동합니다.")
        .meta("")
        .categories(List.of(MeetingCategory.RESEARCH))
        .analyzedFeatures(List.of("배드민턴", "운동", "친목"))
        .analyzedIntroduction("배드민턴을 즐기는 동아리원들과 함께 운동합니다.")
        .build();
        meetings.add(meeting3);

        // 영화 감상 동아리
        Meeting meeting4 = Meeting.builder()
        .id(4L)
        .name("영화 감상 동아리")
        .type(MeetingType.REGULAR)
        .title("영화 감상 동아리 모집")
        .content("매주 수요일 영화를 감상하고 토론을 진행합니다.")
        .meta("")
        .categories(List.of(MeetingCategory.RESEARCH))
        .analyzedFeatures(List.of("영화", "감상", "토론"))
        .analyzedIntroduction("영화를 감상하고 토론을 진행합니다.")
        .build();
        meetings.add(meeting4);

        // 글쓰기 동아리
        Meeting meeting5 = Meeting.builder()
        .id(5L)
        .name("글쓰기 동아리")
        .type(MeetingType.REGULAR)
        .title("글쓰기 동아리 모집")
        .content("매주 수요일 글쓰기를 연습하고 토론을 진행합니다.")
        .meta("")
        .categories(List.of(MeetingCategory.RESEARCH))
        .analyzedFeatures(List.of("글쓰기", "토론"))
        .analyzedIntroduction("글쓰기를 연습하고 토론을 진행합니다.")
        .build();
        meetings.add(meeting5);

        // 여행 동아리
        Meeting meeting6 = Meeting.builder()
        .id(6L)
        .name("여행 동아리")
        .type(MeetingType.REGULAR)
        .title("여행 동아리 모집")
        .content("매주 수요일 여행을 계획하고 여행 계획을 공유합니다.")
        .meta("")
        .categories(List.of(MeetingCategory.RESEARCH))
        .analyzedFeatures(List.of("여행", "계획", "공유"))
        .analyzedIntroduction("여행을 계획하고 여행 계획을 공유합니다.")
        .build();
        meetings.add(meeting6);

        // 대외활동 동아리
        Meeting meeting7 = Meeting.builder()
        .id(7L)
        .name("대외활동 동아리")
        .type(MeetingType.REGULAR)
        .title("대외활동 동아리 모집")
        .content("매주 수요일 대외활동을 진행하여 스펙을 쌓아봐요.")
        .meta("")
        .categories(List.of(MeetingCategory.RESEARCH))
        .analyzedFeatures(List.of("대외활동", "스펙", "취업"))
        .analyzedIntroduction("열심히 활동하여 취업을 위한 좋은 스펙을 쌓고 싶은 사람에게 추천합니다.")
        .build();
        meetings.add(meeting7);

        // 사진 동아리
        Meeting meeting8 = Meeting.builder()
        .id(8L)
        .name("사진 동아리")
        .type(MeetingType.REGULAR)
        .title("사진 동아리 모집")
        .content("매주 수요일 사진을 찍고 토론을 진행합니다.")
        .meta("")
        .categories(List.of(MeetingCategory.RESEARCH))
        .analyzedFeatures(List.of("사진", "토론"))
        .analyzedIntroduction("사진을 찍고 토론을 진행합니다.")
        .build();
        meetings.add(meeting8);
        
        // 댄스 동아리
        Meeting meeting9 = Meeting.builder()
        .id(9L)
        .name("댄스 동아리")
        .type(MeetingType.REGULAR)
        .title("댄스 동아리 모집")
        .content("매주 수요일 kpop 댄스를 하나 정해 연습을 진행하고, 공연합니다.")
        .meta("")
        .categories(List.of(MeetingCategory.RESEARCH))
        .analyzedFeatures(List.of("댄스", "춤", "공연"))
        .analyzedIntroduction("kpop 댄스를 하나 정해 연습을 진행하고, 공연합니다.")
        .build();
        meetings.add(meeting9);

        return meetings;
    }

    private List<User> generateTestUsers() {
        List<User> users = new ArrayList<>();

        // User 1
        User user1 = User.builder()
        .id("user-uuid-1")
        .username("김시립")
        .major(Major.COMPUTER_SCIENCE)
        .studentId("20210001")
        .attendanceScore((byte) 95)
        .features(List.of("적극적", "리더십", "책임감"))
        .build();
        users.add(user1);

        // User 2
        User user2 = User.builder()
        .id("user-uuid-2")
        .username("이문학")
        .major(Major.ENGLISH_LANGUAGE_AND_LITERATURE)
        .studentId("20210002")
        .attendanceScore((byte) 88)
        .features(List.of("창의적", "소통능력", "문학적감각"))
        .build();
        users.add(user2);

        // 노는 걸 좋아하는 User 3
        User user3 = User.builder()
        .id("user-uuid-3")
        .username("박노예")
        .major(Major.SPORTS_SCIENCE)
        .studentId("20210003")
        .attendanceScore((byte) 70)
        .features(List.of("친목", "술", "의리"))
        .build();
        users.add(user3);

        return users;
    }
}