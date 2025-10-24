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
public class Step4GenerateCsv extends StepExecutionListenerSupport implements Tasklet {
    private static final Logger logger = LogManager.getLogger(Step4GenerateCsv.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("Step4GenerateCsv starting");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Long batchId = chunkContext.getStepContext().getStepExecution()
                                    .getJobExecution()
                                    .getExecutionContext()
                                    .getLong("BATCH_ID");
        logger.debug("Generating CSV for batchId={}", batchId);

        // TODO: implement CSV generation logic using user_management_list_member data
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Step5EncryptAndTransfer finished with status {}", stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }
}
