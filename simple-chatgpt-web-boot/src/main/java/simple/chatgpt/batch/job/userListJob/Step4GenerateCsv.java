package simple.chatgpt.batch.job.userListJob;

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
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

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

    // Default directory where CSV files will be created
    private static final String CSV_OUTPUT_DIR = "/tmp/user_lists";

    public Step4GenerateCsv(UserManagementListService listService) {
        this.listService = listService;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("Step4GenerateCsv starting");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();

        Long listId = stepExecution.getJobExecution().getExecutionContext().getLong("LIST_ID");
        if (listId == null) {
            logger.warn("No LIST_ID found in JobExecutionContext, skipping CSV generation");
            return RepeatStatus.FINISHED;
        }

        logger.debug("Generating CSV for listId={}", listId);

        // Ensure output directory exists
        File dir = new File(CSV_OUTPUT_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            logger.debug("CSV output directory created: {}", created ? dir.getAbsolutePath() : "FAILED");
        }

        // Create CSV file
        String fileName = "user_list_" + listId + ".csv";
        File csvFile = Paths.get(CSV_OUTPUT_DIR, fileName).toFile();

        try (OutputStream fos = new FileOutputStream(csvFile)) {
            Map<String, Object> params = Map.of(
                    "listId", listId,
                    "outputStream", fos
            );

            listService.exportListToCsv(params);
            logger.debug("CSV successfully generated at {}", csvFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Error generating CSV for listId={}", listId, e);
            throw e; // fail the step so the job can handle it
        }

        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Step4GenerateCsv finished with status {}", stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }
}
