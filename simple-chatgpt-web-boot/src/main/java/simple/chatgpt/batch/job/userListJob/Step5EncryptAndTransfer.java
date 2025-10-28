package simple.chatgpt.batch.job.userListJob;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simple.chatgpt.batch.AbstractJobRequest;
import simple.chatgpt.batch.BatchJobConstants;
import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.service.batch.JobRequestService;

@Component
public class Step5EncryptAndTransfer extends AbstractJobRequest {

    private static final Logger logger = LogManager.getLogger(Step5EncryptAndTransfer.class);

    private final JobRequestService jobRequestService;
    private JobRequest jobRequest; // internal variable

    @Autowired
    public Step5EncryptAndTransfer(JobRequestMapper jobRequestMapper,
                                   JobRequestService jobRequestService) {
        super(jobRequestMapper);
        this.jobRequestService = jobRequestService;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("Step5EncryptAndTransfer beforeStep called");
        // NO context modifications here
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();

        // Initialize internal JobRequest
        jobRequest = jobRequestService.getOneRecentJobRequestByParams(
                UserListJobConfig.JOB_NAME, 500, 1, JobRequest.STATUS_SUBMITTED);
        logger.debug("execute jobRequest={}", jobRequest);
        
        if (jobRequest == null) {
            logger.warn("No live JobRequest found");
            return RepeatStatus.FINISHED;
        }

        try {
            // Fetch file path from internal JobRequest
            String filePath = jobRequest.getStepData() != null
                    ? (String) jobRequest.getStepData().get(BatchJobConstants.CONTEXT_LIST_FILE_PATH)
                    : null;

            if (filePath == null) {
                logger.warn("No CSV file path found in JobRequest.stepData. Skipping transfer.");
                return RepeatStatus.FINISHED;
            }

            logger.debug("Encrypting and transferring file={}", filePath);

            // TODO: implement actual PGP encryption + FTP transfer
            // Simulated step here, just for logging

            // ==================================================
            // Generate downloadable URL
            // ==================================================
            String fileName = jobRequest.getDownloadUrl();
            logger.debug("Generated fileName={}", fileName);

            // 2️⃣ Store URL also in JobRequest.stepData map
            Map<String, Object> stepData = jobRequest.getStepData() != null
                    ? new HashMap<>(jobRequest.getStepData())
                    : new HashMap<>();
            stepData.put(BatchJobConstants.CONTEXT_LIST_FILE_PATH, filePath);
            stepData.put(BatchJobConstants.DOWNLOAD_URL, fileName);

            // ==================================================
            // Flip JobRequest to stage=1000, status=1 (completed)
            // ==================================================
            jobRequest.setStepData(stepData);
            jobRequest.setProcessingStage(1000);
            jobRequest.setProcessingStatus(1);
            jobRequestService.update(jobRequest.getId(), jobRequest);
            logger.debug("###########");
            logger.debug("JobRequest updated to stage=1000 status=1 (completed)");
            logger.debug("JobRequest jobRequest={}", jobRequest);
            logger.debug("###########");

            // Persist info in ExecutionContext
            stepExecution.getJobExecution().getExecutionContext().put(BatchJobConstants.CONTEXT_LIST_FILE_PATH, filePath);
            stepExecution.getJobExecution().getExecutionContext().put(BatchJobConstants.DOWNLOAD_URL, fileName);

        } catch (Exception e) {
            logger.error("Error during encryption/transfer for jobRequest={}", jobRequest, e);

            // Flip JobRequest to FAILED
            jobRequest.setStatus(JobRequest.STATUS_FAILED);
            jobRequest.setErrorMessage(e.getMessage());
            try {
                jobRequestService.update(jobRequest.getId(), jobRequest);
                logger.debug("JobRequest updated to FAILED due to exception");
            } catch (Exception ex) {
                logger.error("Failed to update JobRequest to FAILED", ex);
            }
            throw e; // fail the step
        }

        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Step5EncryptAndTransfer finished with status {}", stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }
}
