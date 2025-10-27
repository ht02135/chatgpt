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
import simple.chatgpt.pojo.openai.ParallelCrewExecutor;
import simple.chatgpt.pojo.openai.Task;
import simple.chatgpt.pojo.openai.TaskQueue;

/*
 * hung: Spring-managed service that orchestrates multi-agent customer outreach workflows
 * Fixed: executor outputs captured properly
 */
@Service("openaiCustomerOutreachService")
public class CustomerOutreachServiceImpl implements CustomerOutreachService {

    private static final Logger logger = LogManager.getLogger(CustomerOutreachServiceImpl.class);

    private final OpenAIClient client;
    private final AgentRegistry agentRegistry;
    private final TaskQueue taskQueue;
    private final ParallelCrewExecutor executor;
    private final Map<String, String> taskResults = new HashMap<>();

    @Autowired
    public CustomerOutreachServiceImpl(OpenAIClient client) {
        logger.debug("CustomerOutreachServiceImpl constructor called");
        logger.debug("CustomerOutreachServiceImpl client param={}", client);

        this.client = client;
        this.agentRegistry = new AgentRegistry();
        this.taskQueue = new TaskQueue();
        this.executor = new ParallelCrewExecutor(agentRegistry, taskQueue);

        logger.debug("CustomerOutreachServiceImpl agentRegistry initialized");
        logger.debug("CustomerOutreachServiceImpl taskQueue initialized");
        logger.debug("CustomerOutreachServiceImpl executor initialized");

        initAgents();
    }

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

        Task task = new Task(agent,
                "Coordinate venue booking, setup, and layout.",
                "List of confirmed venues with setup instructions.");
        logger.debug("kickoffVenueTasks task={}", task);

        taskQueue.enqueue(Collections.singletonList(task));

        Map<Task, String> results = executor.executeAllWithResults();
        String result = results.get(task);
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

        Task task = new Task(agent,
                "Plan transportation, catering, and materials.",
                "Detailed logistics plan and supplier confirmations.");
        logger.debug("kickoffLogisticsTasks task={}", task);

        taskQueue.enqueue(Collections.singletonList(task));

        Map<Task, String> results = executor.executeAllWithResults();
        String result = results.get(task);
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

        Task task = new Task(agent,
                "Prepare marketing content, emails, and outreach materials.",
                "Marketing campaign assets ready for launch.");
        logger.debug("kickoffMarketingTasks task={}", task);

        taskQueue.enqueue(Collections.singletonList(task));

        Map<Task, String> results = executor.executeAllWithResults();
        String result = results.get(task);
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
            return "NOT_FOUND";
        }

        return "COMPLETED: " + result;
    }
}
