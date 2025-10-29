package simple.chatgpt.batch.step.userList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simple.chatgpt.batch.job.userList.UserListJobConfig;
import simple.chatgpt.batch.step.AbstractJobRequestStep;
import simple.chatgpt.batch.step.BatchJobConstants;
import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.service.batch.JobRequestService;

@Component
public class Step5EncryptAndTransfer extends AbstractJobRequestStep {

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
            // TODO: implement actual PGP encryption + FTP transfer
            // Simulated step here, just for logging

            // ==================================================
            // Generate downloadable URL
            // ==================================================
            String fileName = jobRequest.getDownloadUrl();
            logger.debug("Generated fileName={}", fileName);

            // ==== USE updateJobRequestStepData ====
            updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.DOWNLOAD_URL, fileName);

            // ==== USE updateJobRequest to mark completed ====
            updateJobRequest(jobRequest, 1000, 1, JobRequest.STATUS_COMPLETED);

            logger.debug("###########");
            logger.debug("JobRequest updated to stage=1000 status=1 (completed)");
            logger.debug("JobRequest jobRequest={}", jobRequest);
            logger.debug("###########");

        } catch (Exception e) {
            logger.error("Error e={}", e);
            updateJobRequest(jobRequest, jobRequest.getProcessingStage(), 999, 
            	JobRequest.STATUS_FAILED, e.getMessage());
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
