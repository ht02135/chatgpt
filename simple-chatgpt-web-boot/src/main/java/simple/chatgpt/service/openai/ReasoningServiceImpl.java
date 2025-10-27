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
public class ReasoningServiceImpl implements ReasoningService {

    private static final Logger logger = LogManager.getLogger(ReasoningServiceImpl.class);

    private final OpenAIClient client;

    /*
     * hung: constructor-based dependency injection
     */
    @Autowired
    public ReasoningServiceImpl(OpenAIClient client) {
        logger.debug("ReasoningServiceImpl constructor called");
        logger.debug("ReasoningServiceImpl client param={}", client);
        this.client = client;
    }

    /*
     * hung: default example logic problem
     */
    @Override
    public void solveLogicProblem() {
        solveLogicProblem("If John is older than Mary and Mary is older than Sam, who is the oldest?");
    }

    @Override
    public void solveLogicProblem(String problem) {
        logger.debug("solveLogicProblem called");
        logger.debug("solveLogicProblem problem={}", problem);

        // Build chat completion parameters
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4_TURBO)
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("system"))
                        .content("You are a reasoning assistant. Think step by step before answering.")
                        .build())
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("user"))
                        .content(problem + " Let's reason this out step by step.")
                        .build())
                .build();
        logger.debug("solveLogicProblem params built");

        // Send request
        ChatCompletion completion = client.chat().completions().create(params);
        logger.debug("solveLogicProblem completion response received");

        // Parse and log output
        completion.choices().forEach(choice -> {
            logger.debug("solveLogicProblem choice={}", choice);

            String result = choice.message().content().orElse("");
            logger.debug("solveLogicProblem result={}", result);

            System.out.println(result);
        });
    }
}
