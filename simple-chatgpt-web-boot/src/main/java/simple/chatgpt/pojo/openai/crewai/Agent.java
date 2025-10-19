package simple.chatgpt.pojo.openai.crewai;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Agent {
    private static final Logger logger = LogManager.getLogger(Agent.class);

    private final String name;
    private final String role;
    private final String goal;
    private final String backstory;

    /*
     * hung: simple constructor using only name
     */
    public Agent(String name) {
        this.name = name;
        this.role = null;
        this.goal = null;
        this.backstory = null;

        logger.debug("Agent constructor called (name) name={}", name);
    }

    /*
     * hung: detailed constructor with role, goal, backstory
     */
    public Agent(String role, String goal, String backstory) {
        this.name = null;
        this.role = role;
        this.goal = goal;
        this.backstory = backstory;

        logger.debug("Agent constructor called (role/goal/backstory) role={}, goal={}, backstory={}", role, goal, backstory);
    }

    // getters
    public String getName() {
        logger.debug("getName called");
        return name;
    }

    public String getRole() {
        logger.debug("getRole called");
        return role;
    }

    public String getGoal() {
        logger.debug("getGoal called");
        return goal;
    }

    public String getBackstory() {
        logger.debug("getBackstory called");
        return backstory;
    }

    /*
     * hung: simple perform (void) for tasks without input
     */
    public void perform(Task task) {
        logger.debug("perform called (simple) task={} by agent={}", task, name != null ? name : role);

        try {
            Thread.sleep(500);  // simulate work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Agent {} interrupted while performing {}", name != null ? name : role, task, e);
        }

        logger.debug("Agent {} completed task={}", name != null ? name : role, task);
    }

    /*
     * hung: advanced perform (with input) returning simulated result
     */
    public String perform(Task task, String input) {
        logger.debug("perform called (advanced) agent={}, task={}, input={}", this, task, input);
        String actor = role != null ? role : name;
        String result = String.format("[Agent %s generated output for task \"%s\" with input \"%s\"]",
                actor, task.getDescription(), input);
        logger.debug("perform result={}", result);
        return result;
    }

    @Override
    public String toString() {
        if (role != null) {
            return "Agent{" + "role='" + role + '\'' + '}';
        } else {
            return "Agent{" + "name='" + name + '\'' + '}';
        }
    }
}
