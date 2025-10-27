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
public class TransformationServiceImpl implements TransformationService {

    private static final Logger logger = LogManager.getLogger(TransformationServiceImpl.class);

    private final OpenAIClient client;

    /*
     * hung: constructor-based dependency injection
     */
    @Autowired
    public TransformationServiceImpl(OpenAIClient client) {
        logger.debug("TransformationServiceImpl constructor called");
        logger.debug("TransformationServiceImpl client param={}", client);
        this.client = client;
    }

    /*
     * hung: default transformation using example text
     */
    @Override
    public void transformTone() {
        transformTone(
                "Our product launch was delayed due to supply chain issues.",
                "enthusiastic"
        );
    }

    @Override
    public void transformTone(String text, String tone) {
        logger.debug("transformTone called");
        logger.debug("transformTone text={}", text);
        logger.debug("transformTone tone={}", tone);

        // Build chat completion parameters
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4_TURBO)
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("system"))
                        .content("You are an assistant that rewrites text in different tones.")
                        .build())
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("user"))
                        .content("Rewrite the following text in a " + tone + " tone:\n\n" + text)
                        .build())
                .build();
        logger.debug("transformTone params built");

        // Send request
        ChatCompletion completion = client.chat().completions().create(params);
        logger.debug("transformTone completion response received");

        // Parse and log output
        completion.choices().forEach(choice -> {
            logger.debug("transformTone choice={}", choice);

            String result = choice.message().content().orElse("");
            logger.debug("transformTone result={}", result);

            System.out.println(result);
        });
    }
}
