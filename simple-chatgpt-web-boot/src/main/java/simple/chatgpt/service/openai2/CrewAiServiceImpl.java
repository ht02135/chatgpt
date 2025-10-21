package simple.chatgpt.service.openai2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
 * Only Agents directly call the OpenAI API.
 */
@Service
public class CrewAiServiceImpl implements CrewAiService {

    private static final Logger logger = LogManager.getLogger(CrewAiServiceImpl.class);

    private final OpenAIClient client;
    private final AgentRegistry agentRegistry;
    private final TaskQueue taskQueue;
    private final ParallelCrewExecutor executor;

    private final Map<String, String> taskResults = new HashMap<>();

    /*
     * hung: constructor-based dependency injection of OpenAIClient
     */
    public CrewAiServiceImpl(OpenAIClient client) {
        logger.debug("CrewAiServiceImpl constructor called");
        logger.debug("CrewAiServiceImpl client param={}", client);

        this.client = client;

        // initialize registry, queue, and executor
        this.agentRegistry = new AgentRegistry();
        this.taskQueue = new TaskQueue();
        this.executor = new ParallelCrewExecutor(agentRegistry, taskQueue);

        logger.debug("CrewAiServiceImpl agentRegistry initialized");
        logger.debug("CrewAiServiceImpl taskQueue initialized");
        logger.debug("CrewAiServiceImpl executor initialized");

        // register agents
        initAgents();
    }

    /*
     * hung: register default agents
     */
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

        if (inquiry == null) {
            logger.warn("kickoffInquiryResolution inquiry is null");
            throw new IllegalArgumentException("inquiry cannot be null");
        }

        Agent agent = agentRegistry.getAgents().stream()
                .filter(a -> a.getName() != null && a.getName().equalsIgnoreCase("SupportAgent"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("kickoffInquiryResolution agent={}", agent);

        String description = "Resolve customer inquiry: " + inquiry.getMessage();
        String expectedOutput = "Response and resolution for customer " + inquiry.getCustomerId();

        Task task = new Task(agent, description, expectedOutput);
        logger.debug("kickoffInquiryResolution task={}", task);

        // enqueue and execute
        taskQueue.enqueue(Collections.singletonList(task));
        executor.executeAll();

        // perform actual API call via agent
        String result = agent.perform(task, inquiry.getMessage());
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

        if (originalKickoffId == null || originalKickoffId.isEmpty()) {
            logger.warn("kickoffQualityAssuranceReview originalKickoffId invalid");
            throw new IllegalArgumentException("originalKickoffId cannot be null or empty");
        }
        if (reviewRequest == null) {
            logger.warn("kickoffQualityAssuranceReview reviewRequest is null");
            throw new IllegalArgumentException("reviewRequest cannot be null");
        }

        String originalResult = taskResults.get(originalKickoffId);
        logger.debug("kickoffQualityAssuranceReview originalResult={}", originalResult);

        if (originalResult == null) {
            logger.warn("kickoffQualityAssuranceReview no result found for kickoffId={}", originalKickoffId);
            throw new IllegalStateException("No original task found for ID: " + originalKickoffId);
        }

        Agent qaAgent = agentRegistry.getAgents().stream()
                .filter(a -> a.getName() != null && a.getName().equalsIgnoreCase("QualityReviewer"))
                .findFirst()
                .orElse(agentRegistry.getAgents().get(0));
        logger.debug("kickoffQualityAssuranceReview qaAgent={}", qaAgent);

        String description = "Perform QA review of previous result: " + reviewRequest.getCriteria();
        String expectedOutput = "QA review and feedback summary";

        Task reviewTask = new Task(qaAgent, description, expectedOutput);
        logger.debug("kickoffQualityAssuranceReview reviewTask={}", reviewTask);

        taskQueue.enqueue(Collections.singletonList(reviewTask));
        executor.executeAll();

        String reviewResult = qaAgent.perform(reviewTask, originalResult);
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

        if (kickoffId == null || kickoffId.isEmpty()) {
            logger.warn("getStatus kickoffId invalid");
            throw new IllegalArgumentException("kickoffId cannot be null or empty");
        }

        String result = taskResults.get(kickoffId);
        logger.debug("getStatus result={}", result);

        if (result == null) {
            logger.debug("getStatus task not found for kickoffId={}", kickoffId);
            return "NOT_FOUND";
        }

        return "COMPLETED: " + result;
    }
}
