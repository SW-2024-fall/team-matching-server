package swe.second.team_matching_server.core.featureExtract;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MeetingRecommender {
    private static final String PROJECT_ID = "named-berm-382814";
    private static final String LOCATION = "us-central1";
    private static final String MODEL_NAME = "gemini-1.5-flash-002"; // Add model name
    private static final ChatLanguageModel model;

    private static void setGoogleCredentials() {
        try {
            // Use direct absolute path to credentials
            String credentialsPath = "C:\\Users\\kkms4641\\named-berm-382814-d242aad5ed24.json";

            // Verify file exists
            File credentialsFile = new File(credentialsPath);
            if (!credentialsFile.exists()) {
                throw new RuntimeException("Credentials file not found at: " + credentialsPath);
            }

            // Set Google credentials property with normalized path
            String normalizedPath = credentialsFile.getAbsolutePath();
            System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", normalizedPath);
            System.out.println("Set credentials path to: " + normalizedPath);

        } catch (Exception e) {
            throw new RuntimeException("Failed to set Google credentials: " + e.getMessage(), e);
        }
    }

    private static final String RECOMMENDATION_PROMPT = """
            당신은 서울시립대학교 학생들을 위한 모임 추천 전문가입니다.
            다음 학생의 정보와 현재 진행 중인 모임들의 정보를 바탕으로 이 학생에게 가장 적합한 모임 3개를 추천해주세요.

            학생 정보:
            이름: %s
            전공: %s
            학번: %s
            출석 점수: %d
            개인 특성: %s
            참여 모임 특성: %s

            추천 가능한 모임 목록:
            %s

            위 정보를 바탕으로 정확히 다음 형식으로만 추천해주세요:
            1. [1]: 추천 이유
            2. [2]: 추천 이유
            3. [3]: 추천 이유

            반드시 모임 ID만 대괄호 안에 숫자로 입력해주세요.
            """;

    static {
        setGoogleCredentials();
        model = VertexAiGeminiChatModel.builder()
                .project(PROJECT_ID)
                .location(LOCATION)
                .modelName(MODEL_NAME)
                .build();
    }

    public static void main(String[] args) {
        // 테스트 데이터 생성
        List<Map<String, Object>> meetings = generateTestMeetings();
        List<Map<String, Object>> users = generateTestUsers();

        // 각 사용자에 대한 추천 실행
        for (Map<String, Object> user : users) {
            try {
                System.out.println("\n=== " + user.get("username") + "님을 위한 추천 ===");
                List<MeetingRecommendation> recommendations = recommendMeetings(user, meetings);
                recommendations.forEach(System.out::println);
            } catch (Exception e) {
                System.err.println("추천 생성 중 오류 발생: " + e.getMessage());
            }
        }
    }

    private static List<Map<String, Object>> generateTestMeetings() {
        List<Map<String, Object>> meetings = new ArrayList<>();

        // Meeting 1: 알고리즘 스터디
        Map<String, Object> meeting1 = new HashMap<>();
        meeting1.put("id", 1L);
        meeting1.put("userRole", "MEMBER");

        Map<String, Object> info1 = new HashMap<>();
        info1.put("name", "알고리즘 스터디");
        info1.put("type", "REGULAR");
        info1.put("title", "코딩테스트 준비 스터디원 모집");
        info1.put("content", "매주 알고리즘 문제를 풀고 코드리뷰를 진행합니다.");
        info1.put("thumbnailUrls", Arrays.asList("https://example.com/algo.jpg"));
        info1.put("startDate", "2024-11-10");
        info1.put("endDate", "2024-12-31");
        info1.put("startTime", "19:00:00");
        info1.put("endTime", "21:00:00");
        info1.put("location", "창공관 스터디룸");
        info1.put("currentParticipant", 8);
        info1.put("minParticipant", 4);
        info1.put("maxParticipant", 12);
        info1.put("meta", "노트북 필수");
        info1.put("categories", Arrays.asList("STUDY", "RESEARCH"));
        info1.put("features", Arrays.asList("알고리즘", "코딩", "팀워크"));
        info1.put("analyzedFeatures", Arrays.asList("문제해결력", "논리적사고", "협업"));
        info1.put("analyzedIntroduction", "코딩 실력 향상을 위한 체계적인 알고리즘 스터디입니다");
        info1.put("applicationMethod", "LEADER_ACCEPT");
        info1.put("likes", 15);
        info1.put("comments", 5);
        info1.put("scraps", 8);

        meeting1.put("info", info1);
        meetings.add(meeting1);

        // Meeting 2: 영어회화 클럽
        Map<String, Object> meeting2 = new HashMap<>();
        meeting2.put("id", 2L);
        meeting2.put("userRole", "MEMBER");

        Map<String, Object> info2 = new HashMap<>();
        info2.put("name", "영어회화 클럽");
        info2.put("type", "REGULAR");
        info2.put("title", "원어민과 함께하는 영어회화");
        info2.put("content", "매주 수요일 원어민 선생님과 함께 다양한 주제로 영어 토론을 진행합니다.");
        info2.put("thumbnailUrls", Arrays.asList("https://example.com/english.jpg"));
        info2.put("startDate", "2024-11-15");
        info2.put("endDate", "2024-12-20");
        info2.put("startTime", "17:00:00");
        info2.put("endTime", "19:00:00");
        info2.put("location", "인문관 세미나실");
        info2.put("currentParticipant", 6);
        info2.put("minParticipant", 4);
        info2.put("maxParticipant", 8);
        info2.put("meta", "중급 이상 영어실력 필요");
        info2.put("categories", Arrays.asList("LANGUAGE", "CULTURE"));
        info2.put("features", Arrays.asList("영어", "토론", "문화교류"));
        info2.put("analyzedFeatures", Arrays.asList("의사소통", "글로벌", "자기개발"));
        info2.put("analyzedIntroduction", "실전 영어회화 능력을 키우는 소규모 스터디입니다");
        info2.put("applicationMethod", "LEADER_ACCEPT");

        meeting2.put("info", info2);
        meetings.add(meeting2);

        // Meeting 3: 축구 동아리
        Map<String, Object> meeting3 = new HashMap<>();
        meeting3.put("id", 3L);
        meeting3.put("userRole", "MEMBER");

        Map<String, Object> info3 = new HashMap<>();
        info3.put("name", "UOS FC");
        info3.put("type", "REGULAR");
        info3.put("title", "주말 축구 정기모임");
        info3.put("content", "매주 토요일 교내 운동장에서 축구를 합니다. 실력과 상관없이 즐기실 분들 환영합니다!");
        info3.put("thumbnailUrls", Arrays.asList("https://example.com/soccer.jpg"));
        info3.put("startDate", "2024-11-16");
        info3.put("endDate", "2024-12-28");
        info3.put("startTime", "10:00:00");
        info3.put("endTime", "12:00:00");
        info3.put("location", "대운동장");
        info3.put("currentParticipant", 15);
        info3.put("minParticipant", 10);
        info3.put("maxParticipant", 22);
        info3.put("meta", "운동복, 축구화 필수");
        info3.put("categories", Arrays.asList("SPORTS", "SOCIAL"));
        info3.put("features", Arrays.asList("축구", "운동", "친목"));
        info3.put("analyzedFeatures", Arrays.asList("체력단련", "팀워크", "스포츠"));
        info3.put("analyzedIntroduction", "주말마다 함께 축구하며 친목을 다지는 동아리입니다");
        info3.put("applicationMethod", "AUTO");

        meeting3.put("info", info3);
        meetings.add(meeting3);

        Map<String, Object> meeting4 = new HashMap<>();
        meeting4.put("id", 4L);
        meeting4.put("userRole", "MEMBER");

        Map<String, Object> info4 = new HashMap<>();
        info4.put("name", "책읽는 대학생");
        info4.put("type", "REGULAR");
        info4.put("title", "월간 독서토론 모임");
        info4.put("content", "매월 한 권의 책을 선정하여 깊이 있는 토론을 진행합니다. 인문학, 사회과학 서적을 중심으로 다양한 분야의 책을 읽습니다.");
        info4.put("thumbnailUrls", Arrays.asList("https://example.com/book.jpg"));
        info4.put("startDate", "2024-11-20");
        info4.put("endDate", "2024-12-20");
        info4.put("startTime", "15:00:00");
        info4.put("endTime", "17:00:00");
        info4.put("location", "중앙도서관 세미나실");
        info4.put("currentParticipant", 7);
        info4.put("minParticipant", 5);
        info4.put("maxParticipant", 10);
        info4.put("meta", "선정도서 구매 필요");
        info4.put("categories", Arrays.asList("CULTURE", "STUDY"));
        info4.put("features", Arrays.asList("독서", "토론", "인문학"));
        info4.put("analyzedFeatures", Arrays.asList("지적성장", "비판적사고", "소통능력"));
        info4.put("analyzedIntroduction", "다양한 분야의 책을 통해 시야를 넓히는 독서토론 모임입니다");
        info4.put("applicationMethod", "LEADER_ACCEPT");

        meeting4.put("info", info4);
        meetings.add(meeting4);

        // Meeting 5: 취업준비 스터디
        Map<String, Object> meeting5 = new HashMap<>();
        meeting5.put("id", 5L);
        meeting5.put("userRole", "MEMBER");

        Map<String, Object> info5 = new HashMap<>();
        info5.put("name", "취준생 모여라");
        info5.put("type", "REGULAR");
        info5.put("title", "IT 기업 취업준비 스터디");
        info5.put("content", "면접 준비, 자소서 피드백, 코딩테스트 준비까지 함께합니다. 서로의 경험을 공유하며 성장해요.");
        info5.put("thumbnailUrls", Arrays.asList("https://example.com/job.jpg"));
        info5.put("startDate", "2024-11-25");
        info5.put("endDate", "2024-12-25");
        info5.put("startTime", "18:30:00");
        info5.put("endTime", "20:30:00");
        info5.put("location", "미래관 스터디룸");
        info5.put("currentParticipant", 6);
        info5.put("minParticipant", 4);
        info5.put("maxParticipant", 8);
        info5.put("meta", "취준생 우대");
        info5.put("categories", Arrays.asList("CAREER", "STUDY"));
        info5.put("features", Arrays.asList("취업", "면접", "자기소개서"));
        info5.put("analyzedFeatures", Arrays.asList("경력개발", "자기PR", "정보공유"));
        info5.put("analyzedIntroduction", "IT 취업을 준비하는 학생들의 정보 공유 모임입니다");
        info5.put("applicationMethod", "LEADER_ACCEPT");
        // 나머지 29개의 모임 데이터도 비슷한 형식으로 추가
        // ...

        // Meeting 6: 기타 동아리
        Map<String, Object> meeting6 = new HashMap<>();
        meeting6.put("id", 6L);
        meeting6.put("userRole", "MEMBER");

        Map<String, Object> info6 = new HashMap<>();
        info6.put("name", "어쿠스틱 기타");
        info6.put("type", "REGULAR");
        info6.put("title", "기타 연주 모임");
        info6.put("content", "매주 목요일 기타 연주와 노래를 함께 즐깁니다. 초보자도 환영!");
        info6.put("thumbnailUrls", Arrays.asList("https://example.com/guitar.jpg"));
        info6.put("startDate", "2024-11-21");
        info6.put("endDate", "2024-12-21");
        info6.put("startTime", "18:00:00");
        info6.put("endTime", "20:00:00");
        info6.put("location", "예술관 음악실");
        info6.put("currentParticipant", 5);
        info6.put("minParticipant", 3);
        info6.put("maxParticipant", 8);
        info6.put("meta", "기타 지참 필수");
        info6.put("categories", Arrays.asList("MUSIC", "HOBBY"));
        info6.put("features", Arrays.asList("음악", "기타", "공연"));
        info6.put("analyzedFeatures", Arrays.asList("예술성", "취미", "공연"));
        info6.put("analyzedIntroduction", "음악을 사랑하는 사람들의 기타 연주 모임입니다");
        info6.put("applicationMethod", "AUTO");

        meeting6.put("info", info6);
        meetings.add(meeting6);

        // Meeting 7: 봉사활동 모임
        Map<String, Object> meeting7 = new HashMap<>();
        meeting7.put("id", 7L);
        meeting7.put("userRole", "MEMBER");

        Map<String, Object> info7 = new HashMap<>();
        info7.put("name", "나눔이");
        info7.put("type", "REGULAR");
        info7.put("title", "주말 봉사활동");
        info7.put("content", "지역 사회를 위한 다양한 봉사활동을 진행합니다. 함께 의미있는 시간을 보내요.");
        info7.put("thumbnailUrls", Arrays.asList("https://example.com/volunteer.jpg"));
        info7.put("startDate", "2024-11-23");
        info7.put("endDate", "2024-12-23");
        info7.put("startTime", "10:00:00");
        info7.put("endTime", "12:00:00");
        info7.put("location", "중구 노인복지관");
        info7.put("currentParticipant", 12);
        info7.put("minParticipant", 5);
        info7.put("maxParticipant", 15);
        info7.put("meta", "봉사활동 확인서 발급 가능");
        info7.put("categories", Arrays.asList("VOLUNTEER", "SOCIAL"));
        info7.put("features", Arrays.asList("봉사", "나눔", "사회공헌"));
        info7.put("analyzedFeatures", Arrays.asList("이타심", "사회기여", "책임감"));
        info7.put("analyzedIntroduction", "따뜻한 마음으로 지역사회에 기여하는 봉사 모임입니다");
        info7.put("applicationMethod", "LEADER_ACCEPT");

        meeting7.put("info", info7);
        meetings.add(meeting7);

        // Meeting 8: 댄스 동아리
        Map<String, Object> meeting8 = new HashMap<>();
        meeting8.put("id", 8L);
        meeting8.put("userRole", "MEMBER");

        Map<String, Object> info8 = new HashMap<>();
        info8.put("name", "UOS 댄스팀");
        info8.put("type", "REGULAR");
        info8.put("title", "K-pop 커버댄스");
        info8.put("content", "최신 K-pop 댄스를 배우고 공연도 준비합니다. 열정만 있다면 누구나 환영!");
        info8.put("thumbnailUrls", Arrays.asList("https://example.com/dance.jpg"));
        info8.put("startDate", "2024-11-24");
        info8.put("endDate", "2024-12-24");
        info8.put("startTime", "17:00:00");
        info8.put("endTime", "19:00:00");
        info8.put("location", "체육관 댄스실");
        info8.put("currentParticipant", 8);
        info8.put("minParticipant", 4);
        info8.put("maxParticipant", 12);
        info8.put("meta", "운동복 필수");
        info8.put("categories", Arrays.asList("DANCE", "PERFORMANCE"));
        info8.put("features", Arrays.asList("댄스", "공연", "K-pop"));
        info8.put("analyzedFeatures", Arrays.asList("예술성", "체력", "표현력"));
        info8.put("analyzedIntroduction", "K-pop 댄스를 통해 열정을 표현하는 댄스 동아리입니다");
        info8.put("applicationMethod", "AUTO");

        meeting8.put("info", info8);
        meetings.add(meeting8);

        // Meeting 9: 취업 스터디
        Map<String, Object> meeting9 = new HashMap<>();
        meeting9.put("id", 9L);
        meeting9.put("userRole", "MEMBER");

        Map<String, Object> info9 = new HashMap<>();
        info9.put("name", "취준생 모임");
        info9.put("type", "REGULAR");
        info9.put("title", "IT 기업 취업준비");
        info9.put("content", "면접 준비, 자소서 피드백, 포트폴리오 리뷰까지 함께합니다. 서로의 경험을 공유하며 성장해요.");
        info9.put("thumbnailUrls", Arrays.asList("https://example.com/job.jpg"));
        info9.put("startDate", "2024-11-25");
        info9.put("endDate", "2024-12-25");
        info9.put("startTime", "18:30:00");
        info9.put("endTime", "20:30:00");
        info9.put("location", "미래관 세미나실");
        info9.put("currentParticipant", 8);
        info9.put("minParticipant", 5);
        info9.put("maxParticipant", 12);
        info9.put("meta", "취준생 우대");
        info9.put("categories", Arrays.asList("CAREER", "STUDY"));
        info9.put("features", Arrays.asList("취업준비", "면접", "자기소개서"));
        info9.put("analyzedFeatures", Arrays.asList("경력개발", "자기PR", "정보공유"));
        info9.put("analyzedIntroduction", "IT 취업을 준비하는 학생들의 정보 공유 모임입니다");
        info9.put("applicationMethod", "LEADER_ACCEPT");

        meeting9.put("info", info9);
        meetings.add(meeting9);

        // Meeting 10: 테니스 동아리
        Map<String, Object> meeting10 = new HashMap<>();
        meeting10.put("id", 10L);
        meeting10.put("userRole", "MEMBER");

        Map<String, Object> info10 = new HashMap<>();
        info10.put("name", "UOS 테니스");
        info10.put("type", "REGULAR");
        info10.put("title", "테니스 함께 치실 분");
        info10.put("content", "주말마다 교내 테니스장에서 테니스를 즐깁니다. 초보자도 환영합니다!");
        info10.put("thumbnailUrls", Arrays.asList("https://example.com/tennis.jpg"));
        info10.put("startDate", "2024-11-26");
        info10.put("endDate", "2024-12-26");
        info10.put("startTime", "10:00:00");
        info10.put("endTime", "12:00:00");
        info10.put("location", "교내 테니스장");
        info10.put("currentParticipant", 6);
        info10.put("minParticipant", 4);
        info10.put("maxParticipant", 8);
        info10.put("meta", "테니스 라켓 대여 가능");
        info10.put("categories", Arrays.asList("SPORTS", "HOBBY"));
        info10.put("features", Arrays.asList("테니스", "운동", "친목"));
        info10.put("analyzedFeatures", Arrays.asList("체력단련", "스포츠", "사교성"));
        info10.put("analyzedIntroduction", "테니스를 통해 건강과 친목을 동시에 챙기는 동아리입니다");
        info10.put("applicationMethod", "AUTO");

        meeting10.put("info", info10);
        meetings.add(meeting10);

        // generateTestMeetings() 메소드 내부에 추가할 모임 데이터 계속

        // Meeting 11: 사진 동아리
        Map<String, Object> meeting11 = new HashMap<>();
        meeting11.put("id", 11L);
        meeting11.put("userRole", "MEMBER");

        Map<String, Object> info11 = new HashMap<>();
        info11.put("name", "UOS 포토");
        info11.put("type", "REGULAR");
        info11.put("title", "일상을 담는 사진동아리");
        info11.put("content", "매주 사진 촬영 및 편집 기술을 공유하고 작품 리뷰를 진행합니다. 초보자도 환영합니다!");
        info11.put("thumbnailUrls", Arrays.asList("https://example.com/photo.jpg"));
        info11.put("startDate", "2024-11-27");
        info11.put("endDate", "2024-12-27");
        info11.put("startTime", "15:00:00");
        info11.put("endTime", "17:00:00");
        info11.put("location", "예술관 사진실");
        info11.put("currentParticipant", 7);
        info11.put("minParticipant", 5);
        info11.put("maxParticipant", 10);
        info11.put("meta", "카메라 보유 필수");
        info11.put("categories", Arrays.asList("ART", "HOBBY"));
        info11.put("features", Arrays.asList("사진", "예술", "창작"));
        info11.put("analyzedFeatures", Arrays.asList("창의성", "예술성", "기술"));
        info11.put("analyzedIntroduction", "사진으로 일상의 아름다움을 담아내는 예술 동아리입니다");
        info11.put("applicationMethod", "AUTO");

        meeting11.put("info", info11);
        meetings.add(meeting11);

        // Meeting 12: 프로그래밍 프로젝트
        Map<String, Object> meeting12 = new HashMap<>();
        meeting12.put("id", 12L);
        meeting12.put("userRole", "LEADER");

        Map<String, Object> info12 = new HashMap<>();
        info12.put("name", "코딩 프로젝트");
        info12.put("type", "REGULAR");
        info12.put("title", "웹/앱 개발 프로젝트 팀원 모집");
        info12.put("content", "실제 서비스를 기획부터 개발, 배포까지 경험해보실 분들을 모집합니다. 백엔드/프론트엔드 개발자 모두 환영!");
        info12.put("thumbnailUrls", Arrays.asList("https://example.com/coding.jpg"));
        info12.put("startDate", "2024-11-28");
        info12.put("endDate", "2024-12-28");
        info12.put("startTime", "19:00:00");
        info12.put("endTime", "21:00:00");
        info12.put("location", "창공관 프로젝트실");
        info12.put("currentParticipant", 4);
        info12.put("minParticipant", 4);
        info12.put("maxParticipant", 6);
        info12.put("meta", "개발 경험자 우대");
        info12.put("categories", Arrays.asList("PROJECT", "STUDY"));
        info12.put("features", Arrays.asList("개발", "프로젝트", "협업"));
        info12.put("analyzedFeatures", Arrays.asList("기술력", "팀워크", "실무경험"));
        info12.put("analyzedIntroduction", "실제 서비스 개발을 통해 실무 경험을 쌓는 프로젝트 모임입니다");
        info12.put("applicationMethod", "LEADER_ACCEPT");

        meeting12.put("info", info12);
        meetings.add(meeting12);

        // Meeting 13: 토익 스터디
        Map<String, Object> meeting13 = new HashMap<>();
        meeting13.put("id", 13L);
        meeting13.put("userRole", "MEMBER");

        Map<String, Object> info13 = new HashMap<>();
        info13.put("name", "토익 900+ 스터디");
        info13.put("type", "REGULAR");
        info13.put("title", "토익 고득점을 위한 스터디원 모집");
        info13.put("content", "매주 LC/RC 파트별 학습과 실전 모의고사를 진행합니다. 목표 점수 900점 이상!");
        info13.put("thumbnailUrls", Arrays.asList("https://example.com/toeic.jpg"));
        info13.put("startDate", "2024-11-29");
        info13.put("endDate", "2024-12-29");
        info13.put("startTime", "16:00:00");
        info13.put("endTime", "18:00:00");
        info13.put("location", "중앙도서관 스터디룸");
        info13.put("currentParticipant", 5);
        info13.put("minParticipant", 4);
        info13.put("maxParticipant", 6);
        info13.put("meta", "현재 토익 점수 750점 이상");
        info13.put("categories", Arrays.asList("LANGUAGE", "STUDY"));
        info13.put("features", Arrays.asList("토익", "영어", "시험준비"));
        info13.put("analyzedFeatures", Arrays.asList("어학능력", "목표지향", "자기계발"));
        info13.put("analyzedIntroduction", "체계적인 학습으로 토익 고득점을 달성하는 스터디입니다");
        info13.put("applicationMethod", "LEADER_ACCEPT");

        meeting13.put("info", info13);
        meetings.add(meeting13);

        // Meeting 14: 디자인 스터디
        Map<String, Object> meeting14 = new HashMap<>();
        meeting14.put("id", 14L);
        meeting14.put("userRole", "MEMBER");

        Map<String, Object> info14 = new HashMap<>();
        info14.put("name", "UX/UI 디자인 스터디");
        info14.put("type", "REGULAR");
        info14.put("title", "실무 디자인 포트폴리오 준비");
        info14.put("content", "매주 디자인 트렌드를 공부하고 포트폴리오 작업물을 피드백합니다. 피그마와 포토샵 실습도 진행합니다.");
        info14.put("thumbnailUrls", Arrays.asList("https://example.com/design.jpg"));
        info14.put("startDate", "2024-11-28");
        info14.put("endDate", "2024-12-28");
        info14.put("startTime", "16:00:00");
        info14.put("endTime", "18:00:00");
        info14.put("location", "예술관 디자인실");
        info14.put("currentParticipant", 6);
        info14.put("minParticipant", 4);
        info14.put("maxParticipant", 8);
        info14.put("meta", "디자인 툴 사용 가능자");
        info14.put("categories", Arrays.asList("DESIGN", "STUDY"));
        info14.put("features", Arrays.asList("디자인", "포트폴리오", "실무"));
        info14.put("analyzedFeatures", Arrays.asList("창의성", "실무역량", "협업"));
        info14.put("analyzedIntroduction", "실무 중심의 디자인 포트폴리오를 준비하는 스터디입니다");
        info14.put("applicationMethod", "LEADER_ACCEPT");

        meeting14.put("info", info14);
        meetings.add(meeting14);

        // Meeting 15: 보드게임 모임
        Map<String, Object> meeting15 = new HashMap<>();
        meeting15.put("id", 15L);
        meeting15.put("userRole", "MEMBER");

        Map<String, Object> info15 = new HashMap<>();
        info15.put("name", "보드게임 친구들");
        info15.put("type", "REGULAR");
        info15.put("title", "주말 보드게임 모임");
        info15.put("content", "다양한 보드게임을 즐기며 새로운 친구들과 함께 즐거운 시간을 보내요!");
        info15.put("thumbnailUrls", Arrays.asList("https://example.com/boardgame.jpg"));
        info15.put("startDate", "2024-11-30");
        info15.put("endDate", "2024-12-30");
        info15.put("startTime", "14:00:00");
        info15.put("endTime", "18:00:00");
        info15.put("location", "학생회관 동아리방");
        info15.put("currentParticipant", 8);
        info15.put("minParticipant", 4);
        info15.put("maxParticipant", 12);
        info15.put("meta", "간식 비용 있음");
        info15.put("categories", Arrays.asList("HOBBY", "SOCIAL"));
        info15.put("features", Arrays.asList("보드게임", "친목", "여가"));
        info15.put("analyzedFeatures", Arrays.asList("사교성", "전략적사고", "즐거움"));
        info15.put("analyzedIntroduction", "보드게임을 통해 새로운 친구들과 즐거운 시간을 보내는 모임입니다");
        info15.put("applicationMethod", "AUTO");

        meeting15.put("info", info15);
        meetings.add(meeting15);

        // Meeting 16: 창업 동아리
        Map<String, Object> meeting16 = new HashMap<>();
        meeting16.put("id", 16L);
        meeting16.put("userRole", "LEADER");

        Map<String, Object> info16 = new HashMap<>();
        info16.put("name", "스타트업 챌린지");
        info16.put("type", "REGULAR");
        info16.put("title", "아이디어로 창업하기");
        info16.put("content", "창업 아이디어를 공유하고 비즈니스 모델을 발전시켜 실제 창업까지 도전해봐요.");
        info16.put("thumbnailUrls", Arrays.asList("https://example.com/startup.jpg"));
        info16.put("startDate", "2024-12-01");
        info16.put("endDate", "2025-02-28");
        info16.put("startTime", "19:00:00");
        info16.put("endTime", "21:00:00");
        info16.put("location", "경영관 세미나실");
        info16.put("currentParticipant", 10);
        info16.put("minParticipant", 5);
        info16.put("maxParticipant", 15);
        info16.put("meta", "창업 경진대회 참가 예정");
        info16.put("categories", Arrays.asList("STARTUP", "PROJECT"));
        info16.put("features", Arrays.asList("창업", "비즈니스", "혁신"));
        info16.put("analyzedFeatures", Arrays.asList("기업가정신", "리더십", "창의성"));
        info16.put("analyzedIntroduction", "혁신적인 아이디어로 실제 창업에 도전하는 프로젝트 모임입니다");
        info16.put("applicationMethod", "LEADER_ACCEPT");

        meeting16.put("info", info16);
        meetings.add(meeting16);

        // Meeting 17: 환경 동아리
        Map<String, Object> meeting17 = new HashMap<>();
        meeting17.put("id", 17L);
        meeting17.put("userRole", "MEMBER");

        Map<String, Object> info17 = new HashMap<>();
        info17.put("name", "그린 캠퍼스");
        info17.put("type", "REGULAR");
        info17.put("title", "캠퍼스 환경보호 프로젝트");
        info17.put("content", "교내 환경 개선 활동과 환경 캠페인을 진행합니다. 매월 플로깅과 재활용 캠페인도 실시합니다.");
        info17.put("thumbnailUrls", Arrays.asList("https://example.com/green.jpg"));
        info17.put("startDate", "2024-12-02");
        info17.put("endDate", "2025-02-28");
        info17.put("startTime", "15:00:00");
        info17.put("endTime", "17:00:00");
        info17.put("location", "학생회관");
        info17.put("currentParticipant", 12);
        info17.put("minParticipant", 5);
        info17.put("maxParticipant", 20);
        info17.put("meta", "활동 인증서 발급");
        info17.put("categories", Arrays.asList("VOLUNTEER", "ENVIRONMENT"));
        info17.put("features", Arrays.asList("환경보호", "봉사활동", "캠페인"));
        info17.put("analyzedFeatures", Arrays.asList("환경의식", "사회공헌", "실천력"));
        info17.put("analyzedIntroduction", "환경 보호를 실천하는 캠퍼스 환경 개선 동아리입니다");
        info17.put("applicationMethod", "AUTO");

        meeting17.put("info", info17);
        meetings.add(meeting17);

        // Meeting 18: 밴드 동아리
        Map<String, Object> meeting18 = new HashMap<>();
        meeting18.put("id", 18L);
        meeting18.put("userRole", "MEMBER");

        Map<String, Object> info18 = new HashMap<>();
        info18.put("name", "UOS 밴드");
        info18.put("type", "REGULAR");
        info18.put("title", "락밴드 멤버 모집");
        info18.put("content", "정기 공연과 작은 콘서트를 준비합니다. 보컬, 기타, 베이스, 드럼 등 다양한 포지션 모집 중!");
        info18.put("thumbnailUrls", Arrays.asList("https://example.com/band.jpg"));
        info18.put("startDate", "2024-12-03");
        info18.put("endDate", "2025-02-28");
        info18.put("startTime", "18:00:00");
        info18.put("endTime", "21:00:00");
        info18.put("location", "예술관 음악실");
        info18.put("currentParticipant", 4);
        info18.put("minParticipant", 4);
        info18.put("maxParticipant", 8);
        info18.put("meta", "악기 연주 가능자");
        info18.put("categories", Arrays.asList("MUSIC", "PERFORMANCE"));
        info18.put("features", Arrays.asList("밴드", "공연", "음악"));
        info18.put("analyzedFeatures", Arrays.asList("예술성", "팀워크", "열정"));
        info18.put("analyzedIntroduction", "음악으로 하나되는 열정적인 밴드 동아리입니다");
        info18.put("applicationMethod", "LEADER_ACCEPT");

        meeting18.put("info", info18);
        meetings.add(meeting18);

        // Meeting 19: 데이터 분석 스터디
        Map<String, Object> meeting19 = new HashMap<>();
        meeting19.put("id", 19L);
        meeting19.put("userRole", "LEADER");

        Map<String, Object> info19 = new HashMap<>();
        info19.put("name", "데이터 사이언스 스터디");
        info19.put("type", "REGULAR");
        info19.put("title", "실무 데이터 분석 스터디");
        info19.put("content", "파이썬을 활용한 데이터 분석과 머신러닝 프로젝트를 진행합니다. 캐글 대회 참가도 준비해요.");
        info19.put("thumbnailUrls", Arrays.asList("https://example.com/data.jpg"));
        info19.put("startDate", "2024-12-04");
        info19.put("endDate", "2025-02-28");
        info19.put("startTime", "19:00:00");
        info19.put("endTime", "21:00:00");
        info19.put("location", "미래관 컴퓨터실");
        info19.put("currentParticipant", 6);
        info19.put("minParticipant", 4);
        info19.put("maxParticipant", 10);
        info19.put("meta", "파이썬 기초 필수");
        info19.put("categories", Arrays.asList("STUDY", "RESEARCH"));
        info19.put("features", Arrays.asList("데이터분석", "머신러닝", "프로그래밍"));
        info19.put("analyzedFeatures", Arrays.asList("분석력", "기술력", "문제해결"));
        info19.put("analyzedIntroduction", "실무 데이터 분석 역량을 키우는 전문 스터디입니다");
        info19.put("applicationMethod", "LEADER_ACCEPT");

        meeting19.put("info", info19);
        meetings.add(meeting19);

        // Meeting 20: 글쓰기 모임
        Map<String, Object> meeting20 = new HashMap<>();
        meeting20.put("id", 20L);
        meeting20.put("userRole", "MEMBER");

        Map<String, Object> info20 = new HashMap<>();
        info20.put("name", "글담");
        info20.put("type", "REGULAR");
        info20.put("title", "함께 쓰는 이야기");
        info20.put("content", "매주 다양한 주제로 글을 쓰고 서로의 작품을 피드백합니다. 시, 소설, 에세이 등 장르 무관!");
        info20.put("thumbnailUrls", Arrays.asList("https://example.com/writing.jpg"));
        info20.put("startDate", "2024-12-05");
        info20.put("endDate", "2025-02-28");
        info20.put("startTime", "15:00:00");
        info20.put("endTime", "17:00:00");
        info20.put("location", "중앙도서관 스터디룸");
        info20.put("currentParticipant", 5);
        info20.put("minParticipant", 3);
        info20.put("maxParticipant", 8);
        info20.put("meta", "필기구 지참");
        info20.put("categories", Arrays.asList("CULTURE", "HOBBY"));
        info20.put("features", Arrays.asList("글쓰기", "창작", "피드백"));
        info20.put("analyzedFeatures", Arrays.asList("창의성", "표현력", "소통능력"));
        info20.put("analyzedIntroduction", "창작의 즐거움을 나누는 글쓰기 모임입니다");
        info20.put("applicationMethod", "AUTO");

        meeting20.put("info", info20);
        meetings.add(meeting20);

        // Meeting 21: 토론 동아리
        Map<String, Object> meeting21 = new HashMap<>();
        meeting21.put("id", 21L);
        meeting21.put("userRole", "MEMBER");

        Map<String, Object> info21 = new HashMap<>();
        info21.put("name", "시대토론회");
        info21.put("type", "REGULAR");
        info21.put("title", "사회 이슈 토론 모임");
        info21.put("content", "매주 사회적 이슈에 대해 토론하고 다양한 관점을 공유합니다. 시사, 문화, 과학 등 폭넓은 주제로 진행됩니다.");
        info21.put("thumbnailUrls", Arrays.asList("https://example.com/debate.jpg"));
        info21.put("startDate", "2024-12-06");
        info21.put("endDate", "2025-02-28");
        info21.put("startTime", "17:00:00");
        info21.put("endTime", "19:00:00");
        info21.put("location", "인문관 세미나실");
        info21.put("currentParticipant", 8);
        info21.put("minParticipant", 6);
        info21.put("maxParticipant", 12);
        info21.put("meta", "사전 자료 읽어오기 필수");
        info21.put("categories", Arrays.asList("DISCUSSION", "SOCIAL"));
        info21.put("features", Arrays.asList("토론", "시사", "소통"));
        info21.put("analyzedFeatures", Arrays.asList("논리력", "비판적사고", "발표력"));
        info21.put("analyzedIntroduction", "다양한 시각으로 사회를 바라보는 토론 모임입니다");
        info21.put("applicationMethod", "LEADER_ACCEPT");

        meeting21.put("info", info21);
        meetings.add(meeting21);

        // Meeting 22: 3D 프린팅 동아리
        Map<String, Object> meeting22 = new HashMap<>();
        meeting22.put("id", 22L);
        meeting22.put("userRole", "LEADER");

        Map<String, Object> info22 = new HashMap<>();
        info22.put("name", "메이커스");
        info22.put("type", "REGULAR");
        info22.put("title", "3D 프린팅 메이커 모임");
        info22.put("content", "3D 모델링부터 프린팅까지 직접 제작합니다. 창의적인 아이디어를 실제 제품으로 만들어보세요.");
        info22.put("thumbnailUrls", Arrays.asList("https://example.com/3dprinting.jpg"));
        info22.put("startDate", "2024-12-07");
        info22.put("endDate", "2025-02-28");
        info22.put("startTime", "14:00:00");
        info22.put("endTime", "17:00:00");
        info22.put("location", "공학관 메이커스페이스");
        info22.put("currentParticipant", 5);
        info22.put("minParticipant", 3);
        info22.put("maxParticipant", 8);
        info22.put("meta", "재료비 별도");
        info22.put("categories", Arrays.asList("TECHNOLOGY", "CREATION"));
        info22.put("features", Arrays.asList("3D프린팅", "메이킹", "설계"));
        info22.put("analyzedFeatures", Arrays.asList("기술력", "창의성", "실무능력"));
        info22.put("analyzedIntroduction", "아이디어를 실제 제품으로 구현하는 메이커 모임입니다");
        info22.put("applicationMethod", "LEADER_ACCEPT");

        meeting22.put("info", info22);
        meetings.add(meeting22);

        // Meeting 23: 주식투자 스터디
        Map<String, Object> meeting23 = new HashMap<>();
        meeting23.put("id", 23L);
        meeting23.put("userRole", "MEMBER");

        Map<String, Object> info23 = new HashMap<>();
        info23.put("name", "투자연구회");
        info23.put("type", "REGULAR");
        info23.put("title", "주식투자 기초부터 실전까지");
        info23.put("content", "기업분석, 차트분석, 투자전략 등을 함께 공부합니다. 모의투자도 진행합니다.");
        info23.put("thumbnailUrls", Arrays.asList("https://example.com/investment.jpg"));
        info23.put("startDate", "2024-12-08");
        info23.put("endDate", "2025-02-28");
        info23.put("startTime", "19:00:00");
        info23.put("endTime", "21:00:00");
        info23.put("location", "경영관 스터디룸");
        info23.put("currentParticipant", 7);
        info23.put("minParticipant", 5);
        info23.put("maxParticipant", 10);
        info23.put("meta", "모의투자 계좌 개설 필요");
        info23.put("categories", Arrays.asList("FINANCE", "STUDY"));
        info23.put("features", Arrays.asList("투자", "주식", "재테크"));
        info23.put("analyzedFeatures", Arrays.asList("금융지식", "분석력", "위험관리"));
        info23.put("analyzedIntroduction", "체계적인 투자 공부를 통해 재테크 능력을 키우는 스터디입니다");
        info23.put("applicationMethod", "LEADER_ACCEPT");

        meeting23.put("info", info23);
        meetings.add(meeting23);

        // Meeting 24: 영화감상 모임
        Map<String, Object> meeting24 = new HashMap<>();
        meeting24.put("id", 24L);
        meeting24.put("userRole", "MEMBER");

        Map<String, Object> info24 = new HashMap<>();
        info24.put("name", "씨네필");
        info24.put("type", "REGULAR");
        info24.put("title", "함께 보는 세계 영화");
        info24.put("content", "매주 한 편의 영화를 선정하여 감상하고 토론합니다. 고전영화부터 최신작까지 다양한 장르를 다룹니다.");
        info24.put("thumbnailUrls", Arrays.asList("https://example.com/cinema.jpg"));
        info24.put("startDate", "2024-12-09");
        info24.put("endDate", "2025-02-28");
        info24.put("startTime", "18:00:00");
        info24.put("endTime", "21:00:00");
        info24.put("location", "예술관 시청각실");
        info24.put("currentParticipant", 10);
        info24.put("minParticipant", 5);
        info24.put("maxParticipant", 15);
        info24.put("meta", "간식비 별도");
        info24.put("categories", Arrays.asList("CULTURE", "HOBBY"));
        info24.put("features", Arrays.asList("영화", "토론", "문화예술"));
        info24.put("analyzedFeatures", Arrays.asList("문화감상", "비평능력", "소통"));
        info24.put("analyzedIntroduction", "영화를 통해 다양한 시각과 문화를 공유하는 모임입니다");
        info24.put("applicationMethod", "AUTO");

        meeting24.put("info", info24);
        meetings.add(meeting24);

        // Meeting 25: 요가 모임
        Map<String, Object> meeting25 = new HashMap<>();
        meeting25.put("id", 25L);
        meeting25.put("userRole", "MEMBER");

        Map<String, Object> info25 = new HashMap<>();
        info25.put("name", "요가 클래스");
        info25.put("type", "REGULAR");
        info25.put("title", "아침을 여는 요가");
        info25.put("content", "매일 아침 요가로 하루를 시작합니다. 초보자도 쉽게 따라할 수 있는 동작들로 구성됩니다.");
        info25.put("thumbnailUrls", Arrays.asList("https://example.com/yoga.jpg"));
        info25.put("startDate", "2024-12-10");
        info25.put("endDate", "2025-02-28");
        info25.put("startTime", "07:30:00");
        info25.put("endTime", "08:30:00");
        info25.put("location", "체육관 요가실");
        info25.put("currentParticipant", 8);
        info25.put("minParticipant", 5);
        info25.put("maxParticipant", 12);
        info25.put("meta", "요가매트 대여 가능");
        info25.put("categories", Arrays.asList("SPORTS", "WELLNESS"));
        info25.put("features", Arrays.asList("요가", "명상", "스트레칭"));
        info25.put("analyzedFeatures", Arrays.asList("건강관리", "유연성", "마음챙김"));
        info25.put("analyzedIntroduction", "요가를 통해 심신의 건강을 도모하는 아침 운동 모임입니다");
        info25.put("applicationMethod", "AUTO");

        meeting25.put("info", info25);
        meetings.add(meeting25);

        // Meeting 26: 외국어 교환 모임
        Map<String, Object> meeting26 = new HashMap<>();
        meeting26.put("id", 26L);
        meeting26.put("userRole", "MEMBER");

        Map<String, Object> info26 = new HashMap<>();
        info26.put("name", "글로벌 프렌즈");
        info26.put("type", "REGULAR");
        info26.put("title", "한국어-영어 언어교환");
        info26.put("content", "원어민과 함께하는 실전 회화 연습. 매주 다양한 주제로 대화하며 서로의 문화도 배워요.");
        info26.put("thumbnailUrls", Arrays.asList("https://example.com/language.jpg"));
        info26.put("startDate", "2024-12-11");
        info26.put("endDate", "2025-02-28");
        info26.put("startTime", "18:00:00");
        info26.put("endTime", "20:00:00");
        info26.put("location", "국제교류센터");
        info26.put("currentParticipant", 6);
        info26.put("minParticipant", 4);
        info26.put("maxParticipant", 8);
        info26.put("meta", "중급 이상 영어 실력 필요");
        info26.put("categories", Arrays.asList("LANGUAGE", "CULTURE"));
        info26.put("features", Arrays.asList("언어교환", "문화교류", "소통"));
        info26.put("analyzedFeatures", Arrays.asList("글로벌", "의사소통", "문화이해"));
        info26.put("analyzedIntroduction", "언어교환을 통해 글로벌 감각을 키우는 국제교류 모임입니다");
        info26.put("applicationMethod", "LEADER_ACCEPT");

        meeting26.put("info", info26);
        meetings.add(meeting26);

        // Meeting 27: AI 스터디
        Map<String, Object> meeting27 = new HashMap<>();
        meeting27.put("id", 27L);
        meeting27.put("userRole", "MEMBER");

        Map<String, Object> info27 = new HashMap<>();
        info27.put("name", "AI 연구회");
        info27.put("type", "REGULAR");
        info27.put("title", "인공지능 논문 리뷰");
        info27.put("content", "최신 AI 논문을 함께 읽고 토론합니다. 실제 구현도 진행하며 프로젝트도 준비해요.");
        info27.put("thumbnailUrls", Arrays.asList("https://example.com/ai.jpg"));
        info27.put("startDate", "2024-12-12");
        info27.put("endDate", "2025-02-28");
        info27.put("startTime", "19:00:00");
        info27.put("endTime", "21:00:00");
        info27.put("location", "미래관 AI랩");
        info27.put("currentParticipant", 7);
        info27.put("minParticipant", 5);
        info27.put("maxParticipant", 10);
        info27.put("meta", "Python 기초 필수");
        info27.put("categories", Arrays.asList("RESEARCH", "TECHNOLOGY"));
        info27.put("features", Arrays.asList("인공지능", "논문", "프로그래밍"));
        info27.put("analyzedFeatures", Arrays.asList("전문성", "연구능력", "기술력"));
        info27.put("analyzedIntroduction", "최신 AI 기술을 연구하고 실습하는 전문 스터디입니다");
        info27.put("applicationMethod", "LEADER_ACCEPT");

        meeting27.put("info", info27);
        meetings.add(meeting27);

        // Meeting 28: 캠퍼스 사진 동아리
        Map<String, Object> meeting28 = new HashMap<>();
        meeting28.put("id", 28L);
        meeting28.put("userRole", "MEMBER");

        Map<String, Object> info28 = new HashMap<>();
        info28.put("name", "캠퍼스 포토");
        info28.put("type", "REGULAR");
        info28.put("title", "캠퍼스 풍경 사진전 준비");
        info28.put("content", "교내 곳곳의 아름다운 풍경을 담아 전시회를 준비합니다. 사진 촬영 기법도 배워요.");
        info28.put("thumbnailUrls", Arrays.asList("https://example.com/photo.jpg"));
        info28.put("startDate", "2024-12-13");
        info28.put("endDate", "2025-02-28");
        info28.put("startTime", "16:00:00");
        info28.put("endTime", "18:00:00");
        info28.put("location", "예술관 사진실");
        info28.put("currentParticipant", 8);
        info28.put("minParticipant", 5);
        info28.put("maxParticipant", 12);
        info28.put("meta", "카메라 보유 필수");
        info28.put("categories", Arrays.asList("ART", "HOBBY"));
        info28.put("features", Arrays.asList("사진", "전시", "예술"));
        info28.put("analyzedFeatures", Arrays.asList("예술성", "창의력", "표현력"));
        info28.put("analyzedIntroduction", "캠퍼스의 아름다움을 사진으로 담아내는 예술 동아리입니다");
        info28.put("applicationMethod", "AUTO");

        meeting28.put("info", info28);
        meetings.add(meeting28);

        // Meeting 29: 독서토론 모임
        Map<String, Object> meeting29 = new HashMap<>();
        meeting29.put("id", 29L);
        meeting29.put("userRole", "MEMBER");

        Map<String, Object> info29 = new HashMap<>();
        info29.put("name", "책과 사람");
        info29.put("type", "REGULAR");
        info29.put("title", "인문학 독서토론");
        info29.put("content", "매달 한 권의 인문학 도서를 선정하여 깊이 있는 토론을 진행합니다. 철학, 역사, 문학 등 다양한 분야의 책을 함께 읽어요.");
        info29.put("thumbnailUrls", Arrays.asList("https://example.com/book.jpg"));
        info29.put("startDate", "2024-12-14");
        info29.put("endDate", "2025-02-28");
        info29.put("startTime", "14:00:00");
        info29.put("endTime", "16:00:00");
        info29.put("location", "중앙도서관 세미나실");
        info29.put("currentParticipant", 6);
        info29.put("minParticipant", 4);
        info29.put("maxParticipant", 8);
        info29.put("meta", "선정도서 구매 필요");
        info29.put("categories", Arrays.asList("CULTURE", "STUDY"));
        info29.put("features", Arrays.asList("독서", "토론", "인문학"));
        info29.put("analyzedFeatures", Arrays.asList("지적성장", "비판적사고", "소통능력"));
        info29.put("analyzedIntroduction", "인문학 도서를 통해 깊이 있는 사고를 나누는 독서토론 모임입니다");
        info29.put("applicationMethod", "LEADER_ACCEPT");

        meeting29.put("info", info29);
        meetings.add(meeting29);

        // Meeting 30: 방송댄스 동아리
        Map<String, Object> meeting30 = new HashMap<>();
        meeting30.put("id", 30L);
        meeting30.put("userRole", "MEMBER");

        Map<String, Object> info30 = new HashMap<>();
        info30.put("name", "댄스메이트");
        info30.put("type", "REGULAR");
        info30.put("title", "K-pop 커버댄스");
        info30.put("content", "인기 K-pop 곡의 안무를 배우고 연습합니다. 학기말에는 공연도 준비해요. 초보자도 환영!");
        info30.put("thumbnailUrls", Arrays.asList("https://example.com/dance.jpg"));
        info30.put("startDate", "2024-12-15");
        info30.put("endDate", "2025-02-28");
        info30.put("startTime", "18:00:00");
        info30.put("endTime", "20:00:00");
        info30.put("location", "체육관 댄스실");
        info30.put("currentParticipant", 12);
        info30.put("minParticipant", 8);
        info30.put("maxParticipant", 20);
        info30.put("meta", "운동복, 실내운동화 필수");
        info30.put("categories", Arrays.asList("DANCE", "PERFORMANCE"));
        info30.put("features", Arrays.asList("댄스", "공연", "운동"));
        info30.put("analyzedFeatures", Arrays.asList("예술성", "체력", "자신감"));
        info30.put("analyzedIntroduction", "K-pop 댄스를 통해 즐거움과 자신감을 키우는 댄스 동아리입니다");
        info30.put("applicationMethod", "AUTO");

        meeting30.put("info", info30);
        meetings.add(meeting30);

        return meetings;
    }

    private static List<Map<String, Object>> generateTestUsers() {
        List<Map<String, Object>> users = new ArrayList<>();

        // User 1
        Map<String, Object> user1 = new HashMap<>();
        user1.put("id", "user-uuid-1");
        user1.put("username", "김시립");
        user1.put("major", "COMPUTER_SCIENCE");
        user1.put("studentId", "20210001");
        user1.put("attendanceScore", 95);
        user1.put("features", Arrays.asList("적극적", "리더십", "책임감"));

        List<Map<String, Object>> meetings1 = new ArrayList<>();
        Map<String, Object> userMeeting1 = new HashMap<>();
        userMeeting1.put("meetingId", "meeting-uuid-1");
        userMeeting1.put("features", Arrays.asList("알고리즘", "코딩", "팀워크"));
        meetings1.add(userMeeting1);

        user1.put("meetings", meetings1);
        users.add(user1);

        // 나머지 9명의 사용자 데이터도 비슷한 형식으로 추가
        // ...

        Map<String, Object> user2 = new HashMap<>();
        user2.put("id", "user-uuid-2");
        user2.put("username", "이문학");
        user2.put("major", "ENGLISH_LITERATURE");
        user2.put("studentId", "20210002");
        user2.put("attendanceScore", 88);
        user2.put("features", Arrays.asList("창의적", "소통능력", "문학적감각"));

        List<Map<String, Object>> meetings2 = new ArrayList<>();
        Map<String, Object> userMeeting2 = new HashMap<>();
        userMeeting2.put("meetingId", "meeting-uuid-2");
        userMeeting2.put("features", Arrays.asList("독서", "토론", "글쓰기"));
        meetings2.add(userMeeting2);

        user2.put("meetings", meetings2);
        users.add(user2);

        // User 3: 체육학도
        Map<String, Object> user3 = new HashMap<>();
        user3.put("id", "user-uuid-3");
        user3.put("username", "김체육");
        user3.put("major", "PHYSICAL_EDUCATION");
        user3.put("studentId", "20210003");
        user3.put("attendanceScore", 92);
        user3.put("features", Arrays.asList("활동적", "리더십", "체력우수"));

        List<Map<String, Object>> meetings3 = new ArrayList<>();
        Map<String, Object> userMeeting3 = new HashMap<>();
        userMeeting3.put("meetingId", "meeting-uuid-3");
        userMeeting3.put("features", Arrays.asList("운동", "팀워크", "건강"));
        meetings3.add(userMeeting3);

        user3.put("meetings", meetings3);
        users.add(user3);

        // User 4: 경영학도
        Map<String, Object> user4 = new HashMap<>();
        user4.put("id", "user-uuid-4");
        user4.put("username", "최경영");
        user4.put("major", "BUSINESS");
        user4.put("studentId", "20210004");
        user4.put("attendanceScore", 85);
        user4.put("features", Arrays.asList("리더십", "분석적", "기획력"));

        List<Map<String, Object>> meetings4 = new ArrayList<>();
        Map<String, Object> userMeeting4 = new HashMap<>();
        userMeeting4.put("meetingId", "meeting-uuid-4");
        userMeeting4.put("features", Arrays.asList("마케팅", "기획", "프레젠테이션"));
        meetings4.add(userMeeting4);

        user4.put("meetings", meetings4);
        users.add(user4);

        // User 5: 예술학도
        Map<String, Object> user5 = new HashMap<>();
        user5.put("id", "user-uuid-5");
        user5.put("username", "박예술");
        user5.put("major", "FINE_ARTS");
        user5.put("studentId", "20210005");
        user5.put("attendanceScore", 92);
        user5.put("features", Arrays.asList("창의적", "감성적", "표현력"));

        List<Map<String, Object>> meetings5 = new ArrayList<>();
        Map<String, Object> userMeeting5 = new HashMap<>();
        userMeeting5.put("meetingId", "meeting-uuid-5");
        userMeeting5.put("features", Arrays.asList("디자인", "창작", "예술"));
        meetings5.add(userMeeting5);

        user5.put("meetings", meetings5);
        users.add(user5);

        // User 6: 음악학도
        Map<String, Object> user6 = new HashMap<>();
        user6.put("id", "user-uuid-6");
        user6.put("username", "정음악");
        user6.put("major", "MUSIC");
        user6.put("studentId", "20210006");
        user6.put("attendanceScore", 87);
        user6.put("features", Arrays.asList("예술적", "감성적", "창의력"));

        List<Map<String, Object>> meetings6 = new ArrayList<>();
        Map<String, Object> userMeeting6 = new HashMap<>();
        userMeeting6.put("meetingId", "meeting-uuid-6");
        userMeeting6.put("features", Arrays.asList("음악", "공연", "예술"));
        meetings6.add(userMeeting6);

        user6.put("meetings", meetings6);
        users.add(user6);

        // User 7: 건축학도
        Map<String, Object> user7 = new HashMap<>();
        user7.put("id", "user-uuid-7");
        user7.put("username", "김건축");
        user7.put("major", "ARCHITECTURE");
        user7.put("studentId", "20210007");
        user7.put("attendanceScore", 93);
        user7.put("features", Arrays.asList("분석적", "창의적", "책임감"));

        List<Map<String, Object>> meetings7 = new ArrayList<>();
        Map<String, Object> userMeeting7 = new HashMap<>();
        userMeeting7.put("meetingId", "meeting-uuid-7");
        userMeeting7.put("features", Arrays.asList("설계", "디자인", "협업"));
        meetings7.add(userMeeting7);

        user7.put("meetings", meetings7);
        users.add(user7);

        // User 8: 화학과 학생
        Map<String, Object> user8 = new HashMap<>();
        user8.put("id", "user-uuid-8");
        user8.put("username", "이화학");
        user8.put("major", "CHEMISTRY");
        user8.put("studentId", "20210008");
        user8.put("attendanceScore", 88);
        user8.put("features", Arrays.asList("실험적", "논리적", "꼼꼼함"));

        List<Map<String, Object>> meetings8 = new ArrayList<>();
        Map<String, Object> userMeeting8 = new HashMap<>();
        userMeeting8.put("meetingId", "meeting-uuid-8");
        userMeeting8.put("features", Arrays.asList("연구", "실험", "분석"));
        meetings8.add(userMeeting8);

        user8.put("meetings", meetings8);
        users.add(user8);

        // User 9: 체육학과 학생
        Map<String, Object> user9 = new HashMap<>();
        user9.put("id", "user-uuid-9");
        user9.put("username", "박체육");
        user9.put("major", "PHYSICAL_EDUCATION");
        user9.put("studentId", "20210009");
        user9.put("attendanceScore", 95);
        user9.put("features", Arrays.asList("활동적", "리더십", "체력우수"));

        List<Map<String, Object>> meetings9 = new ArrayList<>();
        Map<String, Object> userMeeting9 = new HashMap<>();
        userMeeting9.put("meetingId", "meeting-uuid-9");
        userMeeting9.put("features", Arrays.asList("스포츠", "코칭", "팀워크"));
        meetings9.add(userMeeting9);

        user9.put("meetings", meetings9);
        users.add(user9);

        // User 10: 경영학과 학생
        Map<String, Object> user10 = new HashMap<>();
        user10.put("id", "user-uuid-10");
        user10.put("username", "김경영");
        user10.put("major", "BUSINESS");
        user10.put("studentId", "20210010");
        user10.put("attendanceScore", 85);
        user10.put("features", Arrays.asList("분석적", "기획력", "리더십"));

        List<Map<String, Object>> meetings10 = new ArrayList<>();
        Map<String, Object> userMeeting10 = new HashMap<>();
        userMeeting10.put("meetingId", "meeting-uuid-10");
        userMeeting10.put("features", Arrays.asList("마케팅", "기획", "프레젠테이션"));
        meetings10.add(userMeeting10);

        user10.put("meetings", meetings10);
        users.add(user10);

        return users;
    }

    private static String formatMeetingInfo(Map<String, Object> meeting) {
        Map<String, Object> info = (Map<String, Object>) meeting.get("info");
        return String.format("""
                === 모임 ID: %d ===
                모임명: %s
                모임 유형: %s
                제목: %s
                내용: %s
                시작일: %s
                종료일: %s
                시작 시간: %s
                종료 시간: %s
                장소: %s
                현재 참여자 수: %d
                최소 참여자 수: %d
                최대 참여자 수: %d
                추가 정보: %s
                카테고리: %s
                특징: %s
                분석된 특징: %s
                모임 소개: %s
                신청 방식: %s
                좋아요 수: %d
                댓글 수: %d
                스크랩 수: %d
                """,
                meeting.get("id"),
                info.get("name"),
                info.get("type"),
                info.get("title"),
                info.get("content"),
                info.get("startDate"),
                info.get("endDate"),
                info.get("startTime"),
                info.get("endTime"),
                info.get("location"),
                info.get("currentParticipant"),
                info.get("minParticipant"),
                info.get("maxParticipant"),
                info.get("meta"),
                String.join(", ", (List<String>) info.get("categories")),
                String.join(", ", (List<String>) info.get("features")),
                String.join(", ", (List<String>) info.get("analyzedFeatures")),
                info.get("analyzedIntroduction"),
                info.get("applicationMethod"),
                info.getOrDefault("likes", 0),
                info.getOrDefault("comments", 0),
                info.getOrDefault("scraps", 0));
    }

    private static String formatMeetingsList(List<Map<String, Object>> meetings) {
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> meeting : meetings) {
            sb.append(formatMeetingInfo(meeting)).append("\n\n");
        }
        return sb.toString();
    }

    private static List<MeetingRecommendation> recommendMeetings(Map<String, Object> user,
            List<Map<String, Object>> meetings) {
        String prompt = String.format(RECOMMENDATION_PROMPT,
                user.get("username"),
                user.get("major"),
                user.get("studentId"),
                user.get("attendanceScore"),
                user.get("personalTraits"),
                user.get("meetingTraits"),
                formatMeetingsList(meetings));

        String response = model.generate(prompt);
        return parseRecommendations(response); // parseMeetingRecommendations 대신 parseRecommendations 사용
    }

    private static List<MeetingRecommendation> parseRecommendations(String response) {
        List<MeetingRecommendation> recommendations = new ArrayList<>();
        String[] lines = response.split("\n");

        for (String line : lines) {
            if (line.matches("\\d+\\..*")) {
                try {
                    String[] parts = line.substring(line.indexOf(".") + 1).split(":");
                    if (parts.length >= 2) {
                        // ID 추출 시 대괄호와 공백 제거 후 파싱
                        String idStr = parts[0].trim()
                                .replaceAll("[\\[\\]\\s]", "")
                                .replaceAll("[^0-9]", "");

                        if (!idStr.isEmpty()) {
                            Long meetingId = Long.parseLong(idStr);
                            String reason = parts[1].trim();
                            recommendations.add(new MeetingRecommendation(meetingId, reason));
                        }
                    }
                } catch (NumberFormatException e) {
                    // 파싱 실패 시 로그 출력하고 계속 진행
                    System.err.println("ID 파싱 실패: " + line);
                    continue;
                }
            }
        }
        return recommendations;
    }

    @Getter
    @AllArgsConstructor
    static class MeetingRecommendation {
        private final Long meetingId;
        private final String reason;

        @Override
        public String toString() {
            return String.format("모임 ID: %d - %s", meetingId, reason);
        }
    }
}