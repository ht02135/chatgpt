package simple.chatgpt.pojo.openai;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.type.Alias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/*
 hung: parallel executor - agents pick up tasks from a shared queue, returns outputs
 */
@Alias("openaiParallelCrewExecutor")		// for MyBatis    
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

    /**
     * hung: execute all tasks in parallel and return a map of Task -> output
     */
    public Map<Task, String> executeAllWithResults() {
        logger.debug("executeAllWithResults called");

        Map<Task, String> results = new ConcurrentHashMap<>();

        if (agentRegistry == null || agentRegistry.getAgents().isEmpty()) {
            logger.warn("executeAllWithResults no agents registered");
            return results;
        }
        if (taskQueue == null) {
            logger.warn("executeAllWithResults taskQueue is null");
            return results;
        }

        for (Agent agent : agentRegistry.getAgents()) {
            logger.debug("executeAllWithResults submitting agent={}", agent);
            executor.submit(() -> {
                Task task;
                while ((task = taskQueue.dequeue()) != null) {
                    logger.debug("Agent {} picked up task={}", agent.getName(), task);
                    String output = agent.perform(task);
                    logger.debug("Agent {} produced output={}", agent.getName(), output);
                    results.put(task, output);
                }
                logger.debug("Agent {} found no more tasks", agent.getName());
            });
        }

        executor.shutdown();
        try {
            boolean finished = executor.awaitTermination(10, TimeUnit.MINUTES);
            logger.debug("executeAllWithResults awaitTermination finished={}", finished);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("executeAllWithResults interrupted", e);
        }

        logger.debug("executeAllWithResults completed, results={}", results);
        return results;
    }
}
