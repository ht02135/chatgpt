package simple.chatgpt.service.openai.crewai2;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.gateway.openai.crewai2.CrewAiGateway;
import simple.chatgpt.pojo.openai.crewai2.Agent;
import simple.chatgpt.pojo.openai.crewai2.QAReviewRequest;
import simple.chatgpt.pojo.openai.crewai2.SequentialCrewExecutor;
import simple.chatgpt.pojo.openai.crewai2.SupportInquiry;
import simple.chatgpt.pojo.openai.crewai2.Task;

/*
 hung: sequential CrewAiService implementation using SequentialCrewExecutor
 */
@Service
public class SequentialCrewAiServiceImpl implements CrewAiService {
    private static final Logger logger = LogManager.getLogger(SequentialCrewAiServiceImpl.class);

    private final CrewAiGateway gateway;

    @Autowired
    public SequentialCrewAiServiceImpl(CrewAiGateway gateway) {
        logger.debug("SequentialCrewAiServiceImpl constructor called");
        logger.debug("SequentialCrewAiServiceImpl gateway={}", gateway);
        this.gateway = gateway;
    }

    @Override
    public String kickoffInquiryResolution(SupportInquiry inquiry) throws Exception {
        logger.debug("kickoffInquiryResolution called");

        Agent agent = new Agent("support_agent", gateway);
        Task task = new Task(agent, "inquiry_resolution", inquiry.getMessage());

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        SequentialCrewExecutor executor = new SequentialCrewExecutor(tasks);
        executor.execute("");

        return task.getExpectedOutput();
    }

    @Override
    public String kickoffQualityAssuranceReview(String originalKickoffId, QAReviewRequest reviewRequest)
            throws Exception {
        logger.debug("kickoffQualityAssuranceReview called");

        Agent agent = new Agent("support_quality_assurance_agent", gateway);
        Task task = new Task(agent, "quality_assurance_review", reviewRequest.getCriteria());

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        SequentialCrewExecutor executor = new SequentialCrewExecutor(tasks);
        executor.execute(originalKickoffId);

        return task.getExpectedOutput();
    }

    @Override
    public String getStatus(String kickoffId) throws Exception {
        logger.debug("getStatus called");
        String status = gateway.getStatus(kickoffId);
        logger.debug("getStatus status={}", status);
        return status;
    }
}
