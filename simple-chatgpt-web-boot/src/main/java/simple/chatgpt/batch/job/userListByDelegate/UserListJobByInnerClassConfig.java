package simple.chatgpt.batch.job.userListByDelegate;

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

import simple.chatgpt.batch.step.userListByDelegate.Step1CreateBatchHeaderByDelegate;
import simple.chatgpt.batch.step.userListByDelegate.Step2LoadUsersChunkByInnerClass;
import simple.chatgpt.batch.step.userListByDelegate.Step3PopulateUserListChunkByInnerClass;
import simple.chatgpt.batch.step.userListByDelegate.Step4GenerateCsvByDelegate;
import simple.chatgpt.batch.step.userListByDelegate.Step5EncryptAndTransferByDelegate;

/*
 hung:
 This job uses inner classes instead of delegate beans for Step2 and Step3.
*/

@Configuration
@EnableBatchProcessing
public class UserListJobByInnerClassConfig {

    private static final Logger logger = LogManager.getLogger(UserListJobByInnerClassConfig.class);

    public static final String JOB_NAME = "userListJobByInnerClass";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SqlSessionFactory sqlSessionFactory;

    private final Step1CreateBatchHeaderByDelegate step1CreateBatchHeaderByDelegate;
    private final Step2LoadUsersChunkByInnerClass step2LoadUsersChunkByInnerClass;
    private final Step3PopulateUserListChunkByInnerClass step3PopulateUserListChunkByInnerClass;
    private final Step4GenerateCsvByDelegate step4GenerateCsvByDelegate;
    private final Step5EncryptAndTransferByDelegate step5EncryptAndTransferByDelegate;

    public UserListJobByInnerClassConfig(JobBuilderFactory jobBuilderFactory,
                                         StepBuilderFactory stepBuilderFactory,
                                         SqlSessionFactory sqlSessionFactory,
                                         Step1CreateBatchHeaderByDelegate step1CreateBatchHeaderByDelegate,
                                         Step2LoadUsersChunkByInnerClass step2LoadUsersChunkByInnerClass,
                                         Step3PopulateUserListChunkByInnerClass step3PopulateUserListChunkByInnerClass,
                                         Step4GenerateCsvByDelegate step4GenerateCsvByDelegate,
                                         Step5EncryptAndTransferByDelegate step5EncryptAndTransferByDelegate) {
        logger.debug("UserListJobByInnerClassConfig initialized");
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.sqlSessionFactory = sqlSessionFactory;
        this.step1CreateBatchHeaderByDelegate = step1CreateBatchHeaderByDelegate;
        this.step2LoadUsersChunkByInnerClass = step2LoadUsersChunkByInnerClass;
        this.step3PopulateUserListChunkByInnerClass = step3PopulateUserListChunkByInnerClass;
        this.step4GenerateCsvByDelegate = step4GenerateCsvByDelegate;
        this.step5EncryptAndTransferByDelegate = step5EncryptAndTransferByDelegate;
    }

    @Bean(name = JOB_NAME)
    public Job userListJobByInnerClass() {
        logger.debug("userListJobByInnerClass called");
        return jobBuilderFactory.get(JOB_NAME)
                .start(innerClassJob_step1CreateBatchHeaderStep())
                .next(innerClassJob_step2LoadUsersStep())
                .next(innerClassJob_step3PopulateUserListStep())
                .next(innerClassJob_step4GenerateCsvStep())
                .next(innerClassJob_step5EncryptAndTransferStep())
                .build();
    }

    @Bean(name = "innerClassJob_step1CreateBatchHeaderStep")
    public Step innerClassJob_step1CreateBatchHeaderStep() {
        logger.debug("innerClassJob_step1CreateBatchHeaderStep called");
        return stepBuilderFactory.get("innerClassJob_step1CreateBatchHeader")
                .tasklet(step1CreateBatchHeaderByDelegate)
                .build();
    }

    @Bean(name = "innerClassJob_step2LoadUsersStep")
    public Step innerClassJob_step2LoadUsersStep() {
        logger.debug("innerClassJob_step2LoadUsersStep called");
        return step2LoadUsersChunkByInnerClass.step2LoadUsersByInnerClass(stepBuilderFactory);
    }

    @Bean(name = "innerClassJob_step3PopulateUserListStep")
    public Step innerClassJob_step3PopulateUserListStep() {
        logger.debug("innerClassJob_step3PopulateUserListStep called");
        return step3PopulateUserListChunkByInnerClass.step3PopulateUserListByInnerClass(stepBuilderFactory);
    }

    @Bean(name = "innerClassJob_step4GenerateCsvStep")
    public Step innerClassJob_step4GenerateCsvStep() {
        logger.debug("innerClassJob_step4GenerateCsvStep called");
        return stepBuilderFactory.get("innerClassJob_step4GenerateCsv")
                .tasklet(step4GenerateCsvByDelegate)
                .build();
    }

    @Bean(name = "innerClassJob_step5EncryptAndTransferStep")
    public Step innerClassJob_step5EncryptAndTransferStep() {
        logger.debug("innerClassJob_step5EncryptAndTransferStep called");
        return stepBuilderFactory.get("innerClassJob_step5EncryptAndTransfer")
                .tasklet(step5EncryptAndTransferByDelegate)
                .build();
    }
}
