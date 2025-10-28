package simple.chatgpt.batch.job.userListJobByDelegate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simple.chatgpt.batch.BatchJobConstants;
import simple.chatgpt.batch.job.userListJob.UserListJobConfig;
import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.mapper.management.UserManagementListMapper;
import simple.chatgpt.mapper.management.UserManagementMapper;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.service.management.file.UserListFileService;

@Component
public class Step4GenerateCsvByDelegate extends AbstractJobRequestDelegate implements Tasklet {

    private static final Logger logger = LogManager.getLogger(Step4GenerateCsvByDelegate.class);

    private final UserListFileService listFileService;

    @Autowired
    private UserManagementListMapper listMapper;

    private JobRequest jobRequest;

    /**
     * Constructor injecting UserListFileService and passing required mappers to superclass
     */
    public Step4GenerateCsvByDelegate(UserListFileService listFileService,
                                      JobRequestMapper jobRequestMapper,
                                      UserManagementMapper userManagementMapper) {
        super(jobRequestMapper, userManagementMapper);
        this.listFileService = listFileService;
    }

    // ==================================================
    // STEP LISTENER
    // ==================================================
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("beforeStep called for Step4GenerateCsvByDelegate");
     // NO context modifications here
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("afterStep called for Step4GenerateCsvByDelegate, status={}", stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }

    // ==================================================
    // TASKLET EXECUTE
    // ==================================================
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    	StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
    	
        jobRequest = getOneRecentJobRequestByParams(
                UserListJobConfig.JOB_NAME, 400, 1, JobRequest.STATUS_SUBMITTED);
        logger.debug("execute jobRequest={}", jobRequest);
        
    	if (jobRequest == null) {
            logger.warn("No live JobRequest found");
            return RepeatStatus.FINISHED;
        }

        Map<String, Object> stepData = jobRequest.getStepData() != null
                ? new HashMap<>(jobRequest.getStepData())
                : new HashMap<>();

        Number listIdNum = (Number) stepExecution.getJobExecution()
                .getExecutionContext().get(BatchJobConstants.CONTEXT_LIST_ID);
        Long listId = (listIdNum != null) ? listIdNum.longValue() : null;

        String userListFilePath = (String) stepExecution.getJobExecution().getExecutionContext()
                .get(BatchJobConstants.CONTEXT_LIST_FILE_PATH);

        if (listId == null || userListFilePath == null || userListFilePath.isBlank()) {
            logger.warn("LIST_ID or LIST_FILE_PATH missing. Skipping CSV generation.");
            return RepeatStatus.FINISHED;
        }

        logger.debug("Generating CSV for listId={} at path={}", listId, userListFilePath);

        File csvFile = Paths.get(userListFilePath).toFile();
        File parentDir = csvFile.getParentFile();
        if (!parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            logger.debug("CSV parent directory created: {}", created ? parentDir.getAbsolutePath() : "FAILED");
        }

        try (OutputStream fos = new FileOutputStream(csvFile)) {
            Map<String, Object> params = Map.of(
                    "listId", listId,
                    "outputStream", fos
            );
            /*
            hung : dont remove it
            i could add the helper, but risk split this complex thing into 2 place
            i chose to jut localize in listSevice.
            */
            listFileService.exportListToCsv(params);
            logger.debug("CSV successfully generated at {}", csvFile.getAbsolutePath());

            stepData.put(BatchJobConstants.CONTEXT_LIST_FILE_PATH, userListFilePath);
            jobRequest.setStepData(stepData);
            jobRequest.setProcessingStage(500);
            jobRequest.setProcessingStatus(1);
            jobRequestMapper.update(jobRequest.getId(), jobRequest);
            logger.debug("###########");
            logger.debug("Step4GenerateCsvByDelegate updated jobRequest stage=500 status=1");
            logger.debug("Step4GenerateCsvByDelegate jobRequest={}", jobRequest);
            logger.debug("###########");

            stepExecution.getJobExecution().getExecutionContext()
                    .put(BatchJobConstants.CONTEXT_LIST_FILE_PATH, userListFilePath);

        } catch (Exception e) {
            logger.error("Error generating CSV for listId={}", listId, e);
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
