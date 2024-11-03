import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;

@Service
@RequiredArgsConstructor
public class MeetingAnalyzer {
    private final VertexAI vertexAI;
    private final GenerativeModel model;

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

    public ApiResponse<List<String>> analyzeFeatures(MeetingInfo meeting) {
        try {
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
            List<String> features = parseFeatures(ResponseHandler.getText(response));
            
            return ApiResponse.<List<String>>builder()
                .resultCode(ResultCode.SUCCESS)
                .message("모임 특징 분석 완료")
                .data(features)
                .build();
        } catch (Exception e) {
            return ApiResponse.<List<String>>builder()
                .resultCode(ResultCode.INTERNAL_SERVER_ERROR)
                .message("API 호출 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    public ApiResponse<String> generateSummary(MeetingInfo meeting) {
        try {
            String prompt = String.format(SUMMARY_PROMPT,
                // ... format parameters
            );
            GenerateContentResponse response = model.generateContent(prompt);
            String summary = ResponseHandler.getText(response).trim();
            
            return ApiResponse.<String>builder()
                .resultCode(ResultCode.SUCCESS)
                .message("모임 소개 생성 완료")
                .data(summary)
                .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                .resultCode(ResultCode.INTERNAL_SERVER_ERROR)
                .message("API 호출 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
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
}