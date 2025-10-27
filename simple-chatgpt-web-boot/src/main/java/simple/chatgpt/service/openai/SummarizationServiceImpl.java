package simple.chatgpt.service.openai;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;
import com.openai.core.JsonValue;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;

@Service
public class SummarizationServiceImpl implements SummarizationService {

    private static final Logger logger = LogManager.getLogger(SummarizationServiceImpl.class);

    private final OpenAIClient client;

    /*
     * hung: constructor-based dependency injection
     */
    @Autowired
    public SummarizationServiceImpl(OpenAIClient client) {
        logger.debug("SummarizationServiceImpl constructor called");
        logger.debug("SummarizationServiceImpl client param={}", client);
        this.client = client;
    }
    
    @Override
    public void summarizeText() {
    	summarizeText(
    			"OpenAI has released a new model that improves efficiency and reduces hallucination rates."
        );
    }

    @Override
    public void summarizeText(String text) {
        logger.debug("summarizeText called");
        logger.debug("summarizeText text={}", text);

        // Build chat completion parameters
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4_TURBO)
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("system"))
                        .content("You are a helpful assistant that summarizes text.")
                        .build())
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("user"))
                        .content("Summarize this in one sentence: " + text)
                        .build())
                .build();
        logger.debug("summarizeText params built");

        // Send request
        ChatCompletion completion = client.chat().completions().create(params);
        logger.debug("summarizeText completion response received");

        // Parse and log output
        completion.choices().forEach(choice -> {
            logger.debug("summarizeText choice={}", choice);

            // Handle Optional<String> safely
            String result = choice.message().content().orElse("");
            logger.debug("summarizeText result={}", result);

            System.out.println(result);
        });
    }
}
