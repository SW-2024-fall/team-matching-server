package swe.second.team_matching_server.core.config;

import java.io.File;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;

@Configuration
public class GeminiConfig {

    @Value("${gemini.project-id}")
    private static String projectId;
    @Value("${gemini.location}")
    private static String location;
    @Value("${gemini.model-name}")
    private static String modelName;
    @Value("${gemini.credentials-path}")
    private static String credentialsPath;

    private ChatLanguageModel model;

    @PostConstruct
    public void init() {
        setGoogleCredentials();
        model = setModel();
    }
    
    private VertexAiGeminiChatModel setModel() {
        return VertexAiGeminiChatModel.builder()
                .project(projectId)
                .location(location)
                .modelName(modelName)
                .build();
    }

    private static void setGoogleCredentials() {
        try {
            // get current's absolute path
            String currentPath = new File(".").getAbsolutePath();
            // cut 'src' and get project root path
            String projectRootPath = currentPath.substring(0, currentPath.indexOf("src"));
            String credentialsPath = projectRootPath + GeminiConfig.credentialsPath; 

            // set Google credentials property
            System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", credentialsPath);

        } catch (Exception e) {
            throw new RuntimeException("Failed to set Google credentials: " + e.getMessage(), e);
        }
    }

    @Bean
    public ChatLanguageModel model() {
        return model;
    }
}
