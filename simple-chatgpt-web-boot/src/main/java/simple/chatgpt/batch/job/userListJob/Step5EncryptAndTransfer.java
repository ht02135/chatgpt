package simple.chatgpt.batch.job.userListJob;

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
public class Step5EncryptAndTransfer extends StepExecutionListenerSupport implements Tasklet {
    private static final Logger logger = LogManager.getLogger(Step5EncryptAndTransfer.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("Step5EncryptAndTransfer starting");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Long batchId = chunkContext.getStepContext().getStepExecution()
                                    .getJobExecution()
                                    .getExecutionContext()
                                    .getLong("BATCH_ID");
        logger.debug("Encrypting and transferring files for batchId={}", batchId);

        // TODO: implement PGP encryption + FTP transfer logic
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Step5EncryptAndTransfer finished with status {}", stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }
}
