package simple.chatgpt.service.openai.crewai2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import simple.chatgpt.pojo.openai.crewai2.Agent;
import simple.chatgpt.pojo.openai.crewai2.AgentRegistry;
import simple.chatgpt.pojo.openai.crewai2.Task;
import simple.chatgpt.pojo.openai.crewai2.TaskQueue;

/*
 hung: parallel executor - agents pick up tasks from a shared queue
 */
public class ParallelCrewServiceImpl implements ParallelCrewService {
    private static final Logger logger = LogManager.getLogger(ParallelCrewServiceImpl.class);

    private AgentRegistry agentRegistry;
    private TaskQueue taskQueue;
    private ExecutorService executor;

    public ParallelCrewServiceImpl(AgentRegistry agentRegistry, TaskQueue taskQueue) {
        logger.debug("ParallelCrewServiceImpl constructor called");
        logger.debug("ParallelCrewServiceImpl agentRegistry={}", agentRegistry);
        logger.debug("ParallelCrewServiceImpl taskQueue={}", taskQueue);

        this.agentRegistry = agentRegistry;
        this.taskQueue = taskQueue;
        this.executor = Executors.newFixedThreadPool(Math.max(1, agentRegistry.getAgents().size()));

        logger.debug("ParallelCrewServiceImpl this={}", this);
    }

    private static void init() {
        logger.debug("init called");
    }

    @Override
    public void setAgentRegistry(AgentRegistry agentRegistry) {
        logger.debug("setAgentRegistry called");
        logger.debug("setAgentRegistry agentRegistry={}", agentRegistry);

        this.agentRegistry = agentRegistry;
        if (agentRegistry != null) {
            this.executor = Executors.newFixedThreadPool(Math.max(1, agentRegistry.getAgents().size()));
            logger.debug("Executor reinitialized due to new agentRegistry");
        }
    }

    @Override
    public void setTaskQueue(TaskQueue taskQueue) {
        logger.debug("setTaskQueue called");
        logger.debug("setTaskQueue taskQueue={}", taskQueue);

        this.taskQueue = taskQueue;
    }

    @Override
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
