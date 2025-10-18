package simple.chatgpt.service.openai;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonValue;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;

@Service
public class ReviewExtractorServiceImpl implements ReviewExtractorService {

    private static final Logger logger = LogManager.getLogger(ReviewExtractorServiceImpl.class);

    private final OpenAIClient client;

    /*
     * hung: use constructor-based dependency injection
     */
    @Autowired
    public ReviewExtractorServiceImpl(OpenAIClient client) {
        logger.debug("ReviewExtractorServiceImpl constructor called");
        logger.debug("ReviewExtractorServiceImpl client param={}", client);
        this.client = client;
    }

    @Override
    public void extractReviewData() {
        logger.debug("extractReviewData called");

        // Initialize OpenAI client
        OpenAIClient client = OpenAIOkHttpClient.fromEnv();
        logger.debug("extractReviewData client initialized");

        // Example input text (like from user)
        String reviewText = "I bought the iPhone 15 last week and I absolutely love it! "
                + "The camera is stunning but battery life could be better.";
        logger.debug("extractReviewData reviewText={}", reviewText);

        // Build chat completion parameters
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4_TURBO)
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("system"))
                        .content("You are an AI that extracts structured data in JSON format from text reviews.")
                        .build())
                .addMessage(ChatCompletionMessage.builder()
                        .role(JsonValue.from("user"))
                        .content("Extract the following information as JSON: "
                                + "product name, sentiment (positive/negative/neutral), and one-sentence summary.\n\n"
                                + "Text: " + reviewText)
                        .build())
                .build();
        logger.debug("extractReviewData params built");

        // Send request
        ChatCompletion completion = client.chat().completions().create(params);
        logger.debug("extractReviewData completion response received");

        // Parse and log output
        completion.choices().forEach(choice -> {
            logger.debug("extractReviewData choice={}", choice);

            // Handle Optional<String> properly
            String result = choice.message().content().orElse("");
            logger.debug("extractReviewData result={}", result);

            System.out.println(result);
        });
    }
}
