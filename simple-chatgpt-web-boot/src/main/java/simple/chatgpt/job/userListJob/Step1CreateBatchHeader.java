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

@Component
public class Step1CreateBatchHeader extends StepExecutionListenerSupport implements Tasklet {

    private static final Logger logger = LogManager.getLogger(Step1CreateBatchHeader.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("Step1CreateBatchHeader beforeStep called");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.debug("Step1CreateBatchHeader execute called");

        // Example: create batch header ID and store in execution context
        Long batchId = System.currentTimeMillis();
        chunkContext.getStepContext().getStepExecution().getExecutionContext().put("BATCH_ID", batchId);

        logger.debug("Created batch header ID={}", batchId);
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Step5EncryptAndTransfer finished with status {}", stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }
}
