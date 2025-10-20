package simple.chatgpt.pojo.openai;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
	
public class CrewController {
    private static final Logger logger = LogManager.getLogger(CrewController.class);

    private final List<Task> tasks;

    public CrewController(List<Task> tasks) {
        logger.debug("CrewController constructor tasks={}", tasks);
        this.tasks = tasks;
    }

    public void execute(String initialInput) {
        logger.debug("execute called initialInput={}", initialInput);
        String currentInput = initialInput;
        for (Task task : tasks) {
            Agent agent = task.getAgent();
            logger.debug("execute: starting task={} by agent={}", task, agent);
            String output = agent.perform(task, currentInput);
            logger.debug("execute: output from agent={} output={}", agent, output);
            currentInput = output;
        }
        logger.debug("execute completed");
    }
}
