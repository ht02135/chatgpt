package simple.chatgpt.service.openai2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;

import simple.chatgpt.pojo.openai.Agent;
import simple.chatgpt.pojo.openai.AgentRegistry;
import simple.chatgpt.pojo.openai.SequentialCrewExecutor;
import simple.chatgpt.pojo.openai.Task;
import simple.chatgpt.service.crewai.SequentialCrewAiServiceImpl;

/*
 * hung: sequential crew AI workflow orchestrator for content creation
 */
@Service("openaiAgentCrewService")
public class AgentCrewServiceImpl implements AgentCrewService {

    private static final Logger logger = LogManager.getLogger(SequentialCrewAiServiceImpl.class);

    private final OpenAIClient client;
    private final AgentRegistry agentRegistry;
    private final Map<String, String> taskResults = new HashMap<>();

    /*
     * hung: constructor with dependency injection
     */
    @Autowired
    public AgentCrewServiceImpl(OpenAIClient client) {
        logger.debug("AgentCrewServiceImpl constructor called");
        logger.debug("AgentCrewServiceImpl client={}", client);

        this.client = client;
        this.agentRegistry = new AgentRegistry();

        logger.debug("AgentCrewServiceImpl agentRegistry initialized");
        initAgents();
    }

    /*
     * hung: register planner, writer, and editor agents
     */
    private void initAgents() {
        logger.debug("initAgents called");

        Agent planner = new Agent("Content Planner", client);
        logger.debug("initAgents planner={}", planner);

        Agent writer = new Agent("Content Writer", client);
        logger.debug("initAgents writer={}", writer);

        Agent editor = new Agent("Editor", client);
        logger.debug("initAgents editor={}", editor);

        agentRegistry.register(planner);
        agentRegistry.register(writer);
        agentRegistry.register(editor);

        logger.debug("initAgents agents registered={}", agentRegistry.getAgents());
    }

    /*
     * hung: execute sequential crew workflow
     */
    @Override
    public String executeCrewWorkflow(String topic) {
        logger.debug("executeCrewWorkflow called");
        logger.debug("executeCrewWorkflow topic={}", topic);

        Agent planner = agentRegistry.getAgents().stream()
                .filter(a -> a.getName().equalsIgnoreCase("Content Planner"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("executeCrewWorkflow planner={}", planner);

        Agent writer = agentRegistry.getAgents().stream()
                .filter(a -> a.getName().equalsIgnoreCase("Content Writer"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("executeCrewWorkflow writer={}", writer);

        Agent editor = agentRegistry.getAgents().stream()
                .filter(a -> a.getName().equalsIgnoreCase("Editor"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("executeCrewWorkflow editor={}", editor);

        /*
         * hung: DO NOT change description or output text
         */
        Task planTask = new Task(
                planner,
                "1. Prioritize latest trends, key players and noteworthy news on " + topic + ".\n"
                        + "2. Identify the target audience, considering their interests and pain-points.\n"
                        + "3. Develop a detailed content outline including an introduction, key points, and a call to action.\n"
                        + "4. Include SEO keywords and relevant data or sources.",
                "A comprehensive content plan document with an outline, audience analysis, SEO keywords, and resources."
        );
        logger.debug("executeCrewWorkflow planTask={}", planTask);

        Task writeTask = new Task(
                writer,
                "1. Use the content plan to craft a compelling blog post on " + topic + ".\n"
                        + "2. Incorporate SEO keywords naturally.\n"
                        + "3. Sections/Subtitles properly named in an engaging manner.\n"
                        + "4. Ensure the post is structured with engaging introduction, insightful body, summarizing conclusion.\n"
                        + "5. Proof-read for grammar and alignment with the brand's voice.",
                "A well-written blog post in markdown format, ready for publication, each section with 2-3 paragraphs."
        );
        logger.debug("executeCrewWorkflow writeTask={}", writeTask);

        Task editTask = new Task(
                editor,
                "Proof-read the given blog post for grammatical errors and alignment with the brand's voice.",
                "A well-written blog post in markdown format, ready for publication, each section with 2-3 paragraphs."
        );
        logger.debug("executeCrewWorkflow editTask={}", editTask);

        SequentialCrewExecutor executor = new SequentialCrewExecutor(Arrays.asList(planTask, writeTask, editTask));
        logger.debug("executeCrewWorkflow executor={}", executor);

        String initialInput = topic;
        logger.debug("executeCrewWorkflow initialInput={}", initialInput);

        // run thru tasks and do final editTask review
        String result = editor.perform(editTask, executor.execute(initialInput));
        logger.debug("executeCrewWorkflow result={}", result);

        String taskId = UUID.randomUUID().toString();
        taskResults.put(taskId, result);
        logger.debug("executeCrewWorkflow taskId={}", taskId);

        return taskId;
    }

    /*
     * hung: retrieve task result
     */
    @Override
    public String getStatus(String taskId) {
        logger.debug("getStatus called");
        logger.debug("getStatus taskId={}", taskId);

        if (taskId == null || taskId.isEmpty()) {
            logger.warn("getStatus invalid taskId");
            throw new IllegalArgumentException("taskId cannot be null or empty");
        }

        String result = taskResults.get(taskId);
        logger.debug("getStatus result={}", result);

        if (result == null) {
            logger.debug("getStatus no result found for taskId={}", taskId);
            return "NOT_FOUND";
        }

        return "COMPLETED: " + result;
    }
}
