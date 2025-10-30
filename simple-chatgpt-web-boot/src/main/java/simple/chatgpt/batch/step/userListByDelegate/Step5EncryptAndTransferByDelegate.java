package simple.chatgpt.batch.step.userListByDelegate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simple.chatgpt.batch.job.userList.UserListJobConfig;
import simple.chatgpt.batch.step.BatchJobConstants;
import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.mapper.management.UserManagementMapper;
import simple.chatgpt.pojo.batch.JobRequest;

@Component
public class Step5EncryptAndTransferByDelegate extends AbstractJobRequestByDelegateStep implements Tasklet {

    private static final Logger logger = LogManager.getLogger(Step5EncryptAndTransferByDelegate.class);

    private JobRequest jobRequest;

    @Autowired
    public Step5EncryptAndTransferByDelegate(JobRequestMapper jobRequestMapper,
                                             UserManagementMapper userManagementMapper) {
        super(jobRequestMapper, userManagementMapper);
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("beforeStep called");
        logger.debug("beforeStep stepExecution={}", stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("afterStep called");
        logger.debug("afterStep stepExecution={}", stepExecution);
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.debug("execute called");

        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        logger.debug("execute stepExecution={}", stepExecution);

        // Initialize internal JobRequest
        jobRequest = getOneRecentJobRequestByParams(
                UserListJobConfig.JOB_NAME, 500, 1, JobRequest.STATUS_SUBMITTED);
        logger.debug("execute jobRequest={}", jobRequest);
        
        if (jobRequest == null) {
            logger.warn("No live JobRequest found");
            return RepeatStatus.FINISHED;
        }

        try {
            // TODO: implement actual PGP encryption + FTP transfer
            String fileName = jobRequest.getDownloadUrl();
            logger.debug("Generated fileName={}", fileName);

            // ==== USE updateJobRequestStepData ====
            updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.DOWNLOAD_URL, fileName);

            // ==== USE updateJobRequest to mark completed ====
            updateJobRequest(jobRequest, 1000, 1, JobRequest.STATUS_COMPLETED);

        } catch (Exception e) {
            logger.error("Error e={}", e);
            updateJobRequest(jobRequest, jobRequest.getProcessingStage(), 999, 
            	JobRequest.STATUS_FAILED, e.getMessage());
            throw e;
        }

        return RepeatStatus.FINISHED;
    }

}
