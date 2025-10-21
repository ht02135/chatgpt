package simple.chatgpt.pojo.crewai;

import org.apache.ibatis.type.Alias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/*
 hung: represents a single assignment for an Agent
 */
@Alias("crewaiTask")		// for MyBatis    
public class Task {
    private static final Logger logger = LogManager.getLogger(Task.class);

    private final Agent agent;
    private final String description;
    private final String expectedOutput;

    public Task(Agent agent, String description, String expectedOutput) {
        logger.debug("Task constructor called");
        logger.debug("Task agent={}", agent);
        logger.debug("Task description={}", description);
        logger.debug("Task expectedOutput={}", expectedOutput);

        this.agent = agent;
        this.description = description;
        this.expectedOutput = expectedOutput;

        logger.debug("Task this={}", this);
    }

    public Agent getAgent() {
        logger.debug("getAgent called");
        return agent;
    }

    public String getDescription() {
        logger.debug("getDescription called");
        return description;
    }

    public String getExpectedOutput() {
        logger.debug("getExpectedOutput called");
        return expectedOutput;
    }

    @Override
    public String toString() {
        return "Task{" + "agent=" + agent + ", description='" + description + '\'' + '}';
    }
}
