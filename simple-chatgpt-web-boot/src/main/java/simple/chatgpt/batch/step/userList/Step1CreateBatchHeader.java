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
import simple.chatgpt.batch.step.AbstractJobRequest;
import simple.chatgpt.batch.step.BatchJobConstants;
import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.service.batch.JobRequestService;
import simple.chatgpt.service.management.UserManagementListService;

@Component
public class Step1CreateBatchHeader extends AbstractJobRequest {

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
        logger.debug("Step1CreateBatchHeader beforeStep called");
        // NO context modifications here
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();

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
        logger.debug("execute userList={}", userList);

        userList.setUserListName(BatchJobConstants.DEFAULT_USER_LIST_NAME);

        // Generate timestamped filename
        String timestamp = LocalDateTime.now().format(BatchJobConstants.TIMESTAMP_FORMATTER);
        String fileNameWithTimestamp = String.format(BatchJobConstants.USER_LIST_FILENAME_PATTERN, timestamp);
        jobRequest.setDownloadUrl(fileNameWithTimestamp);
        
        String fullFilePath = Paths.get(BatchJobConstants.USER_LIST_BASE_DIR, fileNameWithTimestamp).toString();
        userList.setFilePath(fullFilePath);
        logger.debug("execute userList filePath set to {}", fullFilePath);

        userList.setDescription(BatchJobConstants.DEFAULT_DESCRIPTION);

        // Persist userList
        UserManagementListPojo createdList = userManagementListService.create(userList);
        logger.debug("execute createdList={}", createdList);

        // ==================================================
        // STEP 3: Add userList info to JobRequest stepData
        // ==================================================
        updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.CONTEXT_LIST_ID, createdList.getId());
        updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.CONTEXT_LIST_NAME, createdList.getUserListName());
        updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.CONTEXT_LIST_FILE_PATH, createdList.getFilePath());

        // ==================================================
        // STEP 4: Update JobRequest stage to 200 / status=1
        // ==================================================
        updateJobRequest(jobRequest, 200, 1, JobRequest.STATUS_SUBMITTED);

        // ==================================================
        // STEP 5: Done
        // ==================================================
        logger.debug("Step1CreateBatchHeader execute finished");
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Step1CreateBatchHeader afterStep called");
        logger.debug("afterStep stepExecution={}", stepExecution);
        logger.debug("Step1CreateBatchHeader finished with status {}", stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }
}
