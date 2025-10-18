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
public class SampleServiceImpl implements SampleService {

    private static final Logger logger = LogManager.getLogger(SampleServiceImpl.class);

    private final OpenAIClient client;

    /*
     * hung: constructor-based dependency injection
     * 
     * This sets up the OpenAI client instance that will be used for all
     * interactions with the OpenAI API (completions, chat, assistant simulations).
     */
    @Autowired
    public SampleServiceImpl(OpenAIClient client) {
        logger.debug("SampleServiceImpl constructor called");
        logger.debug("SampleServiceImpl client param={}", client);
        this.client = client;
    }

    /*
     * hung: simple completion using chat completion API
     *
     * Demonstrates a single prompt/response interaction similar to a
     * "text-davinci-003" style completion in older SDKs.
     * Uses the GPT-3.5-Turbo model with a simple static question.
     */
    @Override
    public void simpleCompletion() {
        logger.debug("simpleCompletion called");

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_3_5_TURBO)
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("user"))
                        .content("What is the capital of France?")
                        .build())
                .maxTokens(10L) // Limit the number of tokens in the response
                .build();
        logger.debug("simpleCompletion params built");

        ChatCompletion completion = client.chat().completions().create(params);
        logger.debug("simpleCompletion response received");

        String result = completion.choices().get(0).message().content().orElse("");
        logger.debug("simpleCompletion result={}", result);

        // Output the result
        System.out.println(result);
    }

    /*
     * hung: chat completion with custom question
     *
     * Demonstrates conversational chat interaction, where the input
     * is provided dynamically by the user. Mirrors the ChatCompletion
     * example in your Main class with "Who won the FIFA World Cup in 2018?".
     */
    @Override
    public void chatCompletion(String question) {
        logger.debug("chatCompletion called");
        logger.debug("chatCompletion question={}", question);

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_3_5_TURBO)
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("user"))
                        .content(question)
                        .build())
                .build();
        logger.debug("chatCompletion params built");

        ChatCompletion completion = client.chat().completions().create(params);
        logger.debug("chatCompletion response received");

        String result = completion.choices().get(0).message().content().orElse("");
        logger.debug("chatCompletion result={}", result);

        // Output the result
        System.out.println(result);
    }

    /*
     * hung: simulate assistant interaction using system + user messages
     *
     * Demonstrates how to simulate an assistant with instructions
     * (system message) and a user question. This mirrors the
     * Assistant/Thread/Run example from the Main class.
     * Uses GPT-4-Turbo for more advanced responses.
     */
    @Override
    public void assistantInteraction(String question) {
        logger.debug("assistantInteraction called");
        logger.debug("assistantInteraction question={}", question);

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4_TURBO)
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("system"))
                        .content("You are a math tutor. Explain answers in simple terms.")
                        .build())
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("user"))
                        .content(question)
                        .build())
                .build();
        logger.debug("assistantInteraction params built");

        ChatCompletion completion = client.chat().completions().create(params);
        logger.debug("assistantInteraction response received");

        String result = completion.choices().get(0).message().content().orElse("");
        logger.debug("assistantInteraction result={}", result);

        // Output assistant's answer
        System.out.println("Assistant: " + result);
    }
}
