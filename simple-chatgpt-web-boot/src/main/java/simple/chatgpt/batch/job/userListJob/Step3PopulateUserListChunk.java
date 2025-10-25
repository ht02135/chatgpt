package simple.chatgpt.batch.job.userListJob;

import java.sql.Timestamp;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.service.management.UserManagementListMemberService;
import simple.chatgpt.service.management.UserManagementListService;

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

    private StepExecution stepExecution;

    private final UserManagementListService listService;
    private final UserManagementListMemberService memberService;

    public Step3PopulateUserListChunk(UserManagementListService listService,
                                      UserManagementListMemberService memberService) {
        this.listService = listService;
        this.memberService = memberService;
    }

    @Bean
    public Step step3PopulateUserList(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("step3PopulateUserList")
                .<UserManagementPojo, UserManagementListMemberPojo>chunk(50)
                .reader(userReader())
                .processor(userProcessor())
                .writer(userWriter())
                .listener(this)
                .build();
    }

    /** 
     * Reader: load users from JobExecutionContext (from previous step)
     */
    @Bean
    public ItemReader<UserManagementPojo> userReader() {
        return new ItemReader<UserManagementPojo>() {
            private List<UserManagementPojo> users;
            private int index = 0;

            @Override
            public UserManagementPojo read() {
                if (users == null) {
                    users = (List<UserManagementPojo>) stepExecution.getJobExecution()
                            .getExecutionContext().get("LOADED_USERS");
                    if (users == null) {
                        logger.debug("No users found in JobExecutionContext");
                        return null;
                    }
                    logger.debug("Step3: {} users loaded from JobExecutionContext", users.size());
                }
                if (index < users.size()) {
                    return users.get(index++);
                }
                return null; // all items read
            }
        };
    }

    /**
     * Processor: transform UserManagementPojo -> UserManagementListMemberPojo
     */
    @Bean
    public ItemProcessor<UserManagementPojo, UserManagementListMemberPojo> userProcessor() {
        return user -> {
            Long listId = stepExecution.getJobExecution().getExecutionContext().getLong("LIST_ID");
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

            logger.debug("Transformed user {} to list member", user.getUserName());
            return member;
        };
    }

    /**
     * Writer: save members to DB
     */
    @Bean
    public ItemWriter<UserManagementListMemberPojo> userWriter() {
        return members -> {
            for (UserManagementListMemberPojo member : members) {
                logger.debug("Writing list member user={}", member.getUserName());
                memberService.create(member);
            }
        };
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        logger.debug("Step3PopulateUserList starting");

        // Automatically create a new user_management_list
        UserManagementListPojo listPojo = new UserManagementListPojo();
        listPojo.setUserListName("User List " + System.currentTimeMillis());
        listPojo.setDescription("Automatically created by batch job");
        listPojo.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        listPojo.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        UserManagementListPojo savedList = listService.create(listPojo);
        logger.debug("Created user_management_list with id={}", savedList.getId());

        // Store listId in JobExecutionContext for processor
        stepExecution.getJobExecution().getExecutionContext().put("LIST_ID", savedList.getId());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Step3PopulateUserList finished with status {}", stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }
}
