package simple.chatgpt.service.openai.crewai2;

import simple.chatgpt.pojo.openai.crewai2.QAReviewRequest;
import simple.chatgpt.pojo.openai.crewai2.SupportInquiry;

/*
 hung: service interface to wrap CrewAiSupportClient API calls
 */
public interface CrewAiService {

    /**
     * hung: kickoff an inquiry resolution task
     */
    String kickoffInquiryResolution(SupportInquiry inquiry) throws Exception;

    /**
     * hung: kickoff a QA review task
     */
    String kickoffQualityAssuranceReview(String originalKickoffId, QAReviewRequest reviewRequest) throws Exception;

    /**
     * hung: get status of a task by kickoffId
     */
    String getStatus(String kickoffId) throws Exception;
}
