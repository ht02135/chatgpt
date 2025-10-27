package simple.chatgpt.batch.job.userListJob;

import java.sql.Timestamp;
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
import org.springframework.stereotype.Component;

import simple.chatgpt.batch.BatchJobConstants;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.service.batch.JobRequestService;
import simple.chatgpt.service.management.UserManagementListMemberService;
import simple.chatgpt.service.management.UserManagementListService;
import simple.chatgpt.service.management.UserManagementService;

@Component
public class Step3PopulateUserListChunk extends StepExecutionListenerSupport {

    private static final Logger logger = LogManager.getLogger(Step3PopulateUserListChunk.class);

    private final JobRequestService jobRequestService;
    private final UserManagementListService listService;
    private final UserManagementListMemberService memberService;
    private final UserManagementService userManagementService;

    private StepExecution stepExecution;
    private JobRequest jobRequest;
    private List<Long> userIds;
    private int index = 0;

    public Step3PopulateUserListChunk(JobRequestService jobRequestService,
                                      UserManagementListService listService,
                                      UserManagementListMemberService memberService,
                                      UserManagementService userManagementService) {
        this.jobRequestService = jobRequestService;
        this.listService = listService;
        this.memberService = memberService;
        this.userManagementService = userManagementService;
    }

    public Step step3PopulateUserList(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("step3PopulateUserList")
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
        private boolean initialized = false;

        @Override
        public UserManagementPojo read() {
            if (!initialized) {
                logger.debug("UserReader initializing");

                jobRequest = jobRequestService.getOneRecentJobRequestByParams(
                        UserListJobConfig.JOB_NAME, 300, 1, JobRequest.STATUS_SUBMITTED);
                logger.debug("UserReader fetched jobRequest={}", jobRequest);

                if (jobRequest == null) {
                    logger.debug("No JobRequest found, ending step");
                    initialized = true;
                    return null;
                }

                userIds = (List<Long>) stepExecution.getJobExecution().getExecutionContext()
                        .get(BatchJobConstants.CONTEXT_USER_IDS);
                logger.debug("UserReader loaded {} userIds from ExecutionContext", userIds != null ? userIds.size() : 0);

                initialized = true;
            }

            if (userIds == null || index >= userIds.size()) {
                return null;
            }

            Long userId = userIds.get(index++);
            UserManagementPojo user = userManagementService.get(userId);
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
            Number listIdNum = (Number) stepExecution.getJobExecution()
                    .getExecutionContext().get(BatchJobConstants.CONTEXT_LIST_ID);
            Long listId = (listIdNum != null) ? listIdNum.longValue() : null;
            if (listId == null) {
                throw new IllegalStateException("listId not found in ExecutionContext");
            }

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
            member.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            member.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            logger.debug("UserProcessor processed user {} -> list member", user.getUserName());
            return member;
        }
    }

    // =========================================
    // PRIVATE INNER WRITER
    // =========================================
    private class UserWriter implements ItemWriter<UserManagementListMemberPojo> {
        @Override
        public void write(List<? extends UserManagementListMemberPojo> members) {
            if (jobRequest == null) {
                logger.debug("UserWriter found no JobRequest, skipping update");
                return;
            }

            try {
                List<Long> memberIds = new ArrayList<>();
                for (UserManagementListMemberPojo member : members) {
                    memberService.create(member);
                    memberIds.add(member.getId());
                    logger.debug("UserWriter saved list member user={}", member.getUserName());
                }

                Map<String, Object> stepData = jobRequest.getStepData() != null
                        ? new HashMap<>(jobRequest.getStepData())
                        : new HashMap<>();

                List<Long> existingMemberIds = (List<Long>) stepExecution.getJobExecution().getExecutionContext()
                        .get(BatchJobConstants.CONTEXT_MEMBER_IDS);
                if (existingMemberIds == null) existingMemberIds = new ArrayList<>();
                existingMemberIds.addAll(memberIds);
                stepData.put(BatchJobConstants.CONTEXT_MEMBER_IDS, existingMemberIds);

                jobRequest.setStepData(stepData);
                jobRequest.setProcessingStage(400);
                jobRequest.setProcessingStatus(1);
                jobRequestService.update(jobRequest.getId(), jobRequest);
                logger.debug("###########");
                logger.debug("UserWriter updated jobRequest stage=400 status=1");
                logger.debug("UserWriter  jobRequest={}", jobRequest);
                logger.debug("###########");

                stepExecution.getJobExecution().getExecutionContext()
                        .put(BatchJobConstants.CONTEXT_MEMBER_IDS, existingMemberIds);

            } catch (Exception e) {
                logger.error("UserWriter encountered error, marking jobRequest FAILED", e);
                jobRequest.setStatus(JobRequest.STATUS_FAILED);
                jobRequest.setErrorMessage(e.getMessage());
                try {
                    jobRequestService.update(jobRequest.getId(), jobRequest);
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
        this.stepExecution = stepExecution;
        logger.debug("beforeStep called for Step3PopulateUserListChunk");
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("afterStep called for Step3PopulateUserListChunk, status={}", stepExecution.getStatus());
        this.stepExecution = null;
        return stepExecution.getExitStatus();
    }
}
