package simple.chatgpt.batch.step.userList;

import java.nio.file.Paths;
import java.time.LocalDateTime;

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
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.service.batch.JobRequestService;
import simple.chatgpt.service.management.UserManagementListService;

@Component
public class Step1CreateBatchHeader extends AbstractJobRequestStep {

    private static final Logger logger = LogManager.getLogger(Step1CreateBatchHeader.class);

    private final UserManagementListService userManagementListService;
    private final JobRequestService jobRequestService;

    @Autowired
    public Step1CreateBatchHeader(JobRequestMapper jobRequestMapper,
                                  UserManagementListService userManagementListService,
                                  JobRequestService jobRequestService) {
        super(jobRequestMapper);
        this.userManagementListService = userManagementListService;
        this.jobRequestService = jobRequestService;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("beforeStep called");
        logger.debug("afterStep stepExecution={}", stepExecution);
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.debug("execute called");

        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        logger.debug("execute stepExecution={}", stepExecution);
        
        // ==========================================
        // STEP 1: Fetch live JobRequest (stage=100, status=SUBMITTED)
        // ==========================================
        JobRequest jobRequest = getOneRecentJobRequestByParams(
        	UserListJobConfig.JOB_NAME, 100, 1, JobRequest.STATUS_SUBMITTED);
        logger.debug("execute jobRequest={}", jobRequest);

        if (jobRequest == null) {
            logger.error("No live JobRequest found");
            return RepeatStatus.FINISHED;
        }

        // ==================================================
        // STEP 2: Create new UserManagementList
        // ==================================================
        UserManagementListPojo userList = new UserManagementListPojo();
        userList.setUserListName(BatchJobConstants.DEFAULT_USER_LIST_NAME);

        // Generate timestamped filename
        String timestamp = LocalDateTime.now().format(BatchJobConstants.TIMESTAMP_FORMATTER);
        String fileNameWithTimestamp = String.format(BatchJobConstants.USER_LIST_FILENAME_PATTERN, timestamp);
        jobRequest.setDownloadUrl(fileNameWithTimestamp);
        
        String fullFilePath = Paths.get(BatchJobConstants.USER_LIST_BASE_DIR, fileNameWithTimestamp).toString();
        userList.setOriginalFileName(fileNameWithTimestamp);
        userList.setFilePath(fullFilePath);
        userList.setDescription(BatchJobConstants.DEFAULT_DESCRIPTION);
        logger.debug("execute fileNameWithTimestamp={}", fileNameWithTimestamp);
        logger.debug("execute fullFilePath={}", fullFilePath);

        // ==================================================
        // STEP 3: Insert userList
        // ==================================================
        UserManagementListPojo createdList = userManagementListService.create(userList);
        logger.debug("execute createdList={}", createdList);

        // ==================================================
        // STEP 4: Update JobRequest stepData via helper method
        // ==================================================
        updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.CONTEXT_LIST_ID, createdList.getId());
        updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.CONTEXT_LIST_NAME, createdList.getUserListName());
        updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.CONTEXT_LIST_FILE_PATH, createdList.getFilePath());

        // ==================================================
        // STEP 5: Update JobRequest stage/status
        // ==================================================
        updateJobRequest(jobRequest, 200, 1, JobRequest.STATUS_SUBMITTED);

        // ==================================================
        // STEP 5: Done
        // ==================================================
        logger.debug("execute finished");
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("afterStep called");
        logger.debug("afterStep stepExecution={}", stepExecution);
        return stepExecution.getExitStatus();
    }
}
