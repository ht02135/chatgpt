package simple.chatgpt.batch.job.userListJob;

import javax.sql.DataSource;

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
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import simple.chatgpt.pojo.management.UserManagementPojo;

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
    private DataSource dataSource;

    @Bean
    public Step step2LoadUsers(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("step2LoadUsers")
                .<UserManagementPojo, UserManagementPojo>chunk(50)
                .reader(userReader())
                .processor(userProcessor())
                .writer(userWriter())
                .listener(this)
                .build();
    }

    @Bean
    public ItemReader<UserManagementPojo> userReader() {
        return new JdbcCursorItemReaderBuilder<UserManagementPojo>()
                .name("userReader")
                .dataSource(dataSource)
                .sql("SELECT * FROM user_management WHERE active = true")
                .rowMapper((rs, rowNum) -> {
                    UserManagementPojo user = new UserManagementPojo();
                    user.setId(rs.getLong("id"));
                    user.setUserName(rs.getString("user_name"));
                    user.setUserKey(rs.getString("user_key"));
                    user.setEmail(rs.getString("email"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setActive(rs.getBoolean("active"));
                    user.setLocked(rs.getBoolean("locked"));
                    user.setAddressLine1(rs.getString("address_line_1"));
                    user.setAddressLine2(rs.getString("address_line_2"));
                    user.setCity(rs.getString("city"));
                    user.setState(rs.getString("state"));
                    user.setPostCode(rs.getString("post_code"));
                    user.setCountry(rs.getString("country"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return user;
                })
                .build();
    }

    @Bean
    public ItemProcessor<UserManagementPojo, UserManagementPojo> userProcessor() {
        return user -> {
            logger.debug("Loaded user={}", user.getUserName());
            return user; // no transformation
        };
    }

    @Bean
    public ItemWriter<UserManagementPojo> userWriter() {
        return users -> {
            // Save loaded users into JobExecutionContext for the next step
            StepExecution stepExecution = this.getStepExecution(); 
            stepExecution.getJobExecution().getExecutionContext().put("LOADED_USERS", users);
            logger.debug("Stored {} users in JobExecutionContext", users.size());
        };
    }

    // Use beforeStep/afterStep hooks to set StepExecution for writer
    private StepExecution stepExecution;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        logger.debug("Step2LoadUsers starting");
    }

    private StepExecution getStepExecution() {
        return stepExecution;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Step2LoadUsers finished with status {}", stepExecution.getStatus());
        this.stepExecution = null;
        return stepExecution.getExitStatus();
    }
}
