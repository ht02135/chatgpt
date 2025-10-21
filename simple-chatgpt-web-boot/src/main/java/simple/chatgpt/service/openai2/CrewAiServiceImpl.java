package simple.chatgpt.service.openai2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;

import simple.chatgpt.pojo.crewai.QAReviewRequest;
import simple.chatgpt.pojo.crewai.SupportInquiry;
import simple.chatgpt.pojo.openai.Agent;
import simple.chatgpt.pojo.openai.AgentRegistry;
import simple.chatgpt.pojo.openai.ParallelCrewExecutor;
import simple.chatgpt.pojo.openai.Task;
import simple.chatgpt.pojo.openai.TaskQueue;

/*
 * hung: Spring-managed implementation of CrewAiService
 * Agents now produce meaningful outputs captured in executor
 */
@Service("openaiCrewAiService")
public class CrewAiServiceImpl implements CrewAiService {

    private static final Logger logger = LogManager.getLogger(CrewAiServiceImpl.class);

    private final OpenAIClient client;
    private final AgentRegistry agentRegistry;
    private final TaskQueue taskQueue;
    private final ParallelCrewExecutor executor;
    private final Map<String, String> taskResults = new HashMap<>();

    @Autowired
    public CrewAiServiceImpl(OpenAIClient client) {
        logger.debug("CrewAiServiceImpl constructor called");
        logger.debug("CrewAiServiceImpl client param={}", client);

        this.client = client;
        this.agentRegistry = new AgentRegistry();
        this.taskQueue = new TaskQueue();
        this.executor = new ParallelCrewExecutor(agentRegistry, taskQueue);

        logger.debug("CrewAiServiceImpl agentRegistry initialized");
        logger.debug("CrewAiServiceImpl taskQueue initialized");
        logger.debug("CrewAiServiceImpl executor initialized");

        initAgents();
    }

    private void initAgents() {
        logger.debug("initAgents called");

        Agent supportAgent = new Agent("SupportAgent", client);
        Agent qaAgent = new Agent("QualityReviewer", client);

        agentRegistry.register(supportAgent);
        agentRegistry.register(qaAgent);

        logger.debug("initAgents supportAgent registered={}", supportAgent);
        logger.debug("initAgents qaAgent registered={}", qaAgent);
    }

    @Override
    public String kickoffInquiryResolution(SupportInquiry inquiry) throws Exception {
        logger.debug("kickoffInquiryResolution called");
        logger.debug("kickoffInquiryResolution inquiry={}", inquiry);

        Agent agent = agentRegistry.getAgents().stream()
                .filter(a -> a.getName().equalsIgnoreCase("SupportAgent"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("kickoffInquiryResolution agent={}", agent);

        Task task = new Task(agent,
                "Resolve customer inquiry: " + inquiry.getMessage(),
                "Response and resolution for customer " + inquiry.getCustomerId());
        logger.debug("kickoffInquiryResolution task={}", task);

        taskQueue.enqueue(Collections.singletonList(task));
        Map<Task, String> results = executor.executeAllWithResults();

        String result = results.get(task);
        logger.debug("kickoffInquiryResolution result={}", result);

        String kickoffId = UUID.randomUUID().toString();
        taskResults.put(kickoffId, result);
        logger.debug("kickoffInquiryResolution kickoffId={}", kickoffId);

        return kickoffId;
    }

    @Override
    public String kickoffQualityAssuranceReview(String originalKickoffId, QAReviewRequest reviewRequest) throws Exception {
        logger.debug("kickoffQualityAssuranceReview called");
        logger.debug("kickoffQualityAssuranceReview originalKickoffId={}", originalKickoffId);
        logger.debug("kickoffQualityAssuranceReview reviewRequest={}", reviewRequest);

        String originalResult = taskResults.get(originalKickoffId);
        if (originalResult == null) {
            throw new IllegalStateException("No original task found for ID: " + originalKickoffId);
        }

        Agent qaAgent = agentRegistry.getAgents().stream()
                .filter(a -> a.getName().equalsIgnoreCase("QualityReviewer"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("kickoffQualityAssuranceReview qaAgent={}", qaAgent);

        Task reviewTask = new Task(qaAgent,
                "Perform QA review of previous result: " + reviewRequest.getCriteria(),
                "QA review and feedback summary");
        logger.debug("kickoffQualityAssuranceReview reviewTask={}", reviewTask);

        taskQueue.enqueue(Collections.singletonList(reviewTask));
        Map<Task, String> reviewResults = executor.executeAllWithResults();

        String reviewResult = reviewResults.get(reviewTask);
        logger.debug("kickoffQualityAssuranceReview reviewResult={}", reviewResult);

        String newKickoffId = UUID.randomUUID().toString();
        taskResults.put(newKickoffId, reviewResult);
        logger.debug("kickoffQualityAssuranceReview newKickoffId={}", newKickoffId);

        return newKickoffId;
    }

    @Override
    public String getStatus(String kickoffId) throws Exception {
        logger.debug("getStatus called");
        logger.debug("getStatus kickoffId={}", kickoffId);

        String result = taskResults.get(kickoffId);
        if (result == null) return "NOT_FOUND";

        return "COMPLETED: " + result;
    }
}
