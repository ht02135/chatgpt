package simple.chatgpt.pojo.crewai;

import java.util.List;

import org.apache.ibatis.type.Alias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/*
 hung: sequential executor - runs tasks in order
 */
@Alias("crewaiSequentialCrewExecutor")		// for MyBatis    
public class SequentialCrewExecutor {
    private static final Logger logger = LogManager.getLogger(SequentialCrewExecutor.class);

    private List<Task> tasks;

    public SequentialCrewExecutor(List<Task> tasks) {
        logger.debug("SequentialCrewExecutor constructor called");
        logger.debug("SequentialCrewExecutor tasks={}", tasks);
        this.tasks = tasks;
        logger.debug("SequentialCrewExecutor this={}", this);
    }

    private static void init() {
        logger.debug("init called");
    }

    public void setTasks(List<Task> tasks) {
        logger.debug("setTasks called");
        logger.debug("setTasks tasks={}", tasks);
        this.tasks = tasks;
    }

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
