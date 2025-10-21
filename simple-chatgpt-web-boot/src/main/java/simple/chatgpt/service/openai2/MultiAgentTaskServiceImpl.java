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
import simple.chatgpt.pojo.openai.ParallelCrewExecutor;
import simple.chatgpt.pojo.openai.Task;
import simple.chatgpt.pojo.openai.TaskQueue;

/*
 * hung: parallel multi-agent service implementation
 */
@Service
public class MultiAgentTaskServiceImpl implements MultiAgentTaskService {

    private static final Logger logger = LogManager.getLogger(MultiAgentTaskServiceImpl.class);

    private final OpenAIClient client;
    private final AgentRegistry agentRegistry;
    private final TaskQueue taskQueue;
    private final ParallelCrewExecutor executor;
    private final Map<String, String> taskResults = new HashMap<>();

    /*
     * hung: constructor-based dependency injection
     */
    public MultiAgentTaskServiceImpl(OpenAIClient client) {
        logger.debug("MultiAgentTaskServiceImpl constructor called");
        logger.debug("MultiAgentTaskServiceImpl client={}", client);

        this.client = client;
        this.agentRegistry = new AgentRegistry();
        this.taskQueue = new TaskQueue();
        this.executor = new ParallelCrewExecutor(agentRegistry, taskQueue);

        logger.debug("MultiAgentTaskServiceImpl agentRegistry initialized");
        logger.debug("MultiAgentTaskServiceImpl taskQueue initialized");
        logger.debug("MultiAgentTaskServiceImpl executor initialized");

        initAgents();
    }

    /*
     * hung: register agents for parallel workflow
     */
    private void initAgents() {
        logger.debug("initAgents called");

        Agent agent1 = new Agent("Agent-Alpha", client);
        logger.debug("initAgents agent1={}", agent1);

        Agent agent2 = new Agent("Agent-Beta", client);
        logger.debug("initAgents agent2={}", agent2);

        agentRegistry.register(agent1);
        agentRegistry.register(agent2);
        logger.debug("initAgents agents registered={}", agentRegistry.getAgents());
    }

    /*
     * hung: execute multi-agent workflow
     */
    @Override
    public String executeMultiAgentWorkflow() {
        logger.debug("executeMultiAgentWorkflow called");

        Agent agent1 = agentRegistry.getAgents().stream()
                .filter(a -> a.getName().equalsIgnoreCase("Agent-Alpha"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("executeMultiAgentWorkflow agent1={}", agent1);

        Agent agent2 = agentRegistry.getAgents().stream()
                .filter(a -> a.getName().equalsIgnoreCase("Agent-Beta"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("executeMultiAgentWorkflow agent2={}", agent2);

        /*
         * hung: DO NOT alter task descriptions
         */
        Task task1 = new Task(agent1, "Process data segment A", "Output A");
        logger.debug("executeMultiAgentWorkflow task1={}", task1);

        Task task2 = new Task(agent2, "Analyze image set B", "Output B");
        logger.debug("executeMultiAgentWorkflow task2={}", task2);

        Task task3 = new Task(agent1, "Generate summary report C", "Output C");
        logger.debug("executeMultiAgentWorkflow task3={}", task3);

        taskQueue.enqueue(Arrays.asList(task1, task2, task3));
        logger.debug("executeMultiAgentWorkflow taskQueue after enqueue={}", taskQueue);

        executor.executeAll();
        logger.debug("executeMultiAgentWorkflow executor execution complete");

        // Example final result aggregation (simulate final outcome)
        String result = String.join(", ",
                agent1.perform(task1, "Task A input"),
                agent2.perform(task2, "Task B input"),
                agent1.perform(task3, "Task C input"));
        logger.debug("executeMultiAgentWorkflow result={}", result);

        String taskId = UUID.randomUUID().toString();
        taskResults.put(taskId, result);
        logger.debug("executeMultiAgentWorkflow taskId={}", taskId);

        return taskId;
    }

    /*
     * hung: retrieve task result
     */
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
