package simple.chatgpt.job.userListJob;

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
        logger.debug("Generating CSV for listId={}", listId);

        if (listId != null) {
            // TODO: call listService.exportListToCsv with OutputStream
            // Example placeholder:
            // listService.exportListToCsv(Map.of("listId", listId, "outputStream", myOutputStream));
            logger.debug("CSV generation logic would go here");
        } else {
            logger.debug("No LIST_ID found in JobExecutionContext, skipping CSV generation");
        }

        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Step4GenerateCsv finished with status {}", stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }
}
