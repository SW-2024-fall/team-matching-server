import org.springframework.context.annotation.Configuration;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;

@Configuration
public class ContentAnalyzerConfig {
    @Value("${google.cloud.project-id}")
    private String projectId;
    
    @Value("${google.cloud.location}")
    private String location;
    
    @Bean
    public VertexAI vertexAI() {
        return new VertexAI(projectId, location);
    }

    @Bean
    public GenerativeModel generativeModel(VertexAI vertexAI) {
        return new GenerativeModel("gemini-1.5-flash-001", vertexAI);
    }
}