package simple.chatgpt.service.crewai;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import simple.chatgpt.gateway.crewai.CrewAiGateway;
import simple.chatgpt.pojo.crewai.Agent;
import simple.chatgpt.pojo.crewai.AgentRegistry;
import simple.chatgpt.pojo.crewai.ParallelCrewExecutor;
import simple.chatgpt.pojo.crewai.QAReviewRequest;
import simple.chatgpt.pojo.crewai.SupportInquiry;
import simple.chatgpt.pojo.crewai.Task;
import simple.chatgpt.pojo.crewai.TaskQueue;
/*
 hung: parallel service using ParallelCrewExecutor
 */
@Service("crewaiParallelCrewAiService")
public class ParallelCrewAiServiceImpl implements CrewAiService {
    private static final Logger logger = LogManager.getLogger(ParallelCrewAiServiceImpl.class);

    private final CrewAiGateway gateway;
    private final AgentRegistry agentRegistry;
    private final TaskQueue taskQueue;

    @Autowired
    public ParallelCrewAiServiceImpl(
    	CrewAiGateway gateway, 
    	@Qualifier("crewaiAgentRegistry") AgentRegistry agentRegistry, 
    	@Qualifier("crewaiTaskQueue") TaskQueue taskQueue) 
    {
        logger.debug("ParallelCrewAiServiceImpl constructor called");
        logger.debug("ParallelCrewAiServiceImpl gateway={}", gateway);
        logger.debug("ParallelCrewAiServiceImpl agentRegistry={}", agentRegistry);
        logger.debug("ParallelCrewAiServiceImpl taskQueue={}", taskQueue);

        this.gateway = gateway;
        this.agentRegistry = agentRegistry;
        this.taskQueue = taskQueue;
    }

    @Override
    public String kickoffInquiryResolution(SupportInquiry inquiry) throws Exception {
        logger.debug("kickoffInquiryResolution called");

        // Wrap inquiry as a Task with an Agent
        Agent agent = new Agent("support_agent", gateway);
        Task task = new Task(agent, "inquiry_resolution", inquiry.getMessage());

        taskQueue.enqueue(task);

        ParallelCrewExecutor executor = new ParallelCrewExecutor(agentRegistry, taskQueue);
        executor.executeAll();

        return "[Parallel tasks submitted]";
    }

    @Override
    public String kickoffQualityAssuranceReview(String originalKickoffId, QAReviewRequest reviewRequest)
            throws Exception {
        logger.debug("kickoffQualityAssuranceReview called");

        Agent agent = new Agent("support_quality_assurance_agent", gateway);
        Task task = new Task(agent, "quality_assurance_review", reviewRequest.getCriteria());

        // Optionally include original kickoffId as input
        taskQueue.enqueue(task);

        ParallelCrewExecutor executor = new ParallelCrewExecutor(agentRegistry, taskQueue);
        executor.executeAll();

        return "[Parallel tasks submitted]";
    }

    @Override
    public String getStatus(String kickoffId) throws Exception {
        logger.debug("getStatus called");
        return gateway.getStatus(kickoffId);
    }
}
