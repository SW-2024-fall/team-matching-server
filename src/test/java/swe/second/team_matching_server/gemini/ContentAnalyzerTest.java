package swe.second.team_matching_server.gemini;

import swe.second.team_matching_server.core.gemini.*;
import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingMemberApplicationMethod;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingType;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.enums.Major;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SpringBootTest
public class ContentAnalyzerTest {
    @Autowired
    private ContentAnalyzer analyzer;

    @Test
    public void testAnalyzeMeetingAndMember() {
        log.info("GOOGLE_APPLICATION_CREDENTIALS: " 
                            + System.getProperty("GOOGLE_APPLICATION_CREDENTIALS"));
        assertEquals("/Users/gimhyeju/team-matching-server/gemini-credential.json", 
                        System.getProperty("GOOGLE_APPLICATION_CREDENTIALS"));
        // 사용자 생성
        User user = User.builder()
                .username("홍길동")
                .email("hong@uos.ac.kr")
                .major(Major.COMPUTER_SCIENCE)
                .phoneNumber("010-1234-5678")
                .attendanceScore((byte) 80)
                .features(new ArrayList<>(Arrays.asList("적극적", "팀워크")))
                .build();

        // 모임 생성
        Meeting meeting = Meeting.builder()
                .name("알고리즘 스터디")
                .type(MeetingType.REGULAR)
                .title("코딩테스트 준비 스터디원 모집")
                .content("매주 알고리즘 문제를 풀고 코드리뷰를 진행합니다.")
                .categories(List.of(MeetingCategory.RESEARCH))
                .minParticipant((byte) 2)
                .maxParticipant((byte) 10)
                .currentParticipants(0)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .startTime(LocalTime.of(19, 0))
                .endTime(LocalTime.of(21, 0))
                .location("온라인")
                .applicationMethod(MeetingMemberApplicationMethod.FIRST_COME_FIRST_SERVED)
                .build();

        // 히스토리 생성
        History history = History.builder()
                .title("알고리즘 스터디 첫 번째 세션")
                .content("첫 번째 세션에서는 기초 알고리즘을 다루고 연습 문제를 풀어봤습니다. 모두가 아주 열정적으로 참여하였고, 끝나고는 뒤풀이에서 술과 함께 친목을~")
                .location("온라인")
                .date(LocalDate.now())
                .user(user)
                .meeting(meeting)
                .isPublic(true)
                .build();

        // 모임 분석 실행
        log.info("=== 모임 분석 시작 ===");
        analyzer.analyzeMeetingComplete(meeting);
        log.info("모임 특징: " + meeting.getAnalyzedFeatures());
        log.info("모임 소개: " + meeting.getAnalyzedIntroduction());
    
        // 멤버 분석 실행
        log.info("\n=== 멤버 분석 시작 ===");
        analyzer.analyzeUserComplete(user);
        log.info("업데이트된 사용자 특징: " + user.getFeatures());
    
        // 히스토리 기반 업데이트 실행
        log.info("\n=== 히스토리 기반 업데이트 시작 ===");
        meeting = analyzer.updateMeetingFromHistory(history, meeting);
        log.info("업데이트된 모임 특징: " + meeting.getAnalyzedFeatures());
        user = analyzer.updateUserFromHistory(history, user);
        log.info("업데이트된 사용자 특징: " + user.getFeatures());
    
        // 분석 결과 요약
        log.info("\n=== 최종 분석 결과 ===");
        log.info("1. 모임 정보:");
        log.info("   - 분석된 특징: " + meeting.getAnalyzedFeatures());
        log.info("   - 분석된 소개: " + meeting.getAnalyzedIntroduction());
    
        log.info("\n2. 멤버 정보:");
        log.info("   - 이름: " + user.getUsername());
        log.info("   - 특징: " + user.getFeatures());

        // 결과 검증
        assertEquals(3, meeting.getAnalyzedFeatures().size());
        assertEquals(3, user.getFeatures().size());
    }
}
