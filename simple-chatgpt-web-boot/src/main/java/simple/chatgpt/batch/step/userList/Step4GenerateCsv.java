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
import simple.chatgpt.batch.step.AbstractJobRequestStep;
import simple.chatgpt.batch.step.BatchJobConstants;
import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.service.batch.JobRequestService;
import simple.chatgpt.service.management.file.UserListFileService;

@Component
public class Step4GenerateCsv extends AbstractJobRequestStep {

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
                UserListJobConfig.JOB_NAME, 400, 1, JobRequest.STATUS_SUBMITTED);
        logger.debug("execute jobRequest={}", jobRequest);

        if (jobRequest == null) {
            logger.warn("No live JobRequest found");
            return RepeatStatus.FINISHED;
        }

        Number listIdNum = (Number) stepExecution.getJobExecution()
                .getExecutionContext().get(BatchJobConstants.CONTEXT_LIST_ID);
        Long listId = (listIdNum != null) ? listIdNum.longValue() : null;
        String userListFilePath = (String) stepExecution.getJobExecution().getExecutionContext()
                .get(BatchJobConstants.CONTEXT_LIST_FILE_PATH);
        logger.debug("execute listId={}", listId);
        logger.debug("execute userListFilePath={}", userListFilePath);
        
        if (listId == null || userListFilePath == null || userListFilePath.isBlank()) {
            logger.warn("LIST_ID or LIST_FILE_PATH missing. Skipping CSV generation.");
            return RepeatStatus.FINISHED;
        }

        // Ensure parent directory exists
        File csvFile = Paths.get(userListFilePath).toFile();
        File parentDir = csvFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            logger.debug("CSV parent directory creation: {}", created ? parentDir.getAbsolutePath() : "FAILED");
        }
        /*
		hung : dont remove it
		csvFile=\\data\\user_lists\\generated_user_lists_20251029_1044.csv
		parentDir=\\data\\user_lists
        */
        logger.debug("execute csvFile={}", csvFile);
        logger.debug("execute parentDir={}", parentDir);

        try (OutputStream fos = new FileOutputStream(csvFile)) {
            Map<String, Object> params = Map.of(
                    "listId", listId,
                    "outputStream", fos
            );
            listFileService.exportCsvToFtp(listId, csvFile);

            // === use helper methods for updating JobRequest ===
            updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.CONTEXT_LIST_FILE_PATH, userListFilePath);

            // ==== USE updateJobRequest ====
            updateJobRequest(jobRequest, 500, 1, JobRequest.STATUS_SUBMITTED);
			
        } catch (Exception e) {
            logger.error("Error e={}", e);
            updateJobRequest(jobRequest, jobRequest.getProcessingStage(), 999, 
            	JobRequest.STATUS_FAILED, e.getMessage());
            throw e;
        }
        
        return RepeatStatus.FINISHED;
    }
}
