package simple.chatgpt.service.crewai;

import simple.chatgpt.pojo.crewai.QAReviewRequest;
import simple.chatgpt.pojo.crewai.SupportInquiry;

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
