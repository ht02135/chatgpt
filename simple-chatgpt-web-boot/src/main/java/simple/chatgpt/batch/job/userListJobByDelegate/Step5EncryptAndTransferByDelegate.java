package simple.chatgpt.batch.job.userListJobByDelegate;

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
        // NO context modifications here
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("afterStep called for Step5EncryptAndTransferByDelegate, status={}", stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }

    // ==================================================
    // TASKLET EXECUTE
    // ==================================================
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();

        jobRequest = getOneRecentJobRequestByParams(
                UserListJobConfig.JOB_NAME, 500, 1, JobRequest.STATUS_SUBMITTED);
        logger.debug("execute jobRequest={}", jobRequest);
        
        if (jobRequest == null) {
            logger.warn("No live JobRequest found");
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

            // ==== use helpers instead of direct stepData put & mapper update ====
            updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.CONTEXT_LIST_FILE_PATH, filePath);
            updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.DOWNLOAD_URL, fileName);

            // ==== mark JobRequest completed ====
            updateJobRequest(jobRequest, 1000, 1, JobRequest.STATUS_COMPLETED);

            logger.debug("###########");
            logger.debug("JobRequest updated to stage=1000 status=1 (completed)");
            logger.debug("JobRequest jobRequest={}", jobRequest);
            logger.debug("###########");

        } catch (Exception e) {
            logger.error("Error e={}", e);
            updateJobRequest(jobRequest, jobRequest.getProcessingStage(), 999, 
            	JobRequest.STATUS_FAILED, e.getMessage());
            throw e;
        }

        return RepeatStatus.FINISHED;
    }
}
