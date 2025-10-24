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
public class Step2LoadUsers extends StepExecutionListenerSupport implements Tasklet {
    private static final Logger logger = LogManager.getLogger(Step2LoadUsers.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("Step2LoadUsers starting");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // Example: load users from DB or other source
        Long batchId = chunkContext.getStepContext().getStepExecution()
                                    .getJobExecution()
                                    .getExecutionContext()
                                    .getLong("BATCH_ID");
        logger.debug("Loading users for batchId={}", batchId);

        // TODO: implement actual user loading logic
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Step5EncryptAndTransfer finished with status {}", stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }
}
