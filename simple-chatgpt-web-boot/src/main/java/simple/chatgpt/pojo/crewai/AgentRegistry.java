package simple.chatgpt.pojo.crewai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.type.Alias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/*
 hung: simple registry for agents
 */
@Alias("crewaiAgentRegistry")		// for MyBatis    
@Component("crewaiAgentRegistry")	// for Spring DI/autowire
public class AgentRegistry {
    private static final Logger logger = LogManager.getLogger(AgentRegistry.class);

    private final List<Agent> agents = new ArrayList<>();

    public AgentRegistry() {
        logger.debug("AgentRegistry constructor called");
        logger.debug("AgentRegistry this={}", this);
    }

    public void register(Agent agent) {
        logger.debug("register called");
        logger.debug("register agent={}", agent);
        agents.add(agent);
        logger.debug("register completed agentCount={}", agents.size());
    }

    public Agent getAgentByName(String name) {
        logger.debug("getAgentByName called");
        logger.debug("getAgentByName name={}", name);
        for (Agent a : agents) {
            // note: not logging inside loop per param rule; whole obj logging ok
            if (a.getName() != null && a.getName().equals(name)) {
                logger.debug("getAgentByName found agent={}", a);
                return a;
            }
        }
        logger.debug("getAgentByName found no agent for name={}", name);
        return null;
    }

    public List<Agent> getAgents() {
        logger.debug("getAgents called");
        return Collections.unmodifiableList(agents);
    }

    @Override
    public String toString() {
        return "AgentRegistry{" + "agents=" + agents + '}';
    }
}
