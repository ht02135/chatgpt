package simple.chatgpt.batch.example.reader;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
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
public class LoadUsersJobWithPaging {

    private static final Logger logger = LogManager.getLogger(LoadUsersJobWithPaging.class);

    /*
    hung : dont remove it
    demonstrates MyBatisPagingItemReader with inner Step and private inner reader class
    */

    @Bean(name = "loadUsersJobWithPagingBean")  // <-- renamed bean to avoid conflict
    public Job loadUsersJobWithPaging(JobBuilderFactory jobBuilderFactory,
                                      StepBuilderFactory stepBuilderFactory,
                                      SqlSessionFactory sqlSessionFactory) {
        logger.debug("loadUsersJobWithPaging called");
        logger.debug("loadUsersJobWithPaging param jobBuilderFactory={}", jobBuilderFactory);
        logger.debug("loadUsersJobWithPaging param stepBuilderFactory={}", stepBuilderFactory);
        logger.debug("loadUsersJobWithPaging param sqlSessionFactory={}", sqlSessionFactory);

        Step loadUsersStep = new LoadUsersStep(stepBuilderFactory, sqlSessionFactory).buildStep();

        return jobBuilderFactory.get("loadUsersJobWithPaging")
                .start(loadUsersStep)
                .build();
    }

    // ====================== INNER STEP CLASS ======================
    private static class LoadUsersStep {

        private static final Logger logger = LogManager.getLogger(LoadUsersStep.class);

        private final StepBuilderFactory stepBuilderFactory;
        private final SqlSessionFactory sqlSessionFactory;

        public LoadUsersStep(StepBuilderFactory stepBuilderFactory, SqlSessionFactory sqlSessionFactory) {
            logger.debug("LoadUsersStep constructor called");
            logger.debug("LoadUsersStep param stepBuilderFactory={}", stepBuilderFactory);
            logger.debug("LoadUsersStep param sqlSessionFactory={}", sqlSessionFactory);

            this.stepBuilderFactory = stepBuilderFactory;
            this.sqlSessionFactory = sqlSessionFactory;
        }

        public Step buildStep() {
            logger.debug("LoadUsersStep.buildStep called");

            return stepBuilderFactory.get("loadUsersStepWithPagingReader")
                    .<UserManagementPojo, UserManagementPojo>chunk(50)
                    .reader(itemReader())
                    .processor(itemProcessor())
                    .writer(itemWriter())
                    .build();
        }

        // ------------------- READER -------------------
        private ItemReader<UserManagementPojo> itemReader() {
            logger.debug("LoadUsersStep.itemReader called");
            return new InnerPagingUserReader();
        }

        // ------------------- PROCESSOR -------------------
        private ItemProcessor<UserManagementPojo, UserManagementPojo> itemProcessor() {
            logger.debug("LoadUsersStep.itemProcessor called");
            return user -> {
                logger.debug("LoadUsersStep.itemProcessor processing user={}", user);
                return user;
            };
        }

        // ------------------- WRITER -------------------
        private ItemWriter<UserManagementPojo> itemWriter() {
            logger.debug("LoadUsersStep.itemWriter called");
            return users -> {
                logger.debug("LoadUsersStep.itemWriter called with {} users", users.size());
                for (UserManagementPojo user : users) {
                    logger.debug("LoadUsersStep.itemWriter user={}", user);
                }
            };
        }

        // ====================== PRIVATE INNER READER CLASS ======================
        private class InnerPagingUserReader implements ItemReader<UserManagementPojo> {

            private final MyBatisPagingItemReader<UserManagementPojo> delegate;

            public InnerPagingUserReader() {
                logger.debug("InnerPagingUserReader constructor called");

                delegate = new MyBatisPagingItemReader<>();
                delegate.setSqlSessionFactory(sqlSessionFactory);
                delegate.setQueryId("simple.chatgpt.mapper.management.UserManagementMapper.search");

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("params.active", true);
                delegate.setParameterValues(parameters);
                delegate.setPageSize(100);

                logger.debug("InnerPagingUserReader initialized delegate={}", delegate);
            }

            @Override
            public UserManagementPojo read() throws Exception {
                logger.debug("InnerPagingUserReader.read called");
                UserManagementPojo user = delegate.read();
                logger.debug("InnerPagingUserReader.read returned user={}", user);
                return user;
            }
        }
    }
}
