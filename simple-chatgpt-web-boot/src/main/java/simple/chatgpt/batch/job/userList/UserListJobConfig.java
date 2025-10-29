package simple.chatgpt.batch.job.userList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import simple.chatgpt.batch.step.userList.Step1CreateBatchHeader;
import simple.chatgpt.batch.step.userList.Step2LoadUsersChunk;
import simple.chatgpt.batch.step.userList.Step3PopulateUserListChunk;
import simple.chatgpt.batch.step.userList.Step4GenerateCsv;
import simple.chatgpt.batch.step.userList.Step5EncryptAndTransfer;

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

    /** hung: public constant for JobRequest jobName */
    public static final String JOB_NAME = "userListJob";

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

    @Bean(name = JOB_NAME)
    public Job userListJob() {
        logger.debug("userListJob creation started");
        return jobBuilderFactory.get(JOB_NAME)
                .start(step1CreateBatchHeaderStep())
                .next(step2LoadUsersStep())
                .next(step3PopulateUserListStep())
                .next(step4GenerateCsvStep())
                .next(step5EncryptAndTransferStep())
                .build();
    }

    @Bean(name = "userListJob_step1CreateBatchHeaderStep")
    public Step step1CreateBatchHeaderStep() {
        return stepBuilderFactory.get("userListJob_step1CreateBatchHeader")
                .tasklet(step1CreateBatchHeader)
                .build();
    }

    @Bean(name = "userListJob_step2LoadUsersStep")
    public Step step2LoadUsersStep() {
        return step2LoadUsersChunk.step2LoadUsers(stepBuilderFactory);
    }

    @Bean(name = "userListJob_step3PopulateUserListStep")
    public Step step3PopulateUserListStep() {
        return step3PopulateUserListChunk.step3PopulateUserList(stepBuilderFactory);
    }

    @Bean(name = "userListJob_step4GenerateCsvStep")
    public Step step4GenerateCsvStep() {
        return stepBuilderFactory.get("userListJob_step4GenerateCsv")
                .tasklet(step4GenerateCsv)
                .build();
    }

    @Bean(name = "userListJob_step5EncryptAndTransferStep")
    public Step step5EncryptAndTransferStep() {
        return stepBuilderFactory.get("userListJob_step5EncryptAndTransfer")
                .tasklet(step5EncryptAndTransfer)
                .build();
    }
}
