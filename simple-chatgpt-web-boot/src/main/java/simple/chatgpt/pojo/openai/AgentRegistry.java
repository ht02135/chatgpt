package simple.chatgpt.pojo.openai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.type.Alias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Alias("openaiAgentRegistry")		// for MyBatis    
@Component("openaiAgentRegistry")	// for Spring DI/autowire
public class AgentRegistry {
    private static final Logger logger = LogManager.getLogger(AgentRegistry.class);

    private final List<Agent> agents = new ArrayList<>();

    public void register(Agent agent) {
        logger.debug("register agent={}", agent);
        agents.add(agent);
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