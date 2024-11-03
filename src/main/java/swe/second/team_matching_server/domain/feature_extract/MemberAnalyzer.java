import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;

@Service
@RequiredArgsConstructor
public class MemberAnalyzer {
    private final VertexAI vertexAI;
    private final GenerativeModel model;

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

    public ApiResponse<List<String>> analyzeFeatures(MeetingMemberElement member) {
        try {
            String prompt = String.format(MEMBER_PROMPT,
                member.getName(),
                member.getMajor(),
                member.getAttendenceScore(),
                member.getRole(),
                String.join(", ", member.getFeatures()),
                member.getStudentId());
                
            GenerateContentResponse response = model.generateContent(prompt);
            List<String> features = parseFeatures(ResponseHandler.getText(response));
            
            return ApiResponse.<List<String>>builder()
                .resultCode(ResultCode.SUCCESS)
                .message("멤버 특징 분석 완료")
                .data(features)
                .build();
        } catch (Exception e) {
            return ApiResponse.<List<String>>builder()
                .resultCode(ResultCode.INTERNAL_SERVER_ERROR)
                .message("API 호출 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
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