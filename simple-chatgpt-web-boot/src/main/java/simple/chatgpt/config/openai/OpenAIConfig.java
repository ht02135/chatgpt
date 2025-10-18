package simple.chatgpt.config.openai;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

@Configuration
public class OpenAIConfig {

    private static final Logger logger = LogManager.getLogger(OpenAIConfig.class);

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Bean
    public OpenAIClient openAIClient() {
        logger.debug("Creating OpenAIClient bean with API key from properties");
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(openaiApiKey)
                .build();
        logger.debug("OpenAIClient bean created: {}", client);
        return client;
    }
}
