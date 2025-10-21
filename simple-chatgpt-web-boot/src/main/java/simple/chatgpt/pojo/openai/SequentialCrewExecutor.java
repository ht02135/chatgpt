package simple.chatgpt.pojo.openai;

import java.util.List;

import org.apache.ibatis.type.Alias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/*
 hung: sequential executor - runs tasks in order
 */
@Alias("openaiSequentialCrewExecutor")		// for MyBatis    
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
    
    public String execute(String initialInput) {
        logger.debug("execute called");
        logger.debug("execute initialInput={}", initialInput);

        if (tasks == null || tasks.isEmpty()) {
            logger.warn("execute no tasks configured");
            return "";
        }

        String currentInput = initialInput;
        for (Task task : tasks) {
            logger.debug("execute processing task={}", task);
            Agent agent = task.getAgent();
            logger.debug("execute agent={}", agent);

            String output = agent.perform(task, currentInput);
            logger.debug("execute output={}", output);

            /*
             * hung: Pass the previous agent’s output to the next agent — 
             * but if it’s null, use an empty string instead.
             */
            currentInput = output != null ? output : "";
        }

        logger.debug("execute completed");
        logger.debug("execute returning currentInput={}", currentInput);
        return currentInput;
    }

}
