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
public class FewShotExampleServiceImpl implements FewShotExampleService {

    private static final Logger logger = LogManager.getLogger(FewShotExampleServiceImpl.class);

    private final OpenAIClient client;

    /*
     * hung: constructor-based dependency injection
     */
    @Autowired
    public FewShotExampleServiceImpl(OpenAIClient client) {
        logger.debug("FewShotExampleServiceImpl constructor called");
        logger.debug("FewShotExampleServiceImpl client param={}", client);
        this.client = client;
    }

    /*
     * hung: default example inference
     */
    @Override
    public void inferSentiment() {
        inferSentiment("It's fine, but the updates didn't add much new functionality.");
    }

    @Override
    public void inferSentiment(String text) {
        logger.debug("inferSentiment called");
        logger.debug("inferSentiment text={}", text);

        // Build chat completion parameters
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4_TURBO)
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("system"))
                        .content("You are an assistant that classifies sentiment.")
                        .build())
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("user"))
                        .content("Text: \"I love the new design!\" → Sentiment: Positive")
                        .build())
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("user"))
                        .content("Text: \"The app keeps crashing, it's frustrating.\" → Sentiment: Negative")
                        .build())
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("user"))
                        .content("Text: \"" + text + "\" → Sentiment:")
                        .build())
                .build();
        logger.debug("inferSentiment params built");

        // Send request
        ChatCompletion completion = client.chat().completions().create(params);
        logger.debug("inferSentiment completion response received");

        // Parse and log output
        completion.choices().forEach(choice -> {
            logger.debug("inferSentiment choice={}", choice);

            String result = choice.message().content().orElse("");
            logger.debug("inferSentiment result={}", result);

            System.out.println(result);
        });
    }
}
