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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.service.batch.JobRequestService;
import simple.chatgpt.service.management.UserManagementListMemberService;
import simple.chatgpt.service.management.UserManagementListService;
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
public class Step3PopulateUserListChunk extends StepExecutionListenerSupport {

    private static final Logger logger = LogManager.getLogger(Step3PopulateUserListChunk.class);

    @Autowired
    private JobRequestService jobRequestService;

    private final UserManagementListService listService;
    private final UserManagementListMemberService memberService;
    private final UserManagementService userManagementService;

    private StepExecution stepExecution;
    private JobRequest jobRequest;
    private List<Long> userIds;
    private int index = 0;

    public Step3PopulateUserListChunk(UserManagementListService listService,
                                      UserManagementListMemberService memberService,
                                      UserManagementService userManagementService) {
        this.listService = listService;
        this.memberService = memberService;
        this.userManagementService = userManagementService;
    }

    @Bean
    public Step step3PopulateUserList(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("step3PopulateUserList")
                .<UserManagementPojo, UserManagementListMemberPojo>chunk(50)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .listener(this)
                .build();
    }

    // ==================================================
    // READER: read one user at a time by ID from JobRequest
    // ==================================================
    @Bean
    public ItemReader<UserManagementPojo> itemReader() {
        return new ItemReader<>() {
            private boolean initialized = false;

            @Override
            public UserManagementPojo read() {
                if (!initialized) {
                    logger.debug("itemReader initializing Step3");

                    // fetch JobRequest from context
                    jobRequest = (JobRequest) stepExecution.getJobExecution()
                            .getExecutionContext().get(UserListJobConfig.CONTEXT_JOB_REQUEST);
                    logger.debug("itemReader fetched jobRequest={}", jobRequest);

                    if (jobRequest == null) {
                        logger.debug("No JobRequest found in context, ending step");
                        initialized = true;
                        return null;
                    }

                    // get USER_IDS from stepData
                    Map<String, Object> stepData = jobRequest.getStepData();
                    if (stepData == null || !stepData.containsKey(UserListJobConfig.CONTEXT_USER_IDS)) {
                        logger.debug("No USER_IDS found in JobRequest.stepData, ending step");
                        initialized = true;
                        return null;
                    }

                    userIds = (List<Long>) stepData.get(UserListJobConfig.CONTEXT_USER_IDS);
                    logger.debug("itemReader loaded {} userIds from JobRequest", userIds.size());
                    initialized = true;
                }

                if (userIds == null || index >= userIds.size()) {
                    return null;
                }

                // fetch user by ID
                Long userId = userIds.get(index++);
                UserManagementPojo user = userManagementService.get(userId);
                logger.debug("itemReader returning user id={}, userName={}", user.getId(), user.getUserName());
                return user;
            }
        };
    }

    // ==================================================
    // PROCESSOR: convert user -> list member
    // ==================================================
    @Bean
    public ItemProcessor<UserManagementPojo, UserManagementListMemberPojo> itemProcessor() {
        return user -> {
            Long listId = (Long) stepExecution.getJobExecution().getExecutionContext().get(UserListJobConfig.CONTEXT_LIST_ID);
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

            logger.debug("Processed user {} -> list member", user.getUserName());
            return member;
        };
    }

    // ==================================================
    // WRITER: save members, update JobRequest
    // ==================================================
    @Bean
    public ItemWriter<UserManagementListMemberPojo> itemWriter() {
        return members -> {
            if (jobRequest == null) {
                logger.debug("itemWriter found no JobRequest, skipping update");
                return;
            }

            try {
                List<Long> memberIds = new ArrayList<>();
                for (UserManagementListMemberPojo member : members) {
                    memberService.create(member);
                    memberIds.add(member.getId());
                    logger.debug("Saved list member user={}", member.getUserName());
                }

                // update JobRequest.stepData with MEMBER_IDS
                Map<String, Object> stepData = jobRequest.getStepData() != null
                        ? new HashMap<>(jobRequest.getStepData())
                        : new HashMap<>();
                List<Long> existingMemberIds = (List<Long>) stepData.getOrDefault(UserListJobConfig.CONTEXT_MEMBER_IDS, new ArrayList<>());
                existingMemberIds.addAll(memberIds);
                stepData.put(UserListJobConfig.CONTEXT_MEMBER_IDS, existingMemberIds);
                jobRequest.setStepData(stepData);

                // flip stage/status to 400/1
                jobRequest.setProcessingStage(400);
                jobRequest.setProcessingStatus(1);
                jobRequestService.update(jobRequest.getId(), jobRequest);
                logger.debug("itemWriter updated jobRequest stage=400 status=1, total member count={}", existingMemberIds.size());

                // save updated JobRequest in context
                stepExecution.getJobExecution().getExecutionContext().put(UserListJobConfig.CONTEXT_JOB_REQUEST, jobRequest);

            } catch (Exception e) {
                logger.error("itemWriter encountered error, marking jobRequest FAILED", e);
                jobRequest.setStatus(JobRequest.STATUS_FAILED);
                jobRequest.setErrorMessage(e.getMessage());
                try {
                    jobRequestService.update(jobRequest.getId(), jobRequest);
                } catch (Exception ex) {
                    logger.error("Failed to update JobRequest to FAILED", ex);
                }
                throw e;
            }
        };
    }

    // ==================================================
    // STEP LISTENER METHODS
    // ==================================================
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        logger.debug("beforeStep called for Step3PopulateUserList");
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("afterStep called for Step3PopulateUserList, status={}", stepExecution.getStatus());
        this.stepExecution = null;
        return stepExecution.getExitStatus();
    }
}
