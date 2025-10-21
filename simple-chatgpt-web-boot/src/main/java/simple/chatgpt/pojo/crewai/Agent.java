package simple.chatgpt.pojo.crewai;

import org.apache.ibatis.type.Alias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import simple.chatgpt.gateway.crewai.CrewAiGateway;

/*
 hung: simple agent that delegates work to CrewAiGateway
 */
@Alias("crewaiAgent")		// for MyBatis    
public class Agent {
    private static final Logger logger = LogManager.getLogger(Agent.class);

    private final String name;
    private final String role;
    private final String goal;
    private final String backstory;

    // Gateway to CrewAI / AMP API
    private final CrewAiGateway crewAiGateway;

    /*
     * hung: constructor - basic with name
     */
    public Agent(String name, CrewAiGateway crewAiGateway) {
        logger.debug("Agent constructor called (name)");
        logger.debug("Agent name={}", name);
        logger.debug("Agent crewAiGateway={}", crewAiGateway);

        this.name = name;
        this.role = null;
        this.goal = null;
        this.backstory = null;
        this.crewAiGateway = crewAiGateway;

        logger.debug("Agent this={}", this);
    }

    /*
     * hung: constructor - detailed with role/goal/backstory
     */
    public Agent(String name, String role, String goal, String backstory, CrewAiGateway crewAiGateway) {
        logger.debug("Agent constructor called (detailed)");
        logger.debug("Agent name={}", name);
        logger.debug("Agent role={}", role);
        logger.debug("Agent goal={}", goal);
        logger.debug("Agent backstory={}", backstory);
        logger.debug("Agent crewAiGateway={}", crewAiGateway);

        this.name = name;
        this.role = role;
        this.goal = goal;
        this.backstory = backstory;
        this.crewAiGateway = crewAiGateway;

        logger.debug("Agent this={}", this);
    }

    // getters
    public String getName() { logger.debug("getName called"); return name; }
    public String getRole() { logger.debug("getRole called"); return role; }
    public String getGoal() { logger.debug("getGoal called"); return goal; }
    public String getBackstory() { logger.debug("getBackstory called"); return backstory; }

    /*
     * hung: perform a Task with no extra input
     */
    public void perform(Task task) {
        logger.debug("perform called (void)"); 
        logger.debug("perform task={}", task);

        String result = perform(task, "");
        logger.debug("perform (void) completed result={}", result);
    }

    /*
     * hung: perform a Task with input and return the result
     */
    public String perform(Task task, String input) {
        logger.debug("perform called (with input)");
        logger.debug("perform task={}", task);
        logger.debug("perform input={}", input);

        String actor = role != null ? role : name;
        logger.debug("perform actor={}", actor);
        logger.debug("perform crewAiGateway={}", crewAiGateway);

        try {
            // build JSON inputs for CrewAiGateway
            String jsonInputs = String.format(
                    "{ \"agent\": \"%s\", \"description\": \"%s\", \"input\": \"%s\" }",
                    escapeJson(actor),
                    escapeJson(task.getDescription()),
                    escapeJson(input)
            );
            logger.debug("perform jsonInputs={}", jsonInputs);

            // kickoff task via CrewAiGateway
            String kickoffId = crewAiGateway.kickoff(task.getDescription(), actor, jsonInputs);
            logger.debug("perform kickoffId={}", kickoffId);

            // optionally fetch status
            String status = crewAiGateway.getStatus(kickoffId);
            logger.debug("perform status={}", status);

            return status;
        } catch (Exception e) {
            logger.error("Agent {} failed performing task={}", actor, task, e);
            return "[ERROR: Agent failed to perform task]";
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
    }

    @Override
    public String toString() {
        return "Agent{" +
                "name='" + name + '\'' +
                (role != null ? ", role='" + role + '\'' : "") +
                (goal != null ? ", goal='" + goal + '\'' : "") +
                '}';
    }
}
