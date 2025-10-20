package simple.chatgpt.service.openai.crewai2;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import simple.chatgpt.pojo.openai.crewai2.Agent;
import simple.chatgpt.pojo.openai.crewai2.Task;

/*
 hung: sequential executor - runs tasks in order
 */
public class SequentialCrewServiceImpl implements SequentialCrewService {
    private static final Logger logger = LogManager.getLogger(SequentialCrewServiceImpl.class);

    private List<Task> tasks;

    public SequentialCrewServiceImpl(List<Task> tasks) {
        logger.debug("SequentialCrewServiceImpl constructor called");
        logger.debug("SequentialCrewServiceImpl tasks={}", tasks);

        this.tasks = tasks;
        logger.debug("SequentialCrewServiceImpl this={}", this);
    }

    private static void init() {
        logger.debug("init called");
    }

    @Override
    public void setTasks(List<Task> tasks) {
        logger.debug("setTasks called");
        logger.debug("setTasks tasks={}", tasks);
        this.tasks = tasks;
    }

    /**
     * Execute tasks in sequence. Each task's output is fed as input to the next task.
     */
    @Override
    public void execute(String initialInput) {
        logger.debug("execute called");
        logger.debug("execute initialInput={}", initialInput);

        if (tasks == null || tasks.isEmpty()) {
            logger.warn("execute no tasks configured");
            return;
        }

        String currentInput = initialInput;
        for (Task task : tasks) {
            logger.debug("execute processing task={}", task);
            Agent agent = task.getAgent();
            logger.debug("execute agent={}", agent);

            String output = agent.perform(task, currentInput);
            logger.debug("execute output={}", output);

            currentInput = output != null ? output : "";
        }
        logger.debug("execute completed");
    }
}
