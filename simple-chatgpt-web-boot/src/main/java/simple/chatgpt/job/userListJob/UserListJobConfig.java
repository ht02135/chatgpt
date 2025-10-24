package simple.chatgpt.job.userListJob;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserListJobConfig {
    private static final Logger logger = LogManager.getLogger(UserListJobConfig.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public UserListJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        logger.debug("UserListJobConfig initialized");
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job userListJob(Step step1CreateBatchHeader,
                           Step step2LoadUsers,
                           Step step3PopulateUserList,
                           Step step4GenerateCsv,
                           Step step5EncryptAndTransfer,
                           UserListJobListener listener) {

        logger.debug("userListJob creation started");

        return jobBuilderFactory.get("userListJob")
                .listener(listener)
                .start(step1CreateBatchHeader)
                .next(step2LoadUsers)
                .next(step3PopulateUserList)
                .next(step4GenerateCsv)
                .next(step5EncryptAndTransfer)
                .build();
    }

    @Bean
    public Step step1CreateBatchHeader(Tasklet tasklet) {
        logger.debug("step1CreateBatchHeader initialized");
        return stepBuilderFactory.get("step1CreateBatchHeader")
                .tasklet(tasklet)
                .build();
    }

    @Bean
    public Step step2LoadUsers(Tasklet tasklet) {
        logger.debug("step2LoadUsers initialized");
        return stepBuilderFactory.get("step2LoadUsers")
                .tasklet(tasklet)
                .build();
    }

    @Bean
    public Step step3PopulateUserList(Tasklet tasklet) {
        logger.debug("step3PopulateUserList initialized");
        return stepBuilderFactory.get("step3PopulateUserList")
                .tasklet(tasklet)
                .build();
    }

    @Bean
    public Step step4GenerateCsv(Tasklet tasklet) {
        logger.debug("step4GenerateCsv initialized");
        return stepBuilderFactory.get("step4GenerateCsv")
                .tasklet(tasklet)
                .build();
    }

    @Bean
    public Step step5EncryptAndTransfer(Tasklet tasklet) {
        logger.debug("step5EncryptAndTransfer initialized");
        return stepBuilderFactory.get("step5EncryptAndTransfer")
                .tasklet(tasklet)
                .build();
    }
}