package simple.chatgpt.batch.job.userListJob;

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
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.service.batch.JobRequestService;
import simple.chatgpt.service.management.UserManagementListService;

/*
| Step                    | Type    | Reason                             |
| ----------------------- | ------- | ---------------------------------- |
| Step1CreateBatchHeader  | Tasklet | Single DB insert, one-off          |
| Step2LoadUsers          | Chunk   | Multiple records, DB read/write    |
| Step3PopulateUserList   | Chunk   | Multiple records, DB insert        |
| Step4GenerateCsv        | Tasklet | One-off file creation              |
| Step5EncryptAndTransfer | Tasklet | One-off file encryption & transfer |
*/

@Component
public class Step4GenerateCsv extends StepExecutionListenerSupport implements Tasklet {

    private static final Logger logger = LogManager.getLogger(Step4GenerateCsv.class);

    private final UserManagementListService listService;
    private final JobRequestService jobRequestService;

    private static final String CSV_OUTPUT_DIR = "/tmp/user_lists";

    private JobRequest jobRequest;

    public Step4GenerateCsv(UserManagementListService listService,
                            JobRequestService jobRequestService) {
        this.listService = listService;
        this.jobRequestService = jobRequestService;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("Step4GenerateCsv beforeStep called");

        // Fetch JobRequest 400/1/SUBMITTED
        jobRequest = jobRequestService.getOneRecentJobRequestByParams(
        	UserListJobConfig.JOB_NAME, 400, 1, JobRequest.STATUS_SUBMITTED);
        logger.debug("Fetched JobRequest for CSV generation: {}", jobRequest);

        if (jobRequest != null) {
            // save JobRequest in context
            stepExecution.getJobExecution().getExecutionContext().put(UserListJobConfig.CONTEXT_JOB_REQUEST, jobRequest);
            logger.debug("JobRequest saved to JobExecutionContext");
        } else {
            logger.warn("No JobRequest found with stage=400 status=1 SUBMITTED. CSV generation will be skipped.");
        }
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();

        if (jobRequest == null) {
            logger.warn("No JobRequest to process. Skipping CSV generation.");
            return RepeatStatus.FINISHED;
        }

        Long listId = jobRequest.getStepData() != null ? (Long) jobRequest.getStepData().get(UserListJobConfig.CONTEXT_LIST_ID) : null;
        if (listId == null) {
            logger.warn("No LIST_ID found in JobRequest.stepData, skipping CSV generation");
            return RepeatStatus.FINISHED;
        }

        logger.debug("Generating CSV for listId={}", listId);

        // Ensure output directory exists
        File dir = new File(CSV_OUTPUT_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            logger.debug("CSV output directory created: {}", created ? dir.getAbsolutePath() : "FAILED");
        }

        String fileName = "user_list_" + listId + ".csv";
        File csvFile = Paths.get(CSV_OUTPUT_DIR, fileName).toFile();

        try (OutputStream fos = new FileOutputStream(csvFile)) {
            Map<String, Object> params = Map.of(
                    "listId", listId,
                    "outputStream", fos
            );

            listService.exportListToCsv(params);
            logger.debug("CSV successfully generated at {}", csvFile.getAbsolutePath());

            // Save filename in JobRequest.stepData
            Map<String, Object> stepData = jobRequest.getStepData() != null
                    ? new HashMap<>(jobRequest.getStepData())
                    : new HashMap<>();
            stepData.put("CSV_FILE_NAME", fileName);
            jobRequest.setStepData(stepData);
            logger.debug("Saved CSV fileName={} into JobRequest.stepData", fileName);

            // Advance stage to next step
            jobRequest.setProcessingStage(500); // next stage
            jobRequest.setProcessingStatus(1);
            jobRequestService.update(jobRequest.getId(), jobRequest);

            // Update context
            stepExecution.getJobExecution().getExecutionContext().put(UserListJobConfig.CONTEXT_JOB_REQUEST, jobRequest);
            logger.debug("Updated JobRequest in JobExecutionContext");

        } catch (Exception e) {
            logger.error("Error generating CSV for listId={}", listId, e);
            // flip JobRequest to FAILED
            jobRequest.setStatus(JobRequest.STATUS_FAILED);
            jobRequest.setErrorMessage(e.getMessage());
            try {
                jobRequestService.update(jobRequest.getId(), jobRequest);
                logger.debug("JobRequest updated to FAILED due to exception");
            } catch (Exception ex) {
                logger.error("Failed to update JobRequest to FAILED", ex);
            }
            throw e; // fail step
        }

        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Step4GenerateCsv finished with status {}", stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }
}
