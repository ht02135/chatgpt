package simple.chatgpt.batch;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.stereotype.Component;

import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.pojo.batch.JobRequest;

@Component
public abstract class AbstractJobRequest extends StepExecutionListenerSupport implements Tasklet {

    private static final Logger logger = LogManager.getLogger(AbstractJobRequest.class);

    protected final JobRequestMapper jobRequestMapper;

    protected AbstractJobRequest(JobRequestMapper jobRequestMapper) {
        this.jobRequestMapper = jobRequestMapper;
    }

    // -----------------------------------------------------
    // JobRequest lookup methods
    // -----------------------------------------------------

    public List<JobRequest> getJobRequestByParams(Map<String, Object> params) {
        logger.debug("getJobRequestByParams called");
        logger.debug("getJobRequestByParams params={}", params);

        List<JobRequest> mappings = jobRequestMapper.search(params);
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

    // ============================
    // NEW METHODS
    // ============================

    public boolean existsJobRequest(String jobName,
                                    Integer processingStage,
                                    Integer processingStatus,
                                    String status) {
        logger.debug("existsJobRequest called");
        logger.debug("existsJobRequest jobName={}", jobName);
        logger.debug("existsJobRequest processingStage={}", processingStage);
        logger.debug("existsJobRequest processingStatus={}", processingStatus);
        logger.debug("existsJobRequest status={}", status);

        JobRequest jobRequest = getOneRecentJobRequestByParams(jobName, processingStage, processingStatus, status);
        boolean exists = (jobRequest != null);
        logger.debug("existsJobRequest exists={}", exists);
        return exists;
    }

    public void updateJobRequestStepData(JobRequest jobRequest,
                                         StepExecution stepExecution,
                                         String contextKey,
                                         Object value) {
        logger.debug("updateJobRequestStepData called");
        logger.debug("updateJobRequestStepData jobRequest={}", jobRequest);
        logger.debug("updateJobRequestStepData stepExecution={}", stepExecution);
        logger.debug("updateJobRequestStepData contextKey={}", contextKey);
        logger.debug("updateJobRequestStepData value={}", value);

        Map<String, Object> stepData = jobRequest.getStepData() != null
                ? new HashMap<>(jobRequest.getStepData())
                : new HashMap<>();

        stepData.put(contextKey, value);
        jobRequest.setStepData(stepData);
        jobRequestMapper.update(jobRequest.getId(), jobRequest);
        
        stepExecution.getJobExecution().getExecutionContext().put(contextKey, value);
    }
    
    /**
     * Update a JobRequest with given stage, status, and processingStatus.
     */
    public void updateJobRequest(JobRequest jobRequest,
                                 int processingStage,
                                 int processingStatus,
                                 String status) {
        logger.debug("updateJobRequest called");
        logger.debug("updateJobRequest jobRequest before update={}", jobRequest);
        logger.debug("updateJobRequest processingStage={}", processingStage);
        logger.debug("updateJobRequest processingStatus={}", processingStatus);
        logger.debug("updateJobRequest status={}", status);

        jobRequest.setProcessingStage(processingStage);
        jobRequest.setProcessingStatus(processingStatus);
        jobRequest.setStatus(status);

        jobRequestMapper.update(jobRequest.getId(), jobRequest);

        logger.debug("updateJobRequest jobRequest after update={}", jobRequest);
    }

    /**
     * Update a JobRequest with given stage, status, processingStatus, and error message.
     */
    public void updateJobRequest(JobRequest jobRequest,
                                 int processingStage,
                                 int processingStatus,
                                 String status,
                                 String errorMessage) {
        logger.debug("updateJobRequest (with errorMessage) called");
        logger.debug("updateJobRequest jobRequest before update={}", jobRequest);
        logger.debug("updateJobRequest processingStage={}", processingStage);
        logger.debug("updateJobRequest processingStatus={}", processingStatus);
        logger.debug("updateJobRequest status={}", status);
        logger.debug("updateJobRequest errorMessage={}", errorMessage);

        jobRequest.setProcessingStage(processingStage);
        jobRequest.setProcessingStatus(processingStatus);
        jobRequest.setStatus(status);
        jobRequest.setErrorMessage(errorMessage);

        jobRequestMapper.update(jobRequest.getId(), jobRequest);

        logger.debug("updateJobRequest jobRequest after update={}", jobRequest);
    }

}
