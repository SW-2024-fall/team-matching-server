import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;

@Service
@RequiredArgsConstructor
public class HistoryAnalyzer {
    private final VertexAI vertexAI;
    private final GenerativeModel model;

    @Value("${google.cloud.project-id}")
    private String projectId;
    
    @Value("${google.cloud.location}")
    private String location;
    
    private static final String MODEL_NAME = "gemini-1.5-flash-001";

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

    @PostConstruct
    public void init() {
        this.vertexAI = new VertexAI(projectId, location);
        this.model = new GenerativeModel(MODEL_NAME, vertexAI);
    }

    public ApiResponse<List<String>> updateMeetingFeatures(History history, List<String> currentFeatures) {
        try {
            String prompt = String.format(UPDATE_MEETING_FEATURES_PROMPT,
                String.join("\n", currentFeatures),
                history.getTitle(),
                history.getContent(),
                history.getLocation(),
                history.getDate().toString());
                
            GenerateContentResponse response = model.generateContent(prompt);
            List<String> features = parseFeatures(ResponseHandler.getText(response));
            
            return ApiResponse.<List<String>>builder()
                .resultCode(ResultCode.SUCCESS)
                .message("모임 특징 업데이트 완료")
                .data(features)
                .build();
        } catch (Exception e) {
            return ApiResponse.<List<String>>builder()
                .resultCode(ResultCode.INTERNAL_SERVER_ERROR)
                .message("API 호출 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    public ApiResponse<String> updateMeetingIntroduction(History history, String currentIntro) {
        try {
            String prompt = String.format(UPDATE_MEETING_INTRO_PROMPT,
                currentIntro,
                history.getTitle(),
                history.getContent(),
                history.getLocation(),
                history.getDate().toString());
                
            GenerateContentResponse response = model.generateContent(prompt);
            String introduction = ResponseHandler.getText(response).trim();
            
            return ApiResponse.<String>builder()
                .resultCode(ResultCode.SUCCESS)
                .message("모임 소개 업데이트 완료")
                .data(introduction)
                .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                .resultCode(ResultCode.INTERNAL_SERVER_ERROR)
                .message("API 호출 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    public ApiResponse<List<String>> updateUserFeatures(History history, List<String> currentFeatures) {
        try {
            String prompt = String.format(UPDATE_USER_FEATURES_PROMPT,
                String.join("\n", currentFeatures),
                history.getTitle(),
                history.getContent(),
                history.getLocation(),
                history.getDate().toString());
                
            GenerateContentResponse response = model.generateContent(prompt);
            List<String> features = parseFeatures(ResponseHandler.getText(response));
            
            return ApiResponse.<List<String>>builder()
                .resultCode(ResultCode.SUCCESS)
                .message("사용자 특징 업데이트 완료")
                .data(features)
                .build();
        } catch (Exception e) {
            return ApiResponse.<List<String>>builder()
                .resultCode(ResultCode.INTERNAL_SERVER_ERROR)
                .message("API 호출 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    public ApiResponse<HistoryUpdateResult> updateComplete(History history) {
        try {
            Meeting meeting = history.getMeeting();
            User user = history.getUser();
            
            ApiResponse<List<String>> meetingFeaturesResponse = updateMeetingFeatures(history, meeting.getFeatures());
            ApiResponse<String> meetingIntroResponse = updateMeetingIntroduction(history, meeting.getAnalyzedIntroduction());
            ApiResponse<List<String>> userFeaturesResponse = updateUserFeatures(history, user.getFeatures());

            if (ResultCode.SUCCESS.equals(meetingFeaturesResponse.getResultCode()) &&
                ResultCode.SUCCESS.equals(meetingIntroResponse.getResultCode()) &&
                ResultCode.SUCCESS.equals(userFeaturesResponse.getResultCode())) {

                HistoryUpdateResult result = HistoryUpdateResult.builder()
                    .meetingFeatures(meetingFeaturesResponse.getData())
                    .meetingIntroduction(meetingIntroResponse.getData())
                    .userFeatures(userFeaturesResponse.getData())
                    .build();

                return ApiResponse.<HistoryUpdateResult>builder()
                    .resultCode(ResultCode.SUCCESS)
                    .message("모임 및 사용자 정보 업데이트 완료")
                    .data(result)
                    .build();
            } else {
                return ApiResponse.<HistoryUpdateResult>builder()
                    .resultCode(ResultCode.INTERNAL_SERVER_ERROR)
                    .message("업데이트 중 오류가 발생했습니다")
                    .build();
            }
        } catch (Exception e) {
            return ApiResponse.<HistoryUpdateResult>builder()
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