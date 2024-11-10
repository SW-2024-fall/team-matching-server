package swe.second.team_matching_server.domain.meeting.FeatureExtract;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingMemberElement;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.meeting.model.enums.*;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.enums.Major;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class ContentAnalyzer {
    private static final String PROJECT_ID = "named-berm-382814";
    private static final String LOCATION = "us-central1";
    private static final ChatLanguageModel model;

    static {
        try {
            String credentialsPath = "C:/Users/kkms4641/named-berm-382814-d242aad5ed24.json";
            
            System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", credentialsPath);

            model = VertexAiGeminiChatModel.builder()
                .project(PROJECT_ID)
                .location(LOCATION)
                .modelName("gemini-1.5-flash-002")
                .build();   

            
       } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Google credentials", e);
        }
    }

    private static final String FEATURE_PROMPT = """
            당신은 서울시립대학교 학생들의 모임 게시글의 제목과 내용에서 주요 토픽을 추출하는 전문가입니다.
            당신이 추출하는 토픽은 학생들이 모임 게시글의 특징을 잘 파악할 수 있도록 해야 됩니다.
            모임 게시글의 특징은 문장이 아닌 단어로 구성되어야 합니다.

            다음 게시글에서 가장 중요한 3개의 토픽을 추출해주세요:
            1. 모임의 목적
            2. 필요한 역량
            3. 모임 성격
            
            모임명: %s
            모임 유형: %s
            제목: %s
            내용: %s
            카테고리: %s
            참여 인원: %d/%d명 (최소 %d명)
            일정: %s ~ %s
            시간: %s ~ %s
            장소: %s
            신청 방식: %s
            
            특징:""";

    private static final String SUMMARY_PROMPT = """
            당신은 서울시립대학교 학생들의 모임을 한 문장으로 요약하는 전문가입니다.
            당신이 요약하는 문장은 학생들이 모임 게시글의 특징을 잘 파악할 수 있도록 해야 됩니다.
            다음 모임 정보를 읽고, 핵심 내용을 포함한 간결한 한 문장으로 요약해주세요.
            
            모임명: %s
            모임 유형: %s
            제목: %s
            내용: %s
            카테고리: %s
            참여 인원: %d/%d명 (최소 %d명)
            일정: %s ~ %s
            시간: %s ~ %s
            장소: %s
            
            한 문장 요약:""";

    private static final String MEMBER_PROMPT = """
            당신은 서울시립대학교 학생들의 특징을 분석하는 전문가입니다.
            당신이 분석한 특징은 학생들의 특성을 잘 파악할 수 있도록 해야 됩니다.
            다음 학생의 정보를 바탕으로 가장 중요한 3개의 특징을 추출해주세요:
            학생의 특징은 문장이 아닌 단어로 구성되어야 합니다.

            1. 전문성
            2. 신뢰도
            3. 역할 적합성
            
            이름: %s
            전공: %s
            출석 점수: %d
            역할: %s
            기존 특징: %s
            학번: %s
            
            특징:""";

    private static final String UPDATE_MEETING_FEATURES_PROMPT = """
            당신은 서울시립대학교 학생들의 모임 활동을 분석하는 전문가입니다.
            새로운 활동 기록을 바탕으로 모임의 특징을 업데이트해주세요.
            모임의 특징은 문장이 아닌 단어로 구성되어야 합니다.
            
            기존 모임 특징: %s
            새로운 활동 기록:
            제목: %s
            내용: %s
            장소: %s
            날짜: %s
            
            업데이트된 모임 특징을 다음 형식으로 제공해주세요:
            1.
            2.
            3.""";

    private static final String UPDATE_MEETING_INTRO_PROMPT = """
            당신은 서울시립대학교 학생들의 모임을 한 문장으로 소개하는 전문가입니다.
            새로운 활동 기록을 바탕으로 모임 소개를 업데이트해주세요.
            
            기존 모임 소개: %s
            새로운 활동 기록:
            제목: %s
            내용: %s
            장소: %s
            날짜: %s
            
            업데이트된 모임 소개(한 문장):""";

    private static final String UPDATE_USER_FEATURES_PROMPT = """
            당신은 서울시립대학교 학생들의 특징을 분석하는 전문가입니다.
            새로운 활동 기록을 바탕으로 사용자의 특징을 업데이트해주세요.
            사용자의 특징은 문장이 아닌 단어로 구성되어야 합니다.
            
            기존 사용자 특징: %s
            새로운 활동 기록:
            제목: %s
            내용: %s
            장소: %s
            날짜: %s
            
            업데이트된 사용자 특징을 다음 형식으로 제공해주세요:
            1.
            2.
            3.""";

    public static List<String> analyzeFeatures(Meeting meeting) throws Exception {
        String prompt = String.format(FEATURE_PROMPT,
                meeting.getName(),
                meeting.getType(),
                meeting.getTitle(),
                meeting.getContent(),
                formatCategories(meeting.getCategories()),
                meeting.getCurrentParticipants(),
                meeting.getMaxParticipant(),
                meeting.getMinParticipant(),
                meeting.getStartDate(),
                meeting.getEndDate(),
                meeting.getStartTime(),
                meeting.getEndTime(),
                meeting.getLocation(),
                meeting.getApplicationMethod());

        String feature_response = model.generate(prompt);

        System.out.println(prompt);
        return parseFeatures(feature_response);
    }

    public static String generateSummary(Meeting meeting) throws Exception {
        String summary_prompt = String.format(SUMMARY_PROMPT,
                meeting.getName(),
                meeting.getType(),
                meeting.getTitle(),
                meeting.getContent(),
                formatCategories(meeting.getCategories()),
                meeting.getCurrentParticipants(),
                meeting.getMaxParticipant(),
                meeting.getMinParticipant(),
                meeting.getStartDate(),
                meeting.getEndDate(),
                meeting.getStartTime(),
                meeting.getEndTime(),
                meeting.getLocation());

        String summary_response = model.generate(summary_prompt);
        return summary_response.trim();
    }

    public static List<String> analyzeMember(MeetingMemberElement member) throws Exception {
        String member_prompt = String.format(MEMBER_PROMPT,
                member.getName(),
                member.getMajor(),
                member.getAttendenceScore(),
                member.getRole(),
                String.join(", ", member.getFeatures()),
                member.getStudentId());

        String member_response = model.generate(member_prompt);
        return parseFeatures(member_response);
    }

    public static List<String> updateMeetingFeatures(History history, List<String> currentFeatures) throws Exception {
        String update_meeting_prompt = String.format(UPDATE_MEETING_FEATURES_PROMPT,
                String.join("\n", currentFeatures),
                history.getTitle(),
                history.getContent(),
                history.getLocation(),
                history.getDate().toString());

        String update_meeting_response = model.generate(update_meeting_prompt);
        return parseFeatures(update_meeting_response);
    }

    public static String updateMeetingIntroduction(History history, String currentIntro) throws Exception {
        String update_meeting_introduction_prompt = String.format(UPDATE_MEETING_INTRO_PROMPT,
                currentIntro,
                history.getTitle(),
                history.getContent(),
                history.getLocation(),
                history.getDate().toString());

        String update_meeting_introduction_response = model.generate(update_meeting_introduction_prompt);
        return update_meeting_introduction_response.trim();
    }

    public static List<String> updateUserFeatures(History history, List<String> currentFeatures) throws Exception {
        String update_user_feature_prompt = String.format(UPDATE_USER_FEATURES_PROMPT,
                String.join("\n", currentFeatures),
                history.getTitle(),
                history.getContent(),
                history.getLocation(),
                history.getDate().toString());

        String update_user_feature_response = model.generate(update_user_feature_prompt);
        return parseFeatures(update_user_feature_response);
    }

    private static String formatCategories(List<MeetingCategory> categories) {
        return categories.stream()
                .map(MeetingCategory::toString)
                .collect(Collectors.joining(", "));
    }

    private static List<String> parseFeatures(String response) {
        List<String> features = new ArrayList<>();
        String[] lines = response.split("\n");
        for (String line : lines) {
            if (line.matches("\\d+\\..*")) {
                features.add(line.substring(line.indexOf(".") + 1).trim());
            }
        }
        return features;
    }

    public static void analyzeMeetingComplete(Meeting meeting) {
        try {
            List<String> characteristics = analyzeFeatures(meeting);
            meeting.updateAnalyzedFeatures(characteristics);
            String summary = generateSummary(meeting);
            meeting.updateAnalyzedIntroduction(summary);
        } catch (Exception e) {
            throw new ContentAnalysisException("Error analyzing meeting", e);
        }
    }

    public static void analyzeMemberComplete(MeetingMemberElement member) {
        try {
            List<String> characteristics = analyzeMember(member);
            member.getFeatures().addAll(characteristics);
        } catch (Exception e) {
            throw new ContentAnalysisException("Error analyzing member", e);
        }
    }

    public static void updateFromHistory(History history) {
        try {
            Meeting meeting = history.getMeeting();
            User user = history.getUser();

            // 모임 특징 업데이트
            List<String> updatedMeetingFeatures = updateMeetingFeatures(history, meeting.getFeatures());
            meeting.updateFeatures(updatedMeetingFeatures);

            // 모임 소개 업데이트
            String updatedMeetingIntro = updateMeetingIntroduction(history, meeting.getAnalyzedIntroduction());
            meeting.updateAnalyzedIntroduction(updatedMeetingIntro);

            // 사용자 특징 업데이트
            List<String> updatedUserFeatures = updateUserFeatures(history, user.getFeatures());
            user.getFeatures().clear();
            user.getFeatures().addAll(updatedUserFeatures);
        } catch (Exception e) {
            throw new ContentAnalysisException("Error updating content from history", e);
        }
    }
    public static void main(String[] args) {
        User user = User.builder()
            .username("홍길동")
            .email("hong@uos.ac.kr")
            .major(Major.COMPUTER_SCIENCE)
            .phoneNumber("010-1234-5678")
            .attendanceScore((byte) 80)
            .features(new ArrayList<>(Arrays.asList("적극적", "팀워크")))
            .build();

        Meeting meeting = Meeting.builder()
            .name("알고리즘 스터디")
            .type(MeetingType.REGULAR)
            .title("코딩테스트 준비 스터디원 모집")
            .content("매주 알고리즘 문제를 풀고 코드리뷰를 진행합니다. " +
                    "백준 골드 이상 문제를 함께 풀어보며 실력을 향상시킬 수 있습니다.")
            .categories(List.of(MeetingCategory.RESEARCH))
            .minParticipant((byte)2)
            .maxParticipant((byte)10)
            .currentParticipants(0)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusMonths(1))
            .startTime(LocalTime.of(19, 0))
            .endTime(LocalTime.of(21, 0))
            .location("온라인")
            .applicationMethod(MeetingMemberApplicationMethod.FIRST_COME_FIRST_SERVED)
            .build();

        MeetingMemberElement member = MeetingMemberElement.builder()
            .id("1")
            .name("홍길동")
            .profileUrl("profile.jpg")
            .attendenceScore(95)
            .major(Major.COMPUTER_SCIENCE)
            .studentId("20201234")
            .phoneNumber("010-1234-5678")
            .features(new ArrayList<>(Arrays.asList("적극적인 참여", "팀워크 우수")))
            .role(MeetingMemberRole.LEADER)
            .build();

        History history = History.builder()
            .title("알고리즘 스터디 첫 번째 세션")
            .content("첫 번째 세션에서는 기초 알고리즘을 다루고 연습 문제를 풀어봤습니다.")
            .location("온라인")
            .date(LocalDate.now())
            .user(user)
            .meeting(meeting)
            .isPublic(true)
            .build();

        try {
            // 모임 분석 실행
            System.out.println("=== 모임 분석 시작 ===");
            analyzeMeetingComplete(meeting);
            System.out.println("모임 특징: " + meeting.getAnalyzedFeatures());
            System.out.println("모임 소개: " + meeting.getAnalyzedIntroduction());
        
            // 멤버 분석 실행
            System.out.println("\n=== 멤버 분석 시작 ===");
            analyzeMemberComplete(member);
            System.out.println("멤버 특징: " + member.getFeatures());
        
            // 히스토리 기반 업데이트 실행
            System.out.println("\n=== 히스토리 기반 업데이트 시작 ===");
            updateFromHistory(history);
            System.out.println("업데이트된 모임 특징: " + meeting.getFeatures());
            System.out.println("업데이트된 사용자 특징: " + user.getFeatures());
        
            // 분석 결과 요약
            System.out.println("\n=== 최종 분석 결과 ===");
            System.out.println("1. 모임 정보:");
            System.out.println("   - 이름: " + meeting.getName());
            System.out.println("   - 현재 참여자: " + meeting.getCurrentParticipants() + 
                            "/" + meeting.getMaxParticipant() + "명");
            System.out.println("   - 분석된 특징: " + meeting.getAnalyzedFeatures());
            System.out.println("   - 분석된 소개: " + meeting.getAnalyzedIntroduction());
        
            System.out.println("\n2. 멤버 정보:");
            System.out.println("   - 이름: " + member.getName());
            System.out.println("   - 역할: " + member.getRole());
            System.out.println("   - 특징: " + member.getFeatures());
        
        } catch (Exception e) {
            System.err.println("분석 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
class ContentAnalysisException extends RuntimeException {
    public ContentAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}