package simple.chatgpt.service.openai2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;

import simple.chatgpt.pojo.openai.Agent;
import simple.chatgpt.pojo.openai.AgentRegistry;
import simple.chatgpt.pojo.openai.SequentialCrewExecutor;
import simple.chatgpt.pojo.openai.Task;

/*
 * hung: Spring-managed service that runs multi-agent tasks sequentially
 */
@Service
public class SequentialCrewAiServiceImpl {

    private static final Logger logger = LogManager.getLogger(SequentialCrewAiServiceImpl.class);

    private final OpenAIClient client;
    private final AgentRegistry agentRegistry;
    private final Map<String, String> taskResults = new HashMap<>();

    /*
     * hung: constructor-based dependency injection
     */
    public SequentialCrewAiServiceImpl(OpenAIClient client) {
        logger.debug("SequentialCrewAiServiceImpl constructor called");
        logger.debug("SequentialCrewAiServiceImpl client param={}", client);

        this.client = client;
        this.agentRegistry = new AgentRegistry();

        logger.debug("SequentialCrewAiServiceImpl agentRegistry initialized");

        initAgents();
    }

    /*
     * hung: register sequential agents
     */
    private void initAgents() {
        logger.debug("initAgents called");

        Agent venueAgent = new Agent("VenueCoordinator", client);
        Agent logisticsAgent = new Agent("LogisticsManager", client);
        Agent marketingAgent = new Agent("MarketingCommunicator", client);

        agentRegistry.register(venueAgent);
        agentRegistry.register(logisticsAgent);
        agentRegistry.register(marketingAgent);

        logger.debug("initAgents venueAgent registered={}", venueAgent);
        logger.debug("initAgents logisticsAgent registered={}", logisticsAgent);
        logger.debug("initAgents marketingAgent registered={}", marketingAgent);
    }

    /*
     * hung: kickoff sequential workflow
     */
    public String kickoffSequentialWorkflow() throws Exception {
        logger.debug("kickoffSequentialWorkflow called");

        Agent venueAgent = agentRegistry.getAgents().stream()
                .filter(a -> a.getName().equalsIgnoreCase("VenueCoordinator"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("kickoffSequentialWorkflow venueAgent={}", venueAgent);

        Agent logisticsAgent = agentRegistry.getAgents().stream()
                .filter(a -> a.getName().equalsIgnoreCase("LogisticsManager"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("kickoffSequentialWorkflow logisticsAgent={}", logisticsAgent);

        Agent marketingAgent = agentRegistry.getAgents().stream()
                .filter(a -> a.getName().equalsIgnoreCase("MarketingCommunicator"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("kickoffSequentialWorkflow marketingAgent={}", marketingAgent);

        Task venueTask = new Task(venueAgent, 
            "Coordinate venue booking, setup, and layout.", 
            "List of confirmed venues with setup instructions.");
        logger.debug("kickoffSequentialWorkflow venueTask={}", venueTask);

        Task logisticsTask = new Task(logisticsAgent, 
            "Plan transportation, catering, and materials.", 
            "Detailed logistics plan and supplier confirmations.");
        logger.debug("kickoffSequentialWorkflow logisticsTask={}", logisticsTask);

        Task marketingTask = new Task(marketingAgent, 
            "Prepare marketing content, emails, and outreach materials.", 
            "Marketing campaign assets ready for launch.");
        logger.debug("kickoffSequentialWorkflow marketingTask={}", marketingTask);

        SequentialCrewExecutor executor = new SequentialCrewExecutor(Arrays.asList(venueTask, logisticsTask, marketingTask));
        logger.debug("kickoffSequentialWorkflow executor={}", executor);

        String initialInput = "Kickoff sequential campaign coordination";
        logger.debug("kickoffSequentialWorkflow initialInput={}", initialInput);

        executor.execute(initialInput);

        // final output from the last agent
        String result = marketingAgent.perform(marketingTask, "Finalize campaign after logistics");
        logger.debug("kickoffSequentialWorkflow result={}", result);

        String taskId = UUID.randomUUID().toString();
        taskResults.put(taskId, result);
        logger.debug("kickoffSequentialWorkflow taskId={}", taskId);

        return taskId;
    }

    /*
     * hung: get workflow result by taskId
     */
    public String getStatus(String taskId) throws Exception {
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
