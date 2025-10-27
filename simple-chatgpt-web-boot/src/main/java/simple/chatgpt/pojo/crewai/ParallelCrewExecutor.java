package simple.chatgpt.pojo.crewai;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.type.Alias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/*
 hung: parallel executor - agents pick up tasks from a shared queue
 */
@Alias("crewaiParallelCrewExecutor")		// for MyBatis    
public class ParallelCrewExecutor {
    private static final Logger logger = LogManager.getLogger(ParallelCrewExecutor.class);

    private final AgentRegistry agentRegistry;
    private final TaskQueue taskQueue;
    private ExecutorService executor;

    public ParallelCrewExecutor(AgentRegistry agentRegistry, TaskQueue taskQueue) {
        logger.debug("ParallelCrewExecutor constructor called");
        logger.debug("ParallelCrewExecutor agentRegistry={}", agentRegistry);
        logger.debug("ParallelCrewExecutor taskQueue={}", taskQueue);

        this.agentRegistry = agentRegistry;
        this.taskQueue = taskQueue;
        this.executor = Executors.newFixedThreadPool(Math.max(1, agentRegistry.getAgents().size()));

        logger.debug("ParallelCrewExecutor this={}", this);
    }

    private static void init() {
        logger.debug("init called");
    }

    public void setAgentRegistry(AgentRegistry agentRegistry) {
        logger.debug("setAgentRegistry called");
        logger.debug("setAgentRegistry agentRegistry={}", agentRegistry);

        if (agentRegistry != null) {
            this.executor = Executors.newFixedThreadPool(Math.max(1, agentRegistry.getAgents().size()));
            logger.debug("Executor reinitialized due to new agentRegistry");
        }
    }

    /**
     * Execute all tasks in parallel. Agents pull tasks from shared queue.
     */
    public void executeAll() {
        logger.debug("executeAll called");

        if (agentRegistry == null || agentRegistry.getAgents().isEmpty()) {
            logger.warn("executeAll no agents registered");
            return;
        }
        if (taskQueue == null) {
            logger.warn("executeAll taskQueue is null");
            return;
        }

        for (Agent agent : agentRegistry.getAgents()) {
            logger.debug("executeAll submitting agent={}", agent);
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
            boolean finished = executor.awaitTermination(10, TimeUnit.MINUTES);
            logger.debug("executeAll awaitTermination finished={}", finished);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("executeAll interrupted", e);
        }
        logger.debug("executeAll completed");
    }
}
