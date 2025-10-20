package simple.chatgpt.service.openai.crewai2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.gateway.openai.crewai2.CrewAiGateway;
import simple.chatgpt.pojo.openai.crewai2.Agent;
import simple.chatgpt.pojo.openai.crewai2.AgentRegistry;
import simple.chatgpt.pojo.openai.crewai2.ParallelCrewExecutor;
import simple.chatgpt.pojo.openai.crewai2.Task;
import simple.chatgpt.pojo.openai.crewai2.TaskQueue;

/*
 hung: parallel customer outreach service using ParallelCrewExecutor
 */
@Service
public class CustomerOutreachServiceImpl implements CustomerOutreachService {
    private static final Logger logger = LogManager.getLogger(CustomerOutreachServiceImpl.class);

    private final CrewAiGateway gateway;
    private final AgentRegistry agentRegistry;
    private final TaskQueue taskQueue;

    @Autowired
    public CustomerOutreachServiceImpl(CrewAiGateway gateway, AgentRegistry agentRegistry, TaskQueue taskQueue) {
        logger.debug("CustomerOutreachServiceImpl constructor called");
        logger.debug("CustomerOutreachServiceImpl gateway={}", gateway);
        logger.debug("CustomerOutreachServiceImpl agentRegistry={}", agentRegistry);
        logger.debug("CustomerOutreachServiceImpl taskQueue={}", taskQueue);

        this.gateway = gateway;
        this.agentRegistry = agentRegistry;
        this.taskQueue = taskQueue;
    }

    @Override
    public String kickoffVenueTasks() throws Exception {
        logger.debug("kickoffVenueTasks called");

        Agent venueAgent = new Agent(
                "venue_coordinator",
                "Coordinator",
                "Find best venue options for event",
                "Handles venue discovery and management",
                gateway
        );
        Task venueTask = new Task(venueAgent, "Collect top 3 venue options", "List of venue options with address and capacity");

        taskQueue.enqueue(venueTask);

        ParallelCrewExecutor executor = new ParallelCrewExecutor(agentRegistry, taskQueue);
        executor.executeAll();

        return "[Venue tasks submitted]";
    }

    @Override
    public String kickoffLogisticsTasks() throws Exception {
        logger.debug("kickoffLogisticsTasks called");

        Agent logisticAgent = new Agent(
                "logistic_manager",
                "Logistics Manager",
                "Manage transport, catering, and schedules",
                "Coordinates operational aspects",
                gateway
        );
        Task logisticTask = new Task(logisticAgent, "Plan transport and catering", "Transport schedule and catering plan");

        taskQueue.enqueue(logisticTask);

        ParallelCrewExecutor executor = new ParallelCrewExecutor(agentRegistry, taskQueue);
        executor.executeAll();

        return "[Logistics tasks submitted]";
    }

    @Override
    public String kickoffMarketingTasks() throws Exception {
        logger.debug("kickoffMarketingTasks called");

        Agent marketingAgent = new Agent(
                "marketing_agent",
                "Marketing Specialist",
                "Promote event and send updates",
                "Handles marketing and customer communication",
                gateway
        );
        Task marketingTask = new Task(marketingAgent, "Prepare marketing campaign", "Email campaign and social media posts");

        taskQueue.enqueue(marketingTask);

        ParallelCrewExecutor executor = new ParallelCrewExecutor(agentRegistry, taskQueue);
        executor.executeAll();

        return "[Marketing tasks submitted]";
    }

    @Override
    public String getStatus(String taskId) throws Exception {
        logger.debug("getStatus called");
        return gateway.getStatus(taskId);
    }
}
