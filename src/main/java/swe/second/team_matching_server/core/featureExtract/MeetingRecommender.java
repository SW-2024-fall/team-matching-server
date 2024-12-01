package swe.second.team_matching_server.core.featureExtract;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import dev.langchain4j.model.chat.ChatLanguageModel;

import swe.second.team_matching_server.core.featureExtract.dto.MeetingRecommendation;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.user.model.entity.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeetingRecommender {
    
    private final ChatLanguageModel model;

    private static final String RECOMMENDATION_PROMPT = """
            당신은 서울시립대학교 학생들을 위한 모임 추천 전문가입니다.
            다음 학생의 정보와 현재 진행 중인 모임들의 정보를 바탕으로 이 학생에게 가장 적합한 모임 3개를 추천해주세요.

            학생 정보:
            이름: %s
            전공: %s
            학번: %s
            출석 점수: %d
            특징: %s

            추천 가능한 모임 목록:
            %s

            위 정보를 바탕으로 정확히 다음 형식으로만 추천해주세요:
            1. [1]: 추천 이유
            2. [2]: 추천 이유
            3. [3]: 추천 이유

            반드시 모임 ID만 대괄호 안에 숫자로 입력해주세요.
            """;

    public List<MeetingRecommendation> recommendMeetings(User user, List<Meeting> meetings) {
        String prompt = String.format(RECOMMENDATION_PROMPT,
                user.getUsername(),
                user.getMajor().toString(),
                user.getStudentId(),
                user.getAttendanceScore(),
                user.getFeatures(),
                formatMeetingsList(meetings));
                
                String response = model.generate(prompt);
                return parseRecommendations(response);
            }

    private String formatMeetingsList(List<Meeting> meetings) {
        StringBuilder sb = new StringBuilder();
        for (Meeting meeting : meetings) {
            sb.append(meeting.toString()).append("\n\n");
        }
        return sb.toString();
    }

    private List<MeetingRecommendation> parseRecommendations(String response) {
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
                    log.error("ID 파싱 실패: " + line);
                    continue;
                }
            }
        }
        return recommendations;
    }
}