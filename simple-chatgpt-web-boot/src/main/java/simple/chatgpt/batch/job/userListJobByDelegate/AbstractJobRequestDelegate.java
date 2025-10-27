package simple.chatgpt.batch.job.userListJobByDelegate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.mapper.management.UserManagementMapper;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.pojo.management.UserManagementPojo;

/*
hung:
Abstract base class for all delegate-based steps.
-------------------------------------------------
This base:
 - Extends StepExecutionListenerSupport and implements Tasklet
 - Provides MyBatis-based JobRequest lookups (no service)
 - Centralizes logging
 - Allows subclasses to only focus on their actual business logic
*/

@Component
public abstract class AbstractJobRequestDelegate extends StepExecutionListenerSupport implements org.springframework.batch.core.step.tasklet.Tasklet {

    private static final Logger logger = LogManager.getLogger(AbstractJobRequestDelegate.class);

    @Autowired
    protected JobRequestMapper jobRequestMapper;

    @Autowired
    protected UserManagementMapper userManagementMapper;

    // -----------------------------------------------------
    // JobRequest lookup methods
    // -----------------------------------------------------

    public List<JobRequest> getJobRequestByParams(Map<String, Object> params) {
        logger.debug("getJobRequestByParams called");
        logger.debug("getJobRequestByParams params={}", params);

        List<JobRequest> mappings = jobRequestMapper.search(params);
        logger.debug("getJobRequestByParams result count={}", (mappings != null ? mappings.size() : 0));
        logger.debug("getJobRequestByParams mappings={}", mappings);

        return mappings;
    }

    public JobRequest getOneRecentJobRequestByParams(String jobName,
                                                     Integer processingStage,
                                                     Integer processingStatus,
                                                     String status) {
        logger.debug("getOneRecentJobRequestByParams called");
        logger.debug("getOneRecentJobRequestByParams jobName={}", jobName);
        logger.debug("getOneRecentJobRequestByParams processingStage={}", processingStage);
        logger.debug("getOneRecentJobRequestByParams processingStatus={}", processingStatus);
        logger.debug("getOneRecentJobRequestByParams status={}", status);

        Map<String, Object> params = new HashMap<>();
        params.put("jobName", jobName);
        params.put("processingStage", processingStage);
        params.put("processingStatus", processingStatus);
        params.put("status", status);

        logger.debug("getOneRecentJobRequestByParams params={}", params);

        List<JobRequest> results = getJobRequestByParams(params);
        logger.debug("getOneRecentJobRequestByParams results size={}", (results != null ? results.size() : 0));
        logger.debug("getOneRecentJobRequestByParams results={}", results);

        if (results == null || results.isEmpty()) {
            logger.debug("getOneRecentJobRequestByParams no JobRequest found");
            return null;
        }

        JobRequest mostRecent = results.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(JobRequest::getUpdatedDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .findFirst()
                .orElse(null);

        logger.debug("getOneRecentJobRequestByParams mostRecent={}", mostRecent);
        return mostRecent;
    }

    // -----------------------------------------------------
    // UserManagement lookup convenience methods
    // -----------------------------------------------------

    public List<UserManagementPojo> getUserByParams(Map<String, Object> params) {
        logger.debug("getUserByParams called with params={}", params);
        List<UserManagementPojo> mappings = userManagementMapper.search(params);
        logger.debug("getUserByParams result count={}", (mappings != null ? mappings.size() : 0));
        return mappings;
    }

    public List<UserManagementPojo> getAllUsers() {
        logger.debug("getAllUsers called");
        Map<String, Object> params = new HashMap<>();
        List<UserManagementPojo> users = getUserByParams(params);
        logger.debug("getAllUsers result count={}", (users != null ? users.size() : 0));
        return users;
    }
}
