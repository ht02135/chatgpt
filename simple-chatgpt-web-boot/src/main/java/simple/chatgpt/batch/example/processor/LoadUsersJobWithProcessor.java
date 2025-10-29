package simple.chatgpt.batch.example.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import simple.chatgpt.pojo.management.UserManagementPojo;

@Configuration
public class LoadUsersJobWithProcessor {

    private static final Logger logger = LogManager.getLogger(LoadUsersJobWithProcessor.class);

    /*
    hung : dont remove it
    demonstrates inner Step class with ItemProcessor
    */

    @Bean(name = "loadUsersJobWithProcessorBean")  // <-- changed bean name to avoid conflict
    public Job loadUsersJobWithProcessor(JobBuilderFactory jobBuilderFactory,
                                         StepBuilderFactory stepBuilderFactory) {
        logger.debug("loadUsersJobWithProcessor called");
        logger.debug("loadUsersJobWithProcessor param jobBuilderFactory={}", jobBuilderFactory);
        logger.debug("loadUsersJobWithProcessor param stepBuilderFactory={}", stepBuilderFactory);

        Step loadUsersStep = new LoadUsersStep(stepBuilderFactory).buildStep();

        return jobBuilderFactory.get("loadUsersJobWithProcessor")
                .start(loadUsersStep)
                .build();
    }

    // ====================== INNER STEP CLASS ======================
    private static class LoadUsersStep {

        private static final Logger logger = LogManager.getLogger(LoadUsersStep.class);

        private final StepBuilderFactory stepBuilderFactory;

        public LoadUsersStep(StepBuilderFactory stepBuilderFactory) {
            logger.debug("LoadUsersStep constructor called");
            logger.debug("LoadUsersStep param stepBuilderFactory={}", stepBuilderFactory);
            this.stepBuilderFactory = stepBuilderFactory;
        }

        public Step buildStep() {
            logger.debug("LoadUsersStep.buildStep called");

            return stepBuilderFactory.get("loadUsersStepWithProcessor")
                    .<UserManagementPojo, UserManagementPojo>chunk(50)
                    .reader(itemReader())
                    .processor(itemProcessor())
                    .writer(itemWriter())
                    .build();
        }

        private ItemReader<UserManagementPojo> itemReader() {
            logger.debug("LoadUsersStep.itemReader called");

            return new ItemReader<>() {
                private final List<UserManagementPojo> users = new ArrayList<>() {{
                    add(new UserManagementPojo() {{
                        setUserName("Alice");
                        setEmail("ALICE@EMAIL.COM");
                        setPassword("secret");
                    }});
                    add(new UserManagementPojo() {{
                        setUserName("Bob");
                        setEmail("BOB@EMAIL.COM");
                        setPassword("hunter2");
                    }});
                }};
                private int index = 0;

                @Override
                public UserManagementPojo read() {
                    logger.debug("In-memory reader read() called, index={}", index);
                    if (index >= users.size()) {
                        logger.debug("In-memory reader reached end of list");
                        return null;
                    }
                    UserManagementPojo user = users.get(index++);
                    logger.debug("In-memory reader returning user={}", user);
                    return user;
                }
            };
        }

        private ItemProcessor<UserManagementPojo, UserManagementPojo> itemProcessor() {
            logger.debug("LoadUsersStep.itemProcessor called");
            return new InnerUserProcessor();
        }

        private ItemWriter<UserManagementPojo> itemWriter() {
            logger.debug("LoadUsersStep.itemWriter called");
            return users -> {
                logger.debug("LoadUsersStep.itemWriter called with {} users", users.size());
                for (UserManagementPojo user : users) {
                    logger.debug("LoadUsersStep.itemWriter user={}", user);
                }
            };
        }

        // ====================== INNER PROCESSOR CLASS ======================
        private static class InnerUserProcessor implements ItemProcessor<UserManagementPojo, UserManagementPojo> {

            private static final Logger logger = LogManager.getLogger(InnerUserProcessor.class);

            @Override
            public UserManagementPojo process(UserManagementPojo user) {
                logger.debug("InnerUserProcessor.process called");
                logger.debug("InnerUserProcessor.process param user={}", user);

                if (user == null) {
                    logger.debug("InnerUserProcessor found null user, returning null");
                    return null;
                }

                if (user.getEmail() != null) {
                    String emailLower = user.getEmail().toLowerCase();
                    logger.debug("InnerUserProcessor normalized emailLower={}", emailLower);
                    user.setEmail(emailLower);
                }

                if (user.getPassword() != null) {
                    user.setPassword("****MASKED****");
                    logger.debug("InnerUserProcessor masked password for user={}", user.getUserName());
                }

                if (user.isLocked()) {
                    user.setActive(false);
                    logger.debug("InnerUserProcessor user locked -> set active=false for user={}", user.getUserName());
                }

                logger.debug("InnerUserProcessor returning processed user={}", user);
                return user;
            }
        }
    }
}
