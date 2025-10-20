package simple.chatgpt.service.openai.crewai2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.gateway.openai.crewai2.CrewAiGateway;
import simple.chatgpt.pojo.openai.crewai2.Agent;
import simple.chatgpt.pojo.openai.crewai2.QAReviewRequest;
import simple.chatgpt.pojo.openai.crewai2.SupportInquiry;
import simple.chatgpt.pojo.openai.crewai2.Task;

/*
 hung: service implementation that converts inquiries/reviews into Tasks and delegates to Agents
 */
@Service
public class CrewAiServiceImpl implements CrewAiService {
    private static final Logger logger = LogManager.getLogger(CrewAiServiceImpl.class);

    private final CrewAiGateway gateway;

    @Autowired
    public CrewAiServiceImpl(CrewAiGateway gateway) {
        logger.debug("CrewAiServiceImpl constructor called");
        logger.debug("CrewAiServiceImpl gateway={}", gateway);
        this.gateway = gateway;
    }

    @Override
    public String kickoffInquiryResolution(SupportInquiry inquiry) throws Exception {
        Agent agent = new Agent("support_agent", gateway);
        Task task = new Task(agent, "inquiry_resolution", inquiry.getMessage());
        // explicitly use the version returning a result
        String result = agent.perform(task, "");
        return result;
    }

    @Override
    public String kickoffQualityAssuranceReview(String originalKickoffId, QAReviewRequest reviewRequest) throws Exception {
        Agent agent = new Agent("support_quality_assurance_agent", gateway);
        Task task = new Task(agent, "quality_assurance_review", reviewRequest.getCriteria());
        String result = agent.perform(task, originalKickoffId); // pass kickoffId as input
        return result;
    }

    @Override
    public String getStatus(String kickoffId) throws Exception {
        logger.debug("getStatus called");
        logger.debug("getStatus kickoffId={}", kickoffId);

        String status = gateway.getStatus(kickoffId);
        logger.debug("getStatus status={}", status);

        return status;
    }
}
