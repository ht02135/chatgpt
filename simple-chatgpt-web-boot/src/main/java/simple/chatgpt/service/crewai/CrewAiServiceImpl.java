package simple.chatgpt.service.crewai;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.gateway.crewai.CrewAiGateway;
import simple.chatgpt.pojo.crewai.Agent;
import simple.chatgpt.pojo.crewai.QAReviewRequest;
import simple.chatgpt.pojo.crewai.SupportInquiry;
import simple.chatgpt.pojo.crewai.Task;

/*
 hung: service implementation that converts inquiries/reviews into Tasks and delegates to Agents
 */
@Service("crewaiCrewAiService")
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
