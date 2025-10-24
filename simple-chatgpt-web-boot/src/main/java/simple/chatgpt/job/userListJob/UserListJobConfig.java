package simple.chatgpt.job.userListJob;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserListJobConfig {
    private static final Logger logger = LogManager.getLogger(UserListJobConfig.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final Step1CreateBatchHeader step1CreateBatchHeaderTasklet;
    private final Step2LoadUsers step2LoadUsersTasklet;
    private final Step3PopulateUserList step3PopulateUserListTasklet;
    private final Step4GenerateCsv step4GenerateCsvTasklet;
    private final Step5EncryptAndTransfer step5EncryptAndTransferTasklet;
    private final UserListJobListener jobListener;

    public UserListJobConfig(JobBuilderFactory jobBuilderFactory,
                             StepBuilderFactory stepBuilderFactory,
                             Step1CreateBatchHeader step1CreateBatchHeaderTasklet,
                             Step2LoadUsers step2LoadUsersTasklet,
                             Step3PopulateUserList step3PopulateUserListTasklet,
                             Step4GenerateCsv step4GenerateCsvTasklet,
                             Step5EncryptAndTransfer step5EncryptAndTransferTasklet,
                             UserListJobListener jobListener) {
        logger.debug("UserListJobConfig initialized");
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.step1CreateBatchHeaderTasklet = step1CreateBatchHeaderTasklet;
        this.step2LoadUsersTasklet = step2LoadUsersTasklet;
        this.step3PopulateUserListTasklet = step3PopulateUserListTasklet;
        this.step4GenerateCsvTasklet = step4GenerateCsvTasklet;
        this.step5EncryptAndTransferTasklet = step5EncryptAndTransferTasklet;
        this.jobListener = jobListener;
    }

    @Bean
    public Job userListJob() {
        logger.debug("userListJob creation started");

        return jobBuilderFactory.get("userListJob")
                .listener(jobListener)
                .start(step1CreateBatchHeader())
                .next(step2LoadUsers())
                .next(step3PopulateUserList())
                .next(step4GenerateCsv())
                .next(step5EncryptAndTransfer())
                .build();
    }

    @Bean
    public Step step1CreateBatchHeader() {
        logger.debug("step1CreateBatchHeader initialized");
        return stepBuilderFactory.get("step1CreateBatchHeader")
                .tasklet(step1CreateBatchHeaderTasklet)
                .build();
    }

    @Bean
    public Step step2LoadUsers() {
        logger.debug("step2LoadUsers initialized");
        return stepBuilderFactory.get("step2LoadUsers")
                .tasklet(step2LoadUsersTasklet)
                .build();
    }

    @Bean
    public Step step3PopulateUserList() {
        logger.debug("step3PopulateUserList initialized");
        return stepBuilderFactory.get("step3PopulateUserList")
                .tasklet(step3PopulateUserListTasklet)
                .build();
    }

    @Bean
    public Step step4GenerateCsv() {
        logger.debug("step4GenerateCsv initialized");
        return stepBuilderFactory.get("step4GenerateCsv")
                .tasklet(step4GenerateCsvTasklet)
                .build();
    }

    @Bean
    public Step step5EncryptAndTransfer() {
        logger.debug("step5EncryptAndTransfer initialized");
        return stepBuilderFactory.get("step5EncryptAndTransfer")
                .tasklet(step5EncryptAndTransferTasklet)
                .build();
    }
}
