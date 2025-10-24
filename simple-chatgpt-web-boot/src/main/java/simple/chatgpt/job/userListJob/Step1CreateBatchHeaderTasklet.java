package simple.chatgpt.job.userListJob;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class Step1CreateBatchHeaderTasklet implements Tasklet {
    private static final Logger logger = LogManager.getLogger(Step1CreateBatchHeaderTasklet.class);

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.debug("Step1CreateBatchHeaderTasklet started");

        // Example logic
        Long batchId = System.currentTimeMillis(); // pretend insert and get ID
        chunkContext.getStepContext().getStepExecution().getExecutionContext().put("BATCH_ID", batchId);

        logger.debug("Created batch header ID={}", batchId);
        return RepeatStatus.FINISHED;
    }
}