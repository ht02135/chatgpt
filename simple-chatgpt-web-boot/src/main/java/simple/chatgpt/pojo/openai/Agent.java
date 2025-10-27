package simple.chatgpt.pojo.openai;

import org.apache.ibatis.type.Alias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.openai.client.OpenAIClient;
import com.openai.core.JsonValue;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;

@Alias("openaiAgent")		// for MyBatis    
public class Agent {
    private static final Logger logger = LogManager.getLogger(Agent.class);

    private final String name;
    private final String role;
    private final String goal;
    private final String backstory;

    private final OpenAIClient client; // injected OpenAI client

    /*
     * hung: simple constructor using only name
     */
    public Agent(String name, OpenAIClient client) {
        this.name = name;
        this.role = null;
        this.goal = null;
        this.backstory = null;
        this.client = client;

        logger.debug("Agent constructor called (name) name={}", name);
        logger.debug("Agent constructor client={}", client);
    }

    /*
     * hung: detailed constructor with role, goal, backstory
     */
    public Agent(String role, String goal, String backstory, OpenAIClient client) {
        this.name = null;
        this.role = role;
        this.goal = goal;
        this.backstory = backstory;
        this.client = client;

        logger.debug("Agent constructor called (role/goal/backstory) role={}, goal={}, backstory={}", role, goal, backstory);
        logger.debug("Agent constructor client={}", client);
    }

    // getters
    public String getName() { logger.debug("getName called"); return name; }
    public String getRole() { logger.debug("getRole called"); return role; }
    public String getGoal() { logger.debug("getGoal called"); return goal; }
    public String getBackstory() { logger.debug("getBackstory called"); return backstory; }

    /*
     * hung: perform task using OpenAI (void version)
     * simply delegates to the advanced method with empty input
     */
    public String perform(Task task) {
        logger.debug("perform called (simple) task={} by agent={}", task, name != null ? name : role);
        String result = perform(task, "");  // delegate to OpenAI-enabled method
        logger.debug("perform (simple) completed with result={}", result);
        return result;
    }

    /*
     * hung: advanced perform using OpenAI API (returns generated result)
     */
    /*
     * hung: advanced perform using OpenAI API (returns generated result)
     */
    public String perform(Task task, String input) {
        logger.debug("perform called (advanced) agent={}, task={}, input={}", this, task, input);

        String actor = role != null ? role : name;

        try {
            // build chat completion
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model(ChatModel.GPT_4_TURBO)
                    .addMessage(ChatCompletionMessage.builder()
                            .role(JsonValue.from("system"))
                            .content(backstory != null ? backstory : "You are an AI agent performing tasks.")
                            .refusal(JsonValue.from(null)) // ✅ NEW: required since OpenAI SDK 4.5.0
                            .build())
                    .addMessage(ChatCompletionMessage.builder()
                            .role(JsonValue.from(role != null ? role : "user"))
                            .content("Task: " + task.getDescription() + "\nInput: " + input)
                            .refusal(JsonValue.from(null)) // ✅ NEW: also required
                            .build())
                    .build();
            logger.debug("perform OpenAI params built");

            // call OpenAI
            ChatCompletion completion = client.chat().completions().create(params);
            logger.debug("perform OpenAI response received");

            // parse and log output
            String result = completion.choices().get(0).message().content().orElse("");
            logger.debug("perform OpenAI result={}", result);

            return result;

        } catch (Exception e) {
            logger.error("Agent {} failed performing task={}", actor, task, e);
            return "[ERROR: Agent failed to perform task]";
        }
    }

    @Override
    public String toString() {
        return role != null ? "Agent{role='" + role + "'}" : "Agent{name='" + name + "'}";
    }
}

/*
Here’s the reasoning and flow:
1>Agent responsibility:
Each Agent knows its role, goal, and backstory.
Each Agent is the entity that “performs” a task.
When performing a task, it constructs the prompt/messages and calls OpenAI.

2>Other components (Service, Controller, Crew, TaskQueue, etc.):
They orchestrate tasks and agents.
They do not call OpenAI directly.
Their job is to enqueue tasks, assign them to agents, and collect results.

3>Advantages:
Centralizes all OpenAI calls in one place (Agent.perform) — easy to maintain.
Keeps workflow logic separate from LLM logic.
Makes it easy to swap LLM backend later if needed. 
*/
