package simple.chatgpt.batch.step.userList;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simple.chatgpt.batch.job.userList.UserListJobConfig;
import simple.chatgpt.batch.step.AbstractJobRequestStep;
import simple.chatgpt.batch.step.BatchJobConstants;
import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.service.batch.JobRequestService;
import simple.chatgpt.service.management.UserManagementService;

@Component
public class Step2LoadUsersChunk extends AbstractJobRequestStep {

    private static final Logger logger = LogManager.getLogger(Step2LoadUsersChunk.class);

    private final JobRequestService jobRequestService;
    private final UserManagementService userManagementService;

    private StepExecution stepExecution;
    private JobRequest jobRequest;
    
    private boolean initialized = false;
    private int index = 0;
    private List<UserManagementPojo> allUsers;

    @Autowired
    public Step2LoadUsersChunk(JobRequestMapper jobRequestMapper,
                               JobRequestService jobRequestService,
                               UserManagementService userManagementService) {
        super(jobRequestMapper);
        this.jobRequestService = jobRequestService;
        this.userManagementService = userManagementService;
    }

    public Step step2LoadUsers(StepBuilderFactory stepBuilderFactory) {
        logger.debug("step2LoadUsers called");
        return stepBuilderFactory.get("step2LoadUsers")
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
    private class UserReader implements ItemReader<UserManagementPojo> {

        public UserReader() { 
        }
        
        @Override
        public UserManagementPojo read() {
            if (!initialized) {
                logger.debug("UserReader initializing");

                jobRequest = getOneRecentJobRequestByParams(
                	UserListJobConfig.JOB_NAME, 200, 1, JobRequest.STATUS_SUBMITTED);
                logger.debug("read jobRequest={}", jobRequest);

                if (jobRequest == null) {
                    logger.debug("No live JobRequest found");
                    initialized = true;
                    return null;
                }

                allUsers = userManagementService.getAll();
                logger.debug("UserReader allUsers.size()={}", allUsers.size());

                initialized = true;
            }

            logger.debug("UserReader ##########");
            logger.debug("UserReader allUsers.size={}", allUsers.size());
            logger.debug("UserReader index={}", index);
            logger.debug("UserReader ##########");
            if (allUsers == null || index >= allUsers.size()) {
                logger.debug("No more users, returning null");
                return null;
            }

            UserManagementPojo user = allUsers.get(index++);
            logger.debug("UserReader user={}", user);
            return user;
        }
    }

    // =========================================
    // PRIVATE INNER PROCESSOR
    // =========================================
    private class UserProcessor implements ItemProcessor<UserManagementPojo, UserManagementPojo> {
        @Override
        public UserManagementPojo process(UserManagementPojo user) {
            logger.debug("UserProcessor processing user id={}, userName={}", user.getId(), user.getUserName());
            return user;
        }
    }

    // =========================================
    // PRIVATE INNER WRITER
    // =========================================
    private class UserWriter implements ItemWriter<UserManagementPojo> {
        @Override
        public void write(List<? extends UserManagementPojo> users) {
            logger.debug("UserWriter users={}", users);

            try {
                List<Long> userIds = new ArrayList<>();
                for (UserManagementPojo user : users) {
                    logger.debug("UserWriter user={}", user);
                    userIds.add(user.getId());
                }
                logger.debug("UserWriter userIds={}", userIds);

                // ==================================================
                // Use helper methods instead of manual stepData & ExecutionContext
                // ==================================================
                List<Long> existingIds = (List<Long>) stepExecution.getJobExecution().getExecutionContext()
                        .get(BatchJobConstants.CONTEXT_USER_IDS);
                if (existingIds == null) existingIds = new ArrayList<>();
                existingIds.addAll(userIds);
                logger.debug("UserWriter existingIds={}", existingIds);

                updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.CONTEXT_USER_IDS, existingIds);
                updateJobRequest(jobRequest, 300, 1, JobRequest.STATUS_SUBMITTED);

                logger.debug("###########");
                logger.debug("UserWriter updated jobRequest stage=300 status=1");
                logger.debug("UserWriter jobRequest={}", jobRequest);
                logger.debug("###########");

            } catch (Exception e) {
                logger.error("Error e={}", e);
                updateJobRequest(jobRequest, jobRequest.getProcessingStage(), 999, 
                	JobRequest.STATUS_FAILED, e.getMessage());
                throw e;
            }
        }
    }

    // =========================================
    // STEP LISTENER
    // =========================================
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("beforeStep called for Step2LoadUsersChunk");
        this.stepExecution = stepExecution;
        initialized = false;
        index = 0;
        allUsers = null;
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("afterStep called for Step2LoadUsersChunk, status={}", stepExecution.getStatus());
        this.stepExecution = null;
        return stepExecution.getExitStatus();
    }

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
