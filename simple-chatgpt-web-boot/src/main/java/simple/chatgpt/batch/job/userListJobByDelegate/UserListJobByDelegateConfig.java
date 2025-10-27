package simple.chatgpt.batch.job.userListJobByDelegate;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 hung:
 This job uses delegate/mappers directly.
*/

@Configuration
@EnableBatchProcessing
public class UserListJobByDelegateConfig {

    private static final Logger logger = LogManager.getLogger(UserListJobByDelegateConfig.class);

    public static final String JOB_NAME = "userListJobByDelegate";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SqlSessionFactory sqlSessionFactory;

    private final Step1CreateBatchHeaderByDelegate step1CreateBatchHeaderByDelegate;
    private final Step2LoadUsersChunkByDelegate step2LoadUsersChunkByDelegate;
    private final Step3PopulateUserListChunkByDelegate step3PopulateUserListChunkByDelegate;
    private final Step4GenerateCsvByDelegate step4GenerateCsvByDelegate;
    private final Step5EncryptAndTransferByDelegate step5EncryptAndTransferByDelegate;

    public UserListJobByDelegateConfig(JobBuilderFactory jobBuilderFactory,
                                       StepBuilderFactory stepBuilderFactory,
                                       SqlSessionFactory sqlSessionFactory,
                                       Step1CreateBatchHeaderByDelegate step1CreateBatchHeaderByDelegate,
                                       Step2LoadUsersChunkByDelegate step2LoadUsersChunkByDelegate,
                                       Step3PopulateUserListChunkByDelegate step3PopulateUserListChunkByDelegate,
                                       Step4GenerateCsvByDelegate step4GenerateCsvByDelegate,
                                       Step5EncryptAndTransferByDelegate step5EncryptAndTransferByDelegate) {
        logger.debug("UserListJobByDelegateConfig initialized");
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.sqlSessionFactory = sqlSessionFactory;
        this.step1CreateBatchHeaderByDelegate = step1CreateBatchHeaderByDelegate;
        this.step2LoadUsersChunkByDelegate = step2LoadUsersChunkByDelegate;
        this.step3PopulateUserListChunkByDelegate = step3PopulateUserListChunkByDelegate;
        this.step4GenerateCsvByDelegate = step4GenerateCsvByDelegate;
        this.step5EncryptAndTransferByDelegate = step5EncryptAndTransferByDelegate;
    }

    @Bean(name = JOB_NAME)
    public Job userListJobByDelegate() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step1CreateBatchHeaderStep())
                .next(step2LoadUsersStep())
                .next(step3PopulateUserListStep())
                .next(step4GenerateCsvStep())
                .next(step5EncryptAndTransferStep())
                .build();
    }

    @Bean(name = "delegateJob_step1CreateBatchHeaderStep")
    public Step step1CreateBatchHeaderStep() {
        return stepBuilderFactory.get("delegateJob_step1CreateBatchHeader")
                .tasklet(step1CreateBatchHeaderByDelegate)
                .build();
    }

    @Bean(name = "delegateJob_step2LoadUsersStep")
    public Step step2LoadUsersStep() {
        return step2LoadUsersChunkByDelegate.step2LoadUsersByDelegate(stepBuilderFactory);
    }

    @Bean(name = "delegateJob_step3PopulateUserListStep")
    public Step step3PopulateUserListStep() {
        return step3PopulateUserListChunkByDelegate.step3PopulateUserListByDelegate(stepBuilderFactory);
    }

    @Bean(name = "delegateJob_step4GenerateCsvStep")
    public Step step4GenerateCsvStep() {
        return stepBuilderFactory.get("delegateJob_step4GenerateCsv")
                .tasklet(step4GenerateCsvByDelegate)
                .build();
    }

    @Bean(name = "delegateJob_step5EncryptAndTransferStep")
    public Step step5EncryptAndTransferStep() {
        return stepBuilderFactory.get("delegateJob_step5EncryptAndTransfer")
                .tasklet(step5EncryptAndTransferByDelegate)
                .build();
    }
}
