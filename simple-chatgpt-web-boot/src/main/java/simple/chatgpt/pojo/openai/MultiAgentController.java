package simple.chatgpt.pojo.openai;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiAgentController {
    private static final Logger logger = LogManager.getLogger(MultiAgentController.class);

    private final AgentRegistry agentRegistry;
    private final TaskQueue taskQueue;
    private final ExecutorService executor;

    public MultiAgentController(AgentRegistry agentRegistry, TaskQueue taskQueue) {
        logger.debug("MultiAgentController constructor agentRegistry={}", agentRegistry);
        logger.debug("MultiAgentController constructor taskQueue={}", taskQueue);

        this.agentRegistry = agentRegistry;
        this.taskQueue = taskQueue;
        this.executor = Executors.newFixedThreadPool(agentRegistry.getAgents().size());
    }

    public void executeAll() {
        logger.debug("executeAll called");

        for (Agent agent : agentRegistry.getAgents()) {
            executor.submit(() -> {
                Task task;
                while ((task = taskQueue.dequeue()) != null) {
                    logger.debug("Agent {} picked up task={}", agent.getName(), task);
                    agent.perform(task);
                }
                logger.debug("Agent {} found no more tasks", agent.getName());
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("executeAll interrupted", e);
        }
        logger.debug("executeAll completed");
    }
}