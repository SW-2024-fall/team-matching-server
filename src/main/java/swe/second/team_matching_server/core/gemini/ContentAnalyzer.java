package swe.second.team_matching_server.core.gemini;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import dev.langchain4j.model.chat.ChatLanguageModel;

import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.meeting.model.enums.*;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.common.enums.ResultCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentAnalyzer {
    
    private final ChatLanguageModel model;

    private static final String FEATURE_PROMPT = """
            당신은 서울시립대학교 학생들의 모임 게시글의 제목과 내용에서 주요 토픽을 추출하는 전문가입니다.
            당신이 추출하는 토픽은 학생들이 모임 게시글의 특징을 잘 파악할 수 있도록 해야 됩니다.
            모임 게시글의 특징은 문장이 아닌 하나의 단어로 구성되어야 합니다.

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
            
            분석된 모임 특징을 다음 형식으로 제공해주세요:
            1.
            2.
            3.""";

    private static final String SUMMARY_PROMPT = """
            당신은 서울시립대학교 학생들의 모임을 한 문장으로 요약하는 전문가입니다.
            당신이 요약하는 문장은 학생들이 모임 게시글의 특징을 잘 파악할 수 있도록 해야 됩니다.
            다음 모임 정보를 읽고, 해당 모임에 잘 맞을 것 같은 이에게 모임을 추천하는 간결한 한 문장을 작성해주세요.
            추천할 때 모임 상세 정보를 포함할 필요는 없습니다.
            
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
            다음 학생 정보를 바탕으로 가장 중요한 3개의 특징을 추출해주세요:
            학생의 특징은 문장이 아닌 단어만 제시해야합니다.
            1. 성격
            2. 신뢰성
            3. 관심분야
            
            이름: %s
            전공: %s
            출석 점수(100점에 가까울수록 성실): %d
            기존 특징: %s
            선호 카테고리: %s
            학번: %s
            
            분석된 사용자 특징을 다음 형식으로 제공해주세요:
            1.
            2.
            3.""";

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
            새로운 활동 기록을 바탕으로 기존 모임의 소개가 달라져야하는 경우, 모임 소개를 업데이트해주세요.
            
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
            모든 단어가 꼭 업데이트될 필요는 없으며, 필요한 단어만 업데이트해주세요.
            당신이 분석한 특징은 학생들의 특성을 잘 파악할 수 있도록 해야 됩니다.
            다음 학생의 정보를 바탕으로 가장 중요한 3개의 특징을 업데이트해주세요:
            학생의 특징은 문장이 아닌 단어로 구성되어야 합니다.

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


    private List<String> analyzeFeatures(Meeting meeting) throws Exception {
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

    private String generateSummary(Meeting meeting) throws Exception {
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

    private List<String> analyzeUser(User user) throws Exception {
        String user_prompt = String.format(MEMBER_PROMPT,
                user.getUsername(),
                user.getMajor(),
                user.getAttendanceScore(),
                String.join(", ", user.getFeatures()),
                String.join(", ", user.getPreferredCategories().stream()
                        .map(MeetingCategory::toString)
                        .collect(Collectors.toList())),
                user.getStudentId());

        String user_response = model.generate(user_prompt);

        return parseFeatures(user_response);
    }

    private List<String> updateMeetingFeatures(History history, List<String> currentFeatures) throws Exception {
        String update_meeting_prompt = String.format(UPDATE_MEETING_FEATURES_PROMPT,
                String.join("\n", currentFeatures),
                history.getTitle(),
                history.getContent(),
                history.getLocation(),
                history.getDate().toString());

        String update_meeting_response = model.generate(update_meeting_prompt);
        return parseFeatures(update_meeting_response);
    }

    private String updateMeetingIntroduction(History history, String currentIntro) throws Exception {
        String update_meeting_introduction_prompt = String.format(UPDATE_MEETING_INTRO_PROMPT,
                currentIntro,
                history.getTitle(),
                history.getContent(),
                history.getLocation(),
                history.getDate().toString());

        String update_meeting_introduction_response = model.generate(update_meeting_introduction_prompt);
        return update_meeting_introduction_response.trim();
    }

    private List<String> updateUserFeatures(History history, List<String> currentFeatures) throws Exception {
        String update_user_feature_prompt = String.format(UPDATE_USER_FEATURES_PROMPT,
                String.join("\n", currentFeatures),
                history.getTitle(),
                history.getContent(),
                history.getLocation(),
                history.getDate().toString());

        String update_user_feature_response = model.generate(update_user_feature_prompt);
        return parseFeatures(update_user_feature_response);
    }

    private String formatCategories(List<MeetingCategory> categories) {
        return categories.stream()
                .map(MeetingCategory::toString)
                .collect(Collectors.joining(", "));
    }

    private List<String> parseFeatures(String response) {
        List<String> features = new ArrayList<>();
        String[] lines = response.split("\n");
        for (String line : lines) {
            if (line.matches("\\d+\\..*")) {
                features.add(line.substring(line.indexOf(".") + 1).trim());
            }
        }
        return features;
    }

    public Meeting analyzeMeetingComplete(Meeting meeting) {
        try {
            List<String> characteristics = analyzeFeatures(meeting);
            meeting.updateAnalyzedFeatures(characteristics);
            String summary = generateSummary(meeting);
            meeting.updateAnalyzedIntroduction(summary);

            log.info("meeting features: " + meeting.getAnalyzedFeatures());
            log.info("meeting summary: " + meeting.getAnalyzedIntroduction());
            
            return meeting;
        } catch (Exception e) {
            throw new RuntimeException(ResultCode.MEETING_ANALYSIS_ERROR.getMessage(), e);
        }
    }

    public User analyzeUserComplete(User user) {
        try {
            List<String> characteristics = analyzeUser(user);
            user.updateFeatures(characteristics);
            log.info("user features: " + user.getFeatures());
        
            return user;
        } catch (Exception e) {
            throw new RuntimeException(ResultCode.MEMBER_ANALYSIS_ERROR.getMessage(), e);
        }
    }

    public Meeting updateMeetingFromHistory(History history, Meeting meeting) {
        try {
            // 모임 특징 업데이트
            List<String> updatedMeetingFeatures =
                    updateMeetingFeatures(history, meeting.getFeatures());
            meeting.updateAnalyzedFeatures(updatedMeetingFeatures);

            // 모임 소개 업데이트
            String updatedMeetingIntro =
                    updateMeetingIntroduction(history, meeting.getAnalyzedIntroduction());
            meeting.updateAnalyzedIntroduction(updatedMeetingIntro);

            return meeting;
        } catch (Exception e) {
            throw new RuntimeException(ResultCode.HISTORY_ANALYSIS_ERROR.getMessage(), e);
        }
    }
    
    public User updateUserFromHistory(History history, User user) {
        try {
            List<String> updatedUserFeatures = updateUserFeatures(history, user.getFeatures());
            user.updateFeatures(updatedUserFeatures);

            return user;
        } catch (Exception e) {
            throw new RuntimeException(ResultCode.HISTORY_ANALYSIS_ERROR.getMessage(), e);
        }
    }
}
