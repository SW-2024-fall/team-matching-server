package swe.second.team_matching_server.core.config;

import java.io.File;

import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;

@Slf4j
@Configuration
public class GeminiConfig {

    @Value("${gemini.project-id}")
    private String projectId;
    @Value("${gemini.location}")
    private String location;
    @Value("${gemini.model-name}")
    private String modelName;
    @Value("${gemini.credential-path}")
    private String credentialPath;

    private ChatLanguageModel model;

    @PostConstruct
    public void init() {
        log.info("projectId: " + projectId);
        log.info("location: " + location);
        log.info("modelName: " + modelName);
        log.info("credentialPath: " + credentialPath);

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

    private void setGoogleCredentials() {
        try {
            // cut 'src' and get project root path
            String projectRootPath = new File(".").getAbsolutePath();
            String credentialsPath = projectRootPath.substring(0, projectRootPath.lastIndexOf('.')) + credentialPath; 

            // set Google credentials property
            System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", credentialsPath);

            log.info("GOOGLE_APPLICATION_CREDENTIALS: " + System.getProperty("GOOGLE_APPLICATION_CREDENTIALS"));

        } catch (Exception e) {
            throw new RuntimeException("Failed to set Google credentials: " + e.getMessage(), e);
        }
    }

    @Bean
    public ChatLanguageModel model() {
        return model;
    }
}
