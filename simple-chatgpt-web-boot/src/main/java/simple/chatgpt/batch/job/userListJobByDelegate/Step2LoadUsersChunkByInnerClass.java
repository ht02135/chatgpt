package simple.chatgpt.batch.job.userListJobByDelegate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simple.chatgpt.batch.BatchJobConstants;
import simple.chatgpt.batch.job.userListJob.UserListJobConfig;
import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.mapper.management.UserManagementMapper;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.pojo.management.UserManagementPojo;

/*
By moving the reader, processor, and writer into private inner classes:
You avoid duplicate bean names entirely because Spring doesn’t need to manage them as separate beans.
Everything stays encapsulated inside the step, which is cleaner and safer.
Logging and JobRequest handling stay exactly the same, so you retain full visibility into each item processed.
Using MyBatisCursorItemReader ensures you’re streaming users efficiently, instead of loading all at once.
*/

@Component
public class Step2LoadUsersChunkByInnerClass extends AbstractJobRequestDelegate {

    private static final Logger logger = LogManager.getLogger(Step2LoadUsersChunkByInnerClass.class);

    @Autowired
    private JobRequestMapper jobRequestMapper;

    @Autowired
    private UserManagementMapper userManagementMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private StepExecution stepExecution;
    private JobRequest jobRequest;

    /**
     * Constructor calling the superclass constructor
     */
    public Step2LoadUsersChunkByInnerClass(JobRequestMapper jobRequestMapper,
                                           UserManagementMapper userManagementMapper) {
        super(jobRequestMapper, userManagementMapper);
    }

    // =========================================
    // STEP BEAN
    // =========================================

    public Step step2LoadUsersByInnerClass(StepBuilderFactory stepBuilderFactory) {
        logger.debug("step2LoadUsersByInnerClass called");

        return stepBuilderFactory.get("step2LoadUsersByInnerClass")
                .<UserManagementPojo, UserManagementPojo>chunk(50)
                .reader(new UserReader())
                .processor(new UserProcessor())
                .writer(new UserWriter())
                .listener(this)
                .build();
    }

    // =========================================
    // PRIVATE INNER READER
    // =========================================
    private class UserReader extends MyBatisCursorItemReader<UserManagementPojo> {

        public UserReader() {
            logger.debug("UserReader initializing");
            setSqlSessionFactory(sqlSessionFactory);
            setQueryId("simple.chatgpt.mapper.management.UserManagementMapper.getAll"); // MyBatis mapper query id
        }

        @Override
        public UserManagementPojo read() throws Exception {
            if (jobRequest == null) {
                jobRequest = getOneRecentJobRequestByParams(
                        UserListJobConfig.JOB_NAME, 200, 1, JobRequest.STATUS_SUBMITTED);
                logger.debug("read jobRequest={}", jobRequest);

                if (jobRequest == null) {
                    logger.debug("No live JobRequest found");
                    return null;
                }
            }

            UserManagementPojo user = super.read();
            if (user != null) {
                logger.debug("UserReader returning user id={}, userName={}", user.getId(), user.getUserName());
            }
            return user;
        }
    }

    // =========================================
    // PRIVATE INNER PROCESSOR
    // =========================================
    private class UserProcessor implements ItemProcessor<UserManagementPojo, UserManagementPojo> {
        @Override
        public UserManagementPojo process(UserManagementPojo user) throws Exception {
            logger.debug("UserProcessor processing user id={}, userName={}", user.getId(), user.getUserName());
            return user;
        }
    }

    // =========================================
    // PRIVATE INNER WRITER
    // =========================================
    private class UserWriter implements ItemWriter<UserManagementPojo> {
        @Override
        public void write(List<? extends UserManagementPojo> users) throws Exception {
            if (jobRequest == null) {
                logger.debug("UserWriter found no JobRequest, skipping update");
                return;
            }

            logger.debug("UserWriter users={}", users);
            try {
                List<Long> userIds = new ArrayList<>();
                for (UserManagementPojo user : users) {
                    logger.debug("UserWriter processing user id={}, userName={}", user.getId(), user.getUserName());
                    userIds.add(user.getId());
                }

                Map<String, Object> stepData = jobRequest.getStepData() != null
                        ? new HashMap<>(jobRequest.getStepData())
                        : new HashMap<>();

                List<Long> existingIds = (List<Long>) stepExecution.getJobExecution().getExecutionContext()
                        .get(BatchJobConstants.CONTEXT_USER_IDS);
                if (existingIds == null) existingIds = new ArrayList<>();
                existingIds.addAll(userIds);
                logger.debug("UserWriter existingIds={}", existingIds);

                stepData.put(BatchJobConstants.CONTEXT_USER_IDS, existingIds);
                jobRequest.setStepData(stepData);
                jobRequest.setProcessingStage(300);
                jobRequest.setProcessingStatus(1);
                jobRequestMapper.update(jobRequest.getId(), jobRequest);
                logger.debug("###########");
                logger.debug("UserWriter updated jobRequest stage=300 status=1");
                logger.debug("UserWriter jobRequest={}", jobRequest);
                logger.debug("###########");

                stepExecution.getJobExecution().getExecutionContext()
                        .put(BatchJobConstants.CONTEXT_USER_IDS, existingIds);

            } catch (Exception e) {
                logger.error("UserWriter encountered error, marking jobRequest FAILED", e);
                jobRequest.setStatus(JobRequest.STATUS_FAILED);
                jobRequest.setErrorMessage(e.getMessage());
                try {
                    jobRequestMapper.update(jobRequest.getId(), jobRequest);
                } catch (Exception ex) {
                    logger.error("Failed to update JobRequest to FAILED", ex);
                }
                throw e;
            }
        }
    }

    // =========================================
    // STEP LISTENER
    // =========================================
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("beforeStep called for Step2LoadUsersChunkByInnerClass");
        this.stepExecution = stepExecution;
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("afterStep called for Step2LoadUsersChunkByInnerClass, status={}", stepExecution.getStatus());
        this.stepExecution = null;
        return stepExecution.getExitStatus();
    }

    // =========================================
    // Tasklet compliance
    // =========================================
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        logger.debug("execute called on Step2LoadUsersChunkByInnerClass - no-op for chunk-based step");
        return RepeatStatus.FINISHED;
    }
}
