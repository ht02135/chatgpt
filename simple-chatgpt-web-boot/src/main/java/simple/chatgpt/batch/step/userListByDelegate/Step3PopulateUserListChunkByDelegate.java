package simple.chatgpt.batch.step.userListByDelegate;

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
import simple.chatgpt.batch.step.BatchJobConstants;
import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.mapper.management.UserManagementListMemberMapper;
import simple.chatgpt.mapper.management.UserManagementMapper;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementPojo;

@Component
public class Step3PopulateUserListChunkByDelegate extends AbstractJobRequestDelegate {

    private static final Logger logger = LogManager.getLogger(Step3PopulateUserListChunkByDelegate.class);

    @Autowired
    private UserManagementMapper userManagementMapper;

    @Autowired
    private UserManagementListMemberMapper memberMapper;

    @Autowired
    private JobRequestMapper jobRequestMapper;

    private StepExecution stepExecution;
    private JobRequest jobRequest;
    
    private boolean initialized = false;
    private int index = 0;
    private List<Long> userIds;

    public Step3PopulateUserListChunkByDelegate(JobRequestMapper jobRequestMapper,
                                                UserManagementMapper userManagementMapper) {
        super(jobRequestMapper, userManagementMapper);
    }

    // =========================================
    // STEP BEAN
    // =========================================

    public Step step3PopulateUserListByDelegate(StepBuilderFactory stepBuilderFactory) {
        logger.debug("step3PopulateUserListByDelegate called");

        return stepBuilderFactory.get("step3PopulateUserListByDelegate")
                .<UserManagementPojo, UserManagementListMemberPojo>chunk(50)
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
                        UserListJobConfig.JOB_NAME, 300, 1, JobRequest.STATUS_SUBMITTED);
                logger.debug("read jobRequest={}", jobRequest);

                if (jobRequest == null) {
                    logger.debug("No live JobRequest found");
                    initialized = true;
                    return null;
                }

                userIds = (List<Long>) stepExecution.getJobExecution().getExecutionContext()
                        .get(BatchJobConstants.CONTEXT_USER_IDS);
                logger.debug("UserReader loaded {} userIds from ExecutionContext", (userIds != null ? userIds.size() : 0));
                initialized = true;
            }

            if (userIds == null || index >= userIds.size()) return null;

            Long userId = userIds.get(index++);
            UserManagementPojo user = userManagementMapper.get(userId);
            logger.debug("UserReader returning user id={}, userName={}", user.getId(), user.getUserName());
            return user;
        }
    }

    // =========================================
    // PRIVATE INNER PROCESSOR
    // =========================================
    private class UserProcessor implements ItemProcessor<UserManagementPojo, UserManagementListMemberPojo> {
        @Override
        public UserManagementListMemberPojo process(UserManagementPojo user) {
            Number listIdNum = (Number) stepExecution.getJobExecution().getExecutionContext()
                    .get(BatchJobConstants.CONTEXT_LIST_ID);
            Long listId = (listIdNum != null) ? listIdNum.longValue() : null;
            if (listId == null) throw new IllegalStateException("listId not found in ExecutionContext");

            UserManagementListMemberPojo member = new UserManagementListMemberPojo();
            member.setListId(listId);
            member.setUserName(user.getUserName());
            member.setUserKey(user.getUserKey());
            member.setPassword(user.getPassword());
            member.setFirstName(user.getFirstName());
            member.setLastName(user.getLastName());
            member.setEmail(user.getEmail());
            member.setAddressLine1(user.getAddressLine1());
            member.setAddressLine2(user.getAddressLine2());
            member.setCity(user.getCity());
            member.setState(user.getState());
            member.setPostCode(user.getPostCode());
            member.setCountry(user.getCountry());
            member.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            member.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            logger.debug("UserProcessor member=", member);
            return member;
        }
    }

    // =========================================
    // PRIVATE INNER WRITER
    // =========================================
    private class UserWriter implements ItemWriter<UserManagementListMemberPojo> {
        @Override
        public void write(List<? extends UserManagementListMemberPojo> members) {
        	
            try {
                List<Long> memberIds = new ArrayList<>();
                for (UserManagementListMemberPojo member : members) {
                    memberMapper.create(member);
                    memberIds.add(member.getId());
                    logger.debug("UserWriter saved list member user={}", member.getUserName());
                }

                List<Long> existingMemberIds = (List<Long>) stepExecution.getJobExecution().getExecutionContext()
                        .get(BatchJobConstants.CONTEXT_MEMBER_IDS);
                if (existingMemberIds == null) existingMemberIds = new ArrayList<>();
                existingMemberIds.addAll(memberIds);

                // === use updateJobRequestStepData & updateJobRequest ===
                updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.CONTEXT_MEMBER_IDS, existingMemberIds);
                updateJobRequest(jobRequest, 400, 1, JobRequest.STATUS_SUBMITTED);

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
        logger.debug("beforeStep called for Step3PopulateUserListChunkByDelegate");
        this.stepExecution = stepExecution;
        initialized = false;
        index = 0;
        userIds = null;
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("afterStep called for Step3PopulateUserListChunkByDelegate, status={}", stepExecution.getStatus());
        this.stepExecution = null;
        return stepExecution.getExitStatus();
    }

    // =========================================
    // Tasklet compliance
    // =========================================
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        logger.debug("execute called on Step3PopulateUserListChunkByDelegate - no-op for chunk-based step");
        return RepeatStatus.FINISHED;
    }
}
