package simple.chatgpt.batch.job.userListJobByDelegate;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import simple.chatgpt.batch.BatchJobConstants;
import simple.chatgpt.batch.job.userListJob.UserListJobConfig;
import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.mapper.management.UserManagementMapper;
import simple.chatgpt.pojo.batch.JobRequest;

@Component
public class Step5EncryptAndTransferByDelegate extends AbstractJobRequestDelegate implements Tasklet {

    private static final Logger logger = LogManager.getLogger(Step5EncryptAndTransferByDelegate.class);

    private StepExecution stepExecution;
    private JobRequest jobRequest;

    /**
     * Constructor calling superclass with required mappers
     */
    public Step5EncryptAndTransferByDelegate(JobRequestMapper jobRequestMapper,
                                             UserManagementMapper userManagementMapper) {
        super(jobRequestMapper, userManagementMapper);
    }

    public Step step5EncryptAndTransferByDelegate(StepBuilderFactory stepBuilderFactory) {
        logger.debug("step5EncryptAndTransferByDelegate called");
        return stepBuilderFactory.get("step5EncryptAndTransferByDelegate")
                .tasklet(this)
                .listener(this)
                .build();
    }

    // ==================================================
    // STEP LISTENER
    // ==================================================
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("beforeStep called for Step5EncryptAndTransferByDelegate");
        this.stepExecution = stepExecution;

        jobRequest = getOneRecentJobRequestByParams(
                UserListJobConfig.JOB_NAME, 500, 1, JobRequest.STATUS_SUBMITTED);
        logger.debug("Fetched JobRequest for encryption/transfer: {}", jobRequest);
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("afterStep called for Step5EncryptAndTransferByDelegate, status={}", stepExecution.getStatus());
        this.stepExecution = null;
        return stepExecution.getExitStatus();
    }

    // ==================================================
    // TASKLET EXECUTE
    // ==================================================
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        if (jobRequest == null) {
            logger.warn("No JobRequest to process. Skipping encryption/transfer.");
            return RepeatStatus.FINISHED;
        }

        try {
            String filePath = jobRequest.getStepData() != null
                    ? (String) jobRequest.getStepData().get(BatchJobConstants.CONTEXT_LIST_FILE_PATH)
                    : null;

            if (filePath == null) {
                logger.warn("No CSV file path found in JobRequest.stepData. Skipping transfer.");
                return RepeatStatus.FINISHED;
            }

            logger.debug("Encrypting and transferring file={}", filePath);

            // TODO: implement actual PGP encryption + FTP transfer

            String fileName = jobRequest.getDownloadUrl();
            logger.debug("Generated fileName={}", fileName);

            Map<String, Object> stepData = jobRequest.getStepData() != null
                    ? new HashMap<>(jobRequest.getStepData())
                    : new HashMap<>();
            stepData.put(BatchJobConstants.CONTEXT_LIST_FILE_PATH, filePath);
            stepData.put(BatchJobConstants.DOWNLOAD_URL, fileName);

            jobRequest.setStepData(stepData);
            jobRequest.setProcessingStage(1000);
            jobRequest.setProcessingStatus(1);
            jobRequest.setStatus(JobRequest.STATUS_COMPLETED);
            jobRequestMapper.update(jobRequest.getId(), jobRequest);
            logger.debug("###########");
            logger.debug("JobRequest updated to stage=1000 status=1 (completed)");
            logger.debug("JobRequest jobRequest={}", jobRequest);
            logger.debug("###########");

            stepExecution.getJobExecution().getExecutionContext()
                    .put(BatchJobConstants.CONTEXT_LIST_FILE_PATH, filePath);
            stepExecution.getJobExecution().getExecutionContext()
                    .put(BatchJobConstants.DOWNLOAD_URL, fileName);

        } catch (Exception e) {
            logger.error("Error during encryption/transfer for jobRequest={}", jobRequest, e);

            jobRequest.setStatus(JobRequest.STATUS_FAILED);
            jobRequest.setErrorMessage(e.getMessage());
            try {
                jobRequestMapper.update(jobRequest.getId(), jobRequest);
                logger.debug("JobRequest updated to FAILED due to exception");
            } catch (Exception ex) {
                logger.error("Failed to update JobRequest to FAILED", ex);
            }
            throw e;
        }

        return RepeatStatus.FINISHED;
    }
}
