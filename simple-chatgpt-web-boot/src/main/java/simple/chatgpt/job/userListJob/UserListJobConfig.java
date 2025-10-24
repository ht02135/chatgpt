package simple.chatgpt.job.userListJob;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
| Step                    | Type    | Reason                             |
| ----------------------- | ------- | ---------------------------------- |
| Step1CreateBatchHeader  | Tasklet | Single DB insert, one-off          |
| Step2LoadUsers          | Chunk   | Multiple records, DB read/write    |
| Step3PopulateUserList   | Chunk   | Multiple records, DB insert        |
| Step4GenerateCsv        | Tasklet | One-off file creation              |
| Step5EncryptAndTransfer | Tasklet | One-off file encryption & transfer |
*/

@Configuration
public class UserListJobConfig {

    private static final Logger logger = LogManager.getLogger(UserListJobConfig.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final Step1CreateBatchHeader step1CreateBatchHeader;
    private final Step2LoadUsersChunk step2LoadUsersChunk;
    private final Step3PopulateUserListChunk step3PopulateUserListChunk;
    private final Step4GenerateCsv step4GenerateCsv;
    private final Step5EncryptAndTransfer step5EncryptAndTransfer;

    public UserListJobConfig(JobBuilderFactory jobBuilderFactory,
                             StepBuilderFactory stepBuilderFactory,
                             Step1CreateBatchHeader step1CreateBatchHeader,
                             Step2LoadUsersChunk step2LoadUsersChunk,
                             Step3PopulateUserListChunk step3PopulateUserListChunk,
                             Step4GenerateCsv step4GenerateCsv,
                             Step5EncryptAndTransfer step5EncryptAndTransfer) {
        logger.debug("UserListJobConfig initialized");
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.step1CreateBatchHeader = step1CreateBatchHeader;
        this.step2LoadUsersChunk = step2LoadUsersChunk;
        this.step3PopulateUserListChunk = step3PopulateUserListChunk;
        this.step4GenerateCsv = step4GenerateCsv;
        this.step5EncryptAndTransfer = step5EncryptAndTransfer;
    }

    @Bean
    public Job userListJob() {
        logger.debug("userListJob creation started");

        return jobBuilderFactory.get("userListJob")
                .start(step1CreateBatchHeaderStep())
                .next(step2LoadUsersStep())
                .next(step3PopulateUserListStep())
                .next(step4GenerateCsvStep())
                .next(step5EncryptAndTransferStep())
                .build();
    }

    // Tasklet Step
    @Bean
    public Step step1CreateBatchHeaderStep() {
        logger.debug("step1CreateBatchHeaderStep initialized");
        return stepBuilderFactory.get("step1CreateBatchHeader")
                .tasklet(step1CreateBatchHeader)
                .build();
    }

    // Chunk Step
    @Bean
    public Step step2LoadUsersStep() {
        logger.debug("step2LoadUsersStep initialized");
        return step2LoadUsersChunk.step2LoadUsers(stepBuilderFactory);
    }

    // Chunk Step
    @Bean
    public Step step3PopulateUserListStep() {
        logger.debug("step3PopulateUserListStep initialized");
        return step3PopulateUserListChunk.step3PopulateUserList(stepBuilderFactory);
    }

    // Tasklet Step
    @Bean
    public Step step4GenerateCsvStep() {
        logger.debug("step4GenerateCsvStep initialized");
        return stepBuilderFactory.get("step4GenerateCsv")
                .tasklet(step4GenerateCsv)
                .build();
    }

    // Tasklet Step
    @Bean
    public Step step5EncryptAndTransferStep() {
        logger.debug("step5EncryptAndTransferStep initialized");
        return stepBuilderFactory.get("step5EncryptAndTransfer")
                .tasklet(step5EncryptAndTransfer)
                .build();
    }
}
