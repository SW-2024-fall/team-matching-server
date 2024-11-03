package swe.second.team_matching_server.domain.meeting.FeatureExtract;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;

public class GeminiQAExample {
    private static final String PROJECT_ID = "named-berm-382814";
    private static final String LOCATION = "us-central1";
    
    public static void main(String[] args) {
        // Gemini 모델 초기화
        ChatLanguageModel model = VertexAiGeminiChatModel.builder()
            .project(PROJECT_ID)
            .location(LOCATION)
            .modelName("gemini-1.5-flash-001")
            .build();

        // 질문하기
        String question = "What is the capital of France?";
        String answer = model.generate(question);
        
        // 결과 출력
        System.out.println("Question: " + question);
        System.out.println("Answer: " + answer);
    }
}