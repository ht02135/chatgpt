package simple.chatgpt.service.openai2;

import java.util.Collections;
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

/*
 * hung: Spring-managed service that orchestrates sequential multi-agent customer outreach workflows
 */
@Service("openaiSequentialCustomerOutreachService")
public class SequentialCustomerOutreachServiceImpl implements CustomerOutreachService {

    private static final Logger logger = LogManager.getLogger(SequentialCustomerOutreachServiceImpl.class);

    private final OpenAIClient client;
    private final AgentRegistry agentRegistry;
    private final SequentialCrewExecutor executor;
    private final Map<String, String> taskResults = new HashMap<>();

    /*
     * hung: constructor-based dependency injection
     */
    @Autowired
    public SequentialCustomerOutreachServiceImpl(OpenAIClient client) {
        logger.debug("SequentialCustomerOutreachServiceImpl constructor called");
        logger.debug("SequentialCustomerOutreachServiceImpl client param={}", client);

        this.client = client;
        this.agentRegistry = new AgentRegistry();
        this.executor = new SequentialCrewExecutor(Collections.emptyList());

        logger.debug("SequentialCustomerOutreachServiceImpl agentRegistry initialized");
        logger.debug("SequentialCustomerOutreachServiceImpl executor initialized");

        initAgents();
    }

    /*
     * hung: register outreach-related agents
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

    @Override
    public String kickoffVenueTasks() throws Exception {
        logger.debug("kickoffVenueTasks called");

        Agent agent = agentRegistry.getAgents().stream()
                .filter(a -> a.getName().equalsIgnoreCase("VenueCoordinator"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("kickoffVenueTasks agent={}", agent);

        String description = "Coordinate venue booking, setup, and layout.";
        String expectedOutput = "List of confirmed venues with setup instructions.";

        Task task = new Task(agent, description, expectedOutput);
        logger.debug("kickoffVenueTasks task={}", task);

        SequentialCrewExecutor seqExecutor = new SequentialCrewExecutor(Collections.singletonList(task));
        logger.debug("kickoffVenueTasks seqExecutor={}", seqExecutor);

        String initialInput = "Kickoff venue coordination sequence";
        logger.debug("kickoffVenueTasks initialInput={}", initialInput);

        seqExecutor.execute(initialInput);

        String result = agent.perform(task, "Venue coordination kickoff");
        logger.debug("kickoffVenueTasks result={}", result);

        String taskId = UUID.randomUUID().toString();
        taskResults.put(taskId, result);
        logger.debug("kickoffVenueTasks taskId={}", taskId);

        return taskId;
    }

    @Override
    public String kickoffLogisticsTasks() throws Exception {
        logger.debug("kickoffLogisticsTasks called");

        Agent agent = agentRegistry.getAgents().stream()
                .filter(a -> a.getName().equalsIgnoreCase("LogisticsManager"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("kickoffLogisticsTasks agent={}", agent);

        String description = "Plan transportation, catering, and materials.";
        String expectedOutput = "Detailed logistics plan and supplier confirmations.";

        Task task = new Task(agent, description, expectedOutput);
        logger.debug("kickoffLogisticsTasks task={}", task);

        SequentialCrewExecutor seqExecutor = new SequentialCrewExecutor(Collections.singletonList(task));
        logger.debug("kickoffLogisticsTasks seqExecutor={}", seqExecutor);

        String initialInput = "Kickoff logistics coordination sequence";
        logger.debug("kickoffLogisticsTasks initialInput={}", initialInput);

        seqExecutor.execute(initialInput);

        String result = agent.perform(task, "Logistics coordination kickoff");
        logger.debug("kickoffLogisticsTasks result={}", result);

        String taskId = UUID.randomUUID().toString();
        taskResults.put(taskId, result);
        logger.debug("kickoffLogisticsTasks taskId={}", taskId);

        return taskId;
    }

    @Override
    public String kickoffMarketingTasks() throws Exception {
        logger.debug("kickoffMarketingTasks called");

        Agent agent = agentRegistry.getAgents().stream()
                .filter(a -> a.getName().equalsIgnoreCase("MarketingCommunicator"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("kickoffMarketingTasks agent={}", agent);

        String description = "Prepare marketing content, emails, and outreach materials.";
        String expectedOutput = "Marketing campaign assets ready for launch.";

        Task task = new Task(agent, description, expectedOutput);
        logger.debug("kickoffMarketingTasks task={}", task);

        SequentialCrewExecutor seqExecutor = new SequentialCrewExecutor(Collections.singletonList(task));
        logger.debug("kickoffMarketingTasks seqExecutor={}", seqExecutor);

        String initialInput = "Kickoff marketing coordination sequence";
        logger.debug("kickoffMarketingTasks initialInput={}", initialInput);

        // run thru tasks and do final editTask review
        String result = agent.perform(task, seqExecutor.execute(initialInput));
        logger.debug("kickoffMarketingTasks result={}", result);

        String taskId = UUID.randomUUID().toString();
        taskResults.put(taskId, result);
        logger.debug("kickoffMarketingTasks taskId={}", taskId);

        return taskId;
    }

    @Override
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
