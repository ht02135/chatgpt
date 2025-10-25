package simple.chatgpt.batch.job.userListJob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.service.batch.JobRequestService;
import simple.chatgpt.service.management.UserManagementService;

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
public class Step2LoadUsersChunk extends StepExecutionListenerSupport {

    private static final Logger logger = LogManager.getLogger(Step2LoadUsersChunk.class);

    @Autowired
    private JobRequestService jobRequestService;

    @Autowired
    private UserManagementService userManagementService;

    private StepExecution stepExecution;
    private JobRequest jobRequest; // keep JobRequest in step scope
    private List<UserManagementPojo> allUsers;

    @Bean
    public Step step2LoadUsers(StepBuilderFactory stepBuilderFactory) {
        logger.debug("step2LoadUsers called");

        return stepBuilderFactory.get("step2LoadUsers")
                .<UserManagementPojo, UserManagementPojo>chunk(50)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .listener(this)
                .build();
    }

    // ==================================================
    // READER
    // ==================================================
    @Bean
    public ItemReader<UserManagementPojo> itemReader() {
        logger.debug("itemReader called");

        return new ItemReader<>() {
            private int index = 0;
            private boolean initialized = false;

            @Override
            public UserManagementPojo read() {
                if (!initialized) {
                    logger.debug("itemReader initializing");

                    // fetch JobRequest 200/1/SUBMITTED
                    jobRequest = jobRequestService.getOneRecentJobRequestByParams(
                            "UserListJobConfig", 200, 1, JobRequest.STATUS_SUBMITTED);
                    logger.debug("itemReader fetched jobRequest={}", jobRequest);

                    if (jobRequest == null) {
                        logger.debug("itemReader found no JobRequest, returning null");
                        initialized = true;
                        return null;
                    }

                    // load all users once
                    allUsers = userManagementService.getAll();
                    logger.debug("itemReader loaded users size={}", allUsers.size());

                    initialized = true;
                }

                if (allUsers == null || index >= allUsers.size()) {
                    logger.debug("itemReader no more users, returning null");
                    return null;
                }

                UserManagementPojo user = allUsers.get(index++);
                logger.debug("itemReader returning user={}", user);
                return user;
            }
        };
    }

    // ==================================================
    // PROCESSOR
    // ==================================================
    @Bean
    public ItemProcessor<UserManagementPojo, UserManagementPojo> itemProcessor() {
        return user -> {
            logger.debug("itemProcessor processing user={}", user);
            return user; // no context update here
        };
    }

    // ==================================================
    // WRITER
    // ==================================================
    @Bean
    public ItemWriter<UserManagementPojo> itemWriter() {
        return users -> {
            if (jobRequest == null) {
                logger.debug("itemWriter found no JobRequest, skipping update");
                return;
            }

            try {
                // collect IDs from this chunk
                List<Long> userIds = users.stream().map(UserManagementPojo::getId).toList();

                // merge with existing stepData
                Map<String, Object> stepData = jobRequest.getStepData() != null
                        ? new HashMap<>(jobRequest.getStepData())
                        : new HashMap<>();

                List<Long> existingIds = (List<Long>) stepData.getOrDefault("USER_IDS", new ArrayList<>());
                existingIds.addAll(userIds);
                stepData.put("USER_IDS", existingIds);
                jobRequest.setStepData(stepData);

                // flip stage/status
                jobRequest.setProcessingStage(300);
                jobRequest.setProcessingStatus(1);

                jobRequestService.update(jobRequest.getId(), jobRequest);
                logger.debug("itemWriter updated jobRequest stage=300 status=1, stepData user count={}", existingIds.size());

                // update ExecutionContext once
                stepExecution.getJobExecution().getExecutionContext().put("JOB_REQUEST", jobRequest);

            } catch (Exception e) {
                logger.error("itemWriter encountered error, marking jobRequest FAILED", e);
                jobRequest.setStatus(JobRequest.STATUS_FAILED);
                jobRequest.setErrorMessage(e.getMessage());
                try {
                    jobRequestService.update(jobRequest.getId(), jobRequest);
                } catch (Exception ex) {
                    logger.error("Failed to update JobRequest to FAILED", ex);
                }
                throw e; // rethrow to fail the step
            }
        };
    }

    // ==================================================
    // STEP LISTENER METHODS
    // ==================================================
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("beforeStep called");
        this.stepExecution = stepExecution;
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("afterStep called");
        this.stepExecution = null;
        return stepExecution.getExitStatus();
    }
}
