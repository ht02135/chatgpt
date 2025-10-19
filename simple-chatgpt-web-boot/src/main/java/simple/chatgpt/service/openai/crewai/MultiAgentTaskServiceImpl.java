package simple.chatgpt.service.openai.crewai;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import simple.chatgpt.pojo.openai.crewai.Agent;
import simple.chatgpt.pojo.openai.crewai.AgentRegistry;
import simple.chatgpt.pojo.openai.crewai.MultiAgentController;
import simple.chatgpt.pojo.openai.crewai.Task;
import simple.chatgpt.pojo.openai.crewai.TaskQueue;

@Service
public class MultiAgentTaskServiceImpl implements MultiAgentTaskService {

    private static final Logger logger = LogManager.getLogger(MultiAgentTaskServiceImpl.class);

    /*
     * hung: default workflow execution (previously main)
     */
    @Override
    public void executeMultiAgentWorkflow() {
        logger.debug("executeMultiAgentWorkflow called");

        TaskQueue taskQueue = new TaskQueue();
        logger.debug("executeMultiAgentWorkflow taskQueue={}", taskQueue);

        AgentRegistry agentRegistry = new AgentRegistry();
        logger.debug("executeMultiAgentWorkflow agentRegistry={}", agentRegistry);

        // register agents first
        Agent agent1 = new Agent("Agent-Alpha");
        logger.debug("executeMultiAgentWorkflow agent1={}", agent1);
        
        Agent agent2 = new Agent("Agent-Beta");
        logger.debug("executeMultiAgentWorkflow agent2={}", agent2);

        // create tasks
        List<Task> tasks = Arrays.asList(
            new Task(agent1, "Process data segment A", "Output A"),
            new Task(agent2, "Analyze image set B", "Output B"),
            new Task(agent1, "Generate summary report C", "Output C")
        );
        logger.debug("executeMultiAgentWorkflow tasks={}", tasks);

        // register agents
        agentRegistry.register(agent1);
        agentRegistry.register(agent2);
        logger.debug("executeMultiAgentWorkflow agentRegistry after registration={}", agentRegistry);

        // enqueue tasks
        taskQueue.enqueue(tasks);
        logger.debug("executeMultiAgentWorkflow taskQueue after enqueue={}", taskQueue);

        // run system
        MultiAgentController controller = new MultiAgentController(agentRegistry, taskQueue);
        logger.debug("executeMultiAgentWorkflow controller={}", controller);

        controller.executeAll();
        logger.debug("executeMultiAgentWorkflow completed");
    }
}
