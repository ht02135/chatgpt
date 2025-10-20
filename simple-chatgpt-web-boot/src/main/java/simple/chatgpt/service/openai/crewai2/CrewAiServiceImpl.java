package simple.chatgpt.service.openai.crewai2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import simple.chatgpt.gateway.openai.crewai2.CrewAiGateway;
import simple.chatgpt.pojo.openai.crewai2.QAReviewRequest;
import simple.chatgpt.pojo.openai.crewai2.SupportInquiry;

/*
 hung: service implementation that delegates to CrewAiGateway
 */
public class CrewAiServiceImpl implements CrewAiService {
    private static final Logger logger = LogManager.getLogger(CrewAiServiceImpl.class);

    private final CrewAiGateway gateway;

    public CrewAiServiceImpl(CrewAiGateway gateway) {
        logger.debug("CrewAiServiceImpl constructor called");
        logger.debug("CrewAiServiceImpl gateway={}", gateway);
        this.gateway = gateway;
    }

    @Override
    public String kickoffInquiryResolution(SupportInquiry inquiry) throws Exception {
        logger.debug("CrewAiServiceImpl kickoffInquiryResolution called");
        logger.debug("CrewAiServiceImpl inquiry={}", inquiry);

        // build minimal JSON inputs object
        String jsonInputs = String.format(
                "{ \"customer_id\": \"%s\", \"customer_message\": \"%s\" }",
                escapeJson(inquiry.getCustomerId()),
                escapeJson(inquiry.getMessage())
        );
        logger.debug("CrewAiServiceImpl jsonInputs={}", jsonInputs);

        String kickoffId = gateway.kickoff("inquiry_resolution", "support_agent", jsonInputs);
        logger.debug("CrewAiServiceImpl kickoffId={}", kickoffId);
        return kickoffId;
    }

    @Override
    public String kickoffQualityAssuranceReview(String originalKickoffId, QAReviewRequest reviewRequest) throws Exception {
        logger.debug("CrewAiServiceImpl kickoffQualityAssuranceReview called");
        logger.debug("CrewAiServiceImpl originalKickoffId={}", originalKickoffId);
        logger.debug("CrewAiServiceImpl reviewRequest={}", reviewRequest);

        String jsonInputs = String.format(
                "{ \"original_kickoff_id\": \"%s\", \"criteria\": \"%s\" }",
                escapeJson(originalKickoffId),
                escapeJson(reviewRequest.getCriteria())
        );
        logger.debug("CrewAiServiceImpl jsonInputs={}", jsonInputs);

        String kickoffId = gateway.kickoff("quality_assurance_review", "support_quality_assurance_agent", jsonInputs);
        logger.debug("CrewAiServiceImpl kickoffId={}", kickoffId);
        return kickoffId;
    }

    @Override
    public String getStatus(String kickoffId) throws Exception {
        logger.debug("CrewAiServiceImpl getStatus called");
        logger.debug("CrewAiServiceImpl kickoffId={}", kickoffId);

        String status = gateway.getStatus(kickoffId);
        logger.debug("CrewAiServiceImpl status={}", status);
        return status;
    }

    // small helper
    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
    }
}
