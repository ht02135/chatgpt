package simple.chatgpt.batch.step.userList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
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

import simple.chatgpt.batch.job.userList.UserListJobConfig;
import simple.chatgpt.batch.step.AbstractJobRequest;
import simple.chatgpt.batch.step.BatchJobConstants;
import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.service.batch.JobRequestService;
import simple.chatgpt.service.management.file.UserListFileService;

/*
hung: step 4 - generate user list CSV file
*/

@Component
public class Step4GenerateCsv extends AbstractJobRequest {

    private static final Logger logger = LogManager.getLogger(Step4GenerateCsv.class);

    private final UserListFileService listFileService;
    private final JobRequestService jobRequestService;

    private JobRequest jobRequest;

    @Autowired
    public Step4GenerateCsv(JobRequestMapper jobRequestMapper,
                            UserListFileService listFileService,
                            JobRequestService jobRequestService) {
        super(jobRequestMapper);
        this.listFileService = listFileService;
        this.jobRequestService = jobRequestService;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("Step4GenerateCsv beforeStep called");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Step4GenerateCsv finished with status {}", stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();

        // Initialize internal JobRequest
        jobRequest = getOneRecentJobRequestByParams(
                UserListJobConfig.JOB_NAME, 400, 1, JobRequest.STATUS_SUBMITTED);
        logger.debug("execute jobRequest={}", jobRequest);

        if (jobRequest == null) {
            logger.warn("No live JobRequest found");
            return RepeatStatus.FINISHED;
        }

        Map<String, Object> stepData = jobRequest.getStepData();
        if (stepData == null) {
            logger.warn("JobRequest.stepData is null. Skipping CSV generation.");
            return RepeatStatus.FINISHED;
        }

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

        // Ensure parent directory exists
        File csvFile = Paths.get(userListFilePath).toFile();
        File parentDir = csvFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            logger.debug("CSV parent directory creation: {}", created ? parentDir.getAbsolutePath() : "FAILED");
        }

        try (OutputStream fos = new FileOutputStream(csvFile)) {
            Map<String, Object> params = Map.of(
                    "listId", listId,
                    "outputStream", fos
            );

            listFileService.exportListToCsv(params);
            logger.debug("CSV successfully generated at {}", csvFile.getAbsolutePath());

            // === use helper methods for updating JobRequest ===
            updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.CONTEXT_LIST_FILE_PATH, userListFilePath);
            updateJobRequest(jobRequest, 500, 1, JobRequest.STATUS_SUBMITTED);

            // Persist file path in ExecutionContext
            stepExecution.getJobExecution().getExecutionContext()
                    .put(BatchJobConstants.CONTEXT_LIST_FILE_PATH, userListFilePath);

        } catch (Exception e) {
            logger.error("Error e={}", e);
            updateJobRequest(jobRequest, jobRequest.getProcessingStage(), 999, 
            	JobRequest.STATUS_FAILED, e.getMessage());
            throw e;
        }

        return RepeatStatus.FINISHED;
    }
}
