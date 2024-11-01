import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingInfo;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingMemberElement;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.meeting.model.enums.*;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.enums.Major;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class featureextract {
    private static final String GOOGLE_API_KEY = "AIzaSyCwhG_WnlWN91KY3BWCGGSpmaZt6PGWjc";
    private static final String LOCATION = "us-central1";
    private static final String MODEL_NAME = "gemini-1.5-flash-001";
    
    private static VertexAI vertexAI;
    private static GenerativeModel model;
    
    static {
        try {
            vertexAI = new VertexAI(PROJECT_ID, LOCATION);
            model = new GenerativeModel(MODEL_NAME, vertexAI);
        } catch (Exception e) {
            System.err.println("Error initializing Vertex AI: " + e.getMessage());
        }
    }

    // 기존 모임 분석용 프롬프트
    private static final String FEATURE_PROMPT = """
            당신은 서울시립대학교 학생들의 모임 게시글을 분석하는 전문가입니다.
            다음 게시글에서 가장 중요한 3개의 특징을 추출해주세요:
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
            다음 학생의 정보를 바탕으로 가장 중요한 3개의 특징을 추출해주세요:
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

    // History 기반 업데이트용 프롬프트
    private static final String UPDATE_MEETING_FEATURES_PROMPT = """
            당신은 서울시립대학교 학생들의 모임 활동을 분석하는 전문가입니다.
            새로운 활동 기록을 바탕으로 모임의 특징을 업데이트해주세요.
            
            기존 모임 특징:
            %s
            
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
            
            기존 모임 소개:
            %s
            
            새로운 활동 기록:
            제목: %s
            내용: %s
            장소: %s
            날짜: %s
            
            업데이트된 모임 소개(한 문장):""";

    private static final String UPDATE_USER_FEATURES_PROMPT = """
            당신은 서울시립대학교 학생들의 특징을 분석하는 전문가입니다.
            새로운 활동 기록을 바탕으로 사용자의 특징을 업데이트해주세요.
            
            기존 사용자 특징:
            %s
            
            새로운 활동 기록:
            제목: %s
            내용: %s
            장소: %s
            날짜: %s
            
            업데이트된 사용자 특징을 다음 형식으로 제공해주세요:
            1.
            2.
            3.""";

    // 기존 모임 분석 메서드
    public static List<String> analyzeFeatures(MeetingInfo meeting) throws Exception {
        String prompt = String.format(FEATURE_PROMPT, 
            meeting.getName(),
            meeting.getType(),
            meeting.getTitle(),
            meeting.getContent(),
            formatCategories(meeting.getCategories()),
            meeting.getCurrentParticipant(),
            meeting.getMaxParticipant(),
            meeting.getMinParticipant(),
            meeting.getStartDate(),
            meeting.getEndDate(),
            meeting.getStartTime(),
            meeting.getEndTime(),
            meeting.getLocation(),
            meeting.getApplicationMethod());
            
            GenerateContentResponse response = model.generateContent(prompt);
            return parseFeatures(ResponseHandler.getText(response));
    }

    public static String generateSummary(MeetingInfo meeting) throws Exception {
        String prompt = String.format(SUMMARY_PROMPT,
            meeting.getName(),
            meeting.getType(),
            meeting.getTitle(),
            meeting.getContent(),
            formatCategories(meeting.getCategories()),
            meeting.getCurrentParticipant(),
            meeting.getMaxParticipant(),
            meeting.getMinParticipant(),
            meeting.getStartDate(),
            meeting.getEndDate(),
            meeting.getStartTime(),
            meeting.getEndTime(),
            meeting.getLocation());

        GenerateContentResponse response = model.generateContent(prompt);
        return ResponseHandler.getText(response).trim();
    }

    public static List<String> analyzeMember(MeetingMemberElement member) throws Exception {
        String prompt = String.format(MEMBER_PROMPT,
            member.getName(),
            member.getMajor(),
            member.getAttendenceScore(),
            member.getRole(),
            String.join(", ", member.getFeatures()),
            member.getStudentId());
            
        GenerativeResponse response = model.generateContent(prompt);
        return parseFeatures(response.getText());
    }

    // History 기반 업데이트 메서드
    public static List<String> updateMeetingFeatures(History history, List<String> currentFeatures) throws Exception {
        String prompt = String.format(UPDATE_MEETING_FEATURES_PROMPT,
            String.join("\n", currentFeatures),
            history.getTitle(),
            history.getContent(),
            history.getLocation(),
            history.getDate().toString());
            
        GenerativeResponse response = model.generateContent(prompt);
        return parseFeatures(response.getText());
    }

    public static String updateMeetingIntroduction(History history, String currentIntro) throws Exception {
        String prompt = String.format(UPDATE_MEETING_INTRO_PROMPT,
            currentIntro,
            history.getTitle(),
            history.getContent(),
            history.getLocation(),
            history.getDate().toString());
            
        GenerativeResponse response = model.generateContent(prompt);
        return response.getText().trim();
    }

    public static List<String> updateUserFeatures(History history, List<String> currentFeatures) throws Exception {
        String prompt = String.format(UPDATE_USER_FEATURES_PROMPT,
            String.join("\n", currentFeatures),
            history.getTitle(),
            history.getContent(),
            history.getLocation(),
            history.getDate().toString());
            
        GenerativeResponse response = model.generateContent(prompt);
        return parseFeatures(response.getText());
    }

    // 유틸리티 메서드
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

    // 통합 분석 메서드
    public static void analyzeMeetingComplete(MeetingInfo meeting) {
        try {
            List<String> characteristics = analyzeFeatures(meeting);
            meeting.setAnalyzedFeatures(characteristics);

            String summary = generateSummary(meeting);
            meeting.setAnalyzedIntroduction(summary);
        } catch (Exception e) {
            System.err.println("Error analyzing meeting: " + e.getMessage());
        }
    }

    public static void analyzeMemberComplete(MeetingMemberElement member) {
        try {
            List<String> characteristics = analyzeMember(member);
            member.getFeatures().addAll(characteristics);
        } catch (Exception e) {
            System.err.println("Error analyzing member: " + e.getMessage());
        }
    }

    public static void updateFromHistory(History history) {
        try {
            Meeting meeting = history.getMeeting();
            User user = history.getUser();
            
            // 모임 특징 업데이트
            List<String> updatedMeetingFeatures = updateMeetingFeatures(history, meeting.getFeatures());
            meeting.getFeatures().clear();
            meeting.getFeatures().addAll(updatedMeetingFeatures);
            
            // 모임 소개 업데이트
            String updatedMeetingIntro = updateMeetingIntroduction(history, meeting.getAnalyzedIntroduction());
            meeting.setAnalyzedIntroduction(updatedMeetingIntro);
            
            // 사용자 특징 업데이트
            List<String> updatedUserFeatures = updateUserFeatures(history, user.getFeatures());
            user.getFeatures().clear();
            user.getFeatures().addAll(updatedUserFeatures);

        } catch (Exception e) {
            System.err.println("Error updating content from history: " + e.getMessage());
        }
    }

    // 사용 예시
    public static void main(String[] args) {
        // 모임 분석 예시
        MeetingInfo meeting = MeetingInfo.builder()
            .name("알고리즘 스터디")
            .type(MeetingType.STUDY)
            .title("코딩테스트 준비 스터디원 모집")
            .content("매주 알고리즘 문제를 풀고 코드리뷰를 진행합니다. " +
                    "백준 골드 이상 문제를 함께 풀어보며 실력을 향상시킬 수 있습니다.")
            .categories(List.of(MeetingCategory.PROGRAMMING))
            .currentParticipant(3)
            .maxParticipant(5)
            .minParticipant(2)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusMonths(1))
            .startTime(LocalTime.of(19, 0))
            .endTime(LocalTime.of(21, 0))
            .location("온라인")
            .applicationMethod(MeetingMemberApplicationMethod.AUTOMATIC)
            .build();

        // 멤버 분석 예시
        MeetingMemberElement member = new MeetingMemberElement(
            1L,
            "홍길동",
            "profile.jpg",
            95,
            Major.COMPUTER_SCIENCE,
            "20201234",
            "010-1234-5678",
            new ArrayList<>(Arrays.asList("적극적인 참여", "팀워크 우수")),
            MeetingMemberRole.LEADER
        );

        // History 분석 예시
        History history = History.builder()
            .title("알고리즘 스터디 첫 번째 세션")
            .content("첫 번째 세션에서는 기초 알고리즘을 다루고 연습 문제를 풀어봤습니다.")
            .location("온라인")
            .date(LocalDateTime.now())
            .build();

        // 분석 실행
        analyzeMeetingComplete(meeting);
        analyzeMemberComplete(member);
        updateFromHistory(history);
        
        // 결과 출력
        System.out.println("Meeting Analysis:");
        System.out.println("Features: " + meeting.getAnalyzedFeatures());
        System.out.println("Summary: " + meeting.getAnalyzedIntroduction());
        
        System.out.println("\nMember Analysis:");
        System.out.println("Features: " + member.getFeatures());
    }
}