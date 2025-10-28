package simple.chatgpt.batch.job.userListJobByDelegate;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simple.chatgpt.batch.BatchJobConstants;
import simple.chatgpt.batch.job.userListJob.UserListJobConfig;
import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.mapper.management.UserManagementListMapper;
import simple.chatgpt.mapper.management.UserManagementMapper;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.pojo.management.UserManagementListPojo;

/*
hung:
Step1CreateBatchHeaderByDelegate
This version uses MyBatis mappers directly (no service layer).
- JobRequestMapper for fetching/updating JobRequest
- UserManagementListMapper for inserting user list header
*/

@Component
public class Step1CreateBatchHeaderByDelegate extends AbstractJobRequestDelegate {

    private static final Logger logger = LogManager.getLogger(Step1CreateBatchHeaderByDelegate.class);

    private final SqlSessionFactory sqlSessionFactory;
    private final UserManagementListMapper userManagementListMapper;

    @Autowired
    public Step1CreateBatchHeaderByDelegate(JobRequestMapper jobRequestMapper,
                                            UserManagementMapper userManagementMapper,
                                            SqlSessionFactory sqlSessionFactory,
                                            UserManagementListMapper userManagementListMapper) {
        super(jobRequestMapper, userManagementMapper);
        this.sqlSessionFactory = sqlSessionFactory;
        this.userManagementListMapper = userManagementListMapper;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("beforeStep called");
        logger.debug("beforeStep stepExecution={}", stepExecution);
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.debug("execute called");
        logger.debug("execute param contribution={}", contribution);
        logger.debug("execute param chunkContext={}", chunkContext);

        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        logger.debug("execute stepExecution={}", stepExecution);

        // ==========================================
        // STEP 1: Fetch JobRequest via mapper (stage=100, status=SUBMITTED)
        // ==========================================
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("jobName", UserListJobByDelegateConfig.JOB_NAME);
        queryParams.put("processingStage", 100);
        queryParams.put("processingStatus", 1);
        queryParams.put("status", JobRequest.STATUS_SUBMITTED);

        logger.debug("execute queryParams={}", queryParams);
        JobRequest jobRequest = getOneRecentJobRequestByParams(
            UserListJobConfig.JOB_NAME, 100, 1, JobRequest.STATUS_SUBMITTED);
        logger.debug("execute jobRequest={}", jobRequest);

        if (jobRequest == null) {
            logger.error("No live JobRequest found for jobName={}", UserListJobByDelegateConfig.JOB_NAME);
            return RepeatStatus.FINISHED;
        }

        // ==================================================
        // STEP 2: Create new UserManagementListPojo
        // ==================================================
        UserManagementListPojo userList = new UserManagementListPojo();
        logger.debug("execute userList initialized={}", userList);

        userList.setUserListName(BatchJobConstants.DEFAULT_USER_LIST_NAME);

        String timestamp = LocalDateTime.now().format(BatchJobConstants.TIMESTAMP_FORMATTER);
        String fileNameWithTimestamp = String.format(BatchJobConstants.USER_LIST_FILENAME_PATTERN, timestamp);
        jobRequest.setDownloadUrl(fileNameWithTimestamp);
        
        String fullFilePath = Paths.get(BatchJobConstants.USER_LIST_BASE_DIR, fileNameWithTimestamp).toString();

        userList.setFilePath(fullFilePath);
        userList.setDescription(BatchJobConstants.DEFAULT_DESCRIPTION);

        logger.debug("execute userList filePath={}", userList.getFilePath());

        // ==================================================
        // STEP 3: Insert userList via MyBatis mapper
        // ==================================================
        userManagementListMapper.create(userList);
        logger.debug("execute userList inserted userList={}", userList);

        // ==================================================
        // STEP 4: Update JobRequest with list info
        // ==================================================
        Map<String, Object> stepData = jobRequest.getStepData();
        if (stepData == null) {
            stepData = new HashMap<>();
        }

        stepData.put(BatchJobConstants.CONTEXT_LIST_ID, userList.getId());
        stepData.put(BatchJobConstants.CONTEXT_LIST_NAME, userList.getUserListName());
        stepData.put(BatchJobConstants.CONTEXT_LIST_FILE_PATH, userList.getFilePath());
        jobRequest.setStepData(stepData);

        jobRequest.setProcessingStage(200);
        jobRequest.setProcessingStatus(1);

        jobRequestMapper.update(jobRequest.getId(), jobRequest);
        logger.debug("execute updated jobRequest={}", jobRequest);

        // ==================================================
        // STEP 5: Store to ExecutionContext
        // ==================================================
        stepExecution.getJobExecution().getExecutionContext().putLong(BatchJobConstants.CONTEXT_LIST_ID, userList.getId());
        stepExecution.getJobExecution().getExecutionContext().putString(BatchJobConstants.CONTEXT_LIST_NAME, userList.getUserListName());
        stepExecution.getJobExecution().getExecutionContext().putString(BatchJobConstants.CONTEXT_LIST_FILE_PATH, userList.getFilePath());

        logger.debug("execute ExecutionContext updated with list info");

        logger.debug("execute finished");
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("afterStep called");
        logger.debug("afterStep stepExecution={}", stepExecution);
        logger.debug("afterStep finished with ExitStatus={}", stepExecution.getExitStatus());
        return stepExecution.getExitStatus();
    }
}
