package simple.chatgpt.pojo.openai.crewai2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import simple.chatgpt.gateway.openai.crewai2.CrewAiGateway;

/*
 hung: simple agent that delegates work to CrewAiController
 */
public class Agent {
    private static final Logger logger = LogManager.getLogger(Agent.class);

    private final String name;
    private final String role;
    private final String goal;
    private final String backstory;

    // Gateway to CrewAI / AMP API
    private final CrewAiGateway crewAiController;

    /*
     * hung: constructor - basic with name
     */
    public Agent(String name, CrewAiGateway crewAiController) {
        logger.debug("Agent constructor called (name)");
        logger.debug("Agent name={}", name);
        logger.debug("Agent crewAiController={}", crewAiController);

        this.name = name;
        this.role = null;
        this.goal = null;
        this.backstory = null;
        this.crewAiController = crewAiController;

        logger.debug("Agent this={}", this);
    }

    /*
     * hung: constructor - detailed with role/goal/backstory
     */
    public Agent(String name, String role, String goal, String backstory, CrewAiGateway crewAiController) {
        logger.debug("Agent constructor called (detailed)");
        logger.debug("Agent name={}", name);
        logger.debug("Agent role={}", role);
        logger.debug("Agent goal={}", goal);
        logger.debug("Agent backstory={}", backstory);
        logger.debug("Agent crewAiController={}", crewAiController);

        this.name = name;
        this.role = role;
        this.goal = goal;
        this.backstory = backstory;
        this.crewAiController = crewAiController;

        logger.debug("Agent this={}", this);
    }

    private static void init() {
        logger.debug("init called");
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
        logger.debug("perform called (void)"); // method entry
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
        logger.debug("perform crewAiController={}", crewAiController);

        try {
            // create a minimal JSON inputs payload for CrewAiController
            String jsonInputs = String.format(
            	    "{\n" +
            	    "  \"agent\": \"%s\",\n" +
            	    "  \"description\": \"%s\",\n" +
            	    "  \"input\": \"%s\"\n" +
            	    "}",
            	    escapeJson(actor),
            	    escapeJson(task.getDescription()),
            	    escapeJson(input)
            	);
            
            logger.debug("perform jsonInputs={}", jsonInputs);

            String kickoffId = crewAiController.kickoff(task.getDescription(), actor, jsonInputs);
            logger.debug("perform kickoffId={}", kickoffId);

            // optionally poll status and fetch result (simplified)
            String status = crewAiController.getStatus(kickoffId);
            logger.debug("perform status={}", status);

            // In real system parse status; here return status string as result
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
