package simple.chatgpt.batch.example.writer;

import java.util.HashMap;
import java.util.List;
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
public class LoadUsersPagingJob {

    private static final Logger logger = LogManager.getLogger(LoadUsersPagingJob.class);

    /*
    hung: dont remove it
    demonstrates MyBatisPagingItemReader with inner Step and inner reader/writer classes
    */

    @Bean(name = "loadUsersPagingJobBean")  // <-- renamed to avoid conflicts
    public Job loadUsersPagingJobBean(JobBuilderFactory jobBuilderFactory,
                                  StepBuilderFactory stepBuilderFactory,
                                  SqlSessionFactory sqlSessionFactory) {
        logger.debug("loadUsersPagingJobBean called");
        logger.debug("loadUsersPagingJobBean param jobBuilderFactory={}", jobBuilderFactory);
        logger.debug("loadUsersPagingJobBean param stepBuilderFactory={}", stepBuilderFactory);
        logger.debug("loadUsersPagingJobBean param sqlSessionFactory={}", sqlSessionFactory);

        Step loadUsersStep = new LoadUsersStep(stepBuilderFactory, sqlSessionFactory).buildStep();

        return jobBuilderFactory.get("loadUsersPagingJob")
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

            return stepBuilderFactory.get("loadUsersPagingStep")
                    .<UserManagementPojo, UserManagementPojo>chunk(50)
                    .reader(itemReader())
                    .processor(itemProcessor())
                    .writer(itemWriter())
                    .build();
        }

        private ItemReader<UserManagementPojo> itemReader() {
            logger.debug("LoadUsersStep.itemReader called");
            return new InnerPagingReader();
        }

        private ItemProcessor<UserManagementPojo, UserManagementPojo> itemProcessor() {
            logger.debug("LoadUsersStep.itemProcessor called");
            return user -> {
                logger.debug("LoadUsersStep.itemProcessor processing user={}", user);
                return user;
            };
        }

        private ItemWriter<UserManagementPojo> itemWriter() {
            logger.debug("LoadUsersStep.itemWriter called");
            return new InnerWriter();
        }

        // ====================== INNER READER ======================
        private class InnerPagingReader implements ItemReader<UserManagementPojo> {

            private final MyBatisPagingItemReader<UserManagementPojo> delegate;

            public InnerPagingReader() {
                logger.debug("InnerPagingReader constructor called");
                logger.debug("InnerPagingReader param sqlSessionFactory={}", sqlSessionFactory);

                delegate = new MyBatisPagingItemReader<>();
                delegate.setSqlSessionFactory(sqlSessionFactory);
                delegate.setQueryId("simple.chatgpt.mapper.management.UserManagementMapper.search");

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("params.active", true);
                delegate.setParameterValues(parameters);
                delegate.setPageSize(100);

                logger.debug("InnerPagingReader initialized delegate={}", delegate);
                logger.debug("InnerPagingReader parameterValues={}", parameters);
            }

            @Override
            public UserManagementPojo read() throws Exception {
                logger.debug("InnerPagingReader.read called");
                UserManagementPojo user = delegate.read();
                logger.debug("InnerPagingReader.read returned user={}", user);
                return user;
            }
        }

        // ====================== INNER WRITER ======================
        private class InnerWriter implements ItemWriter<UserManagementPojo> {

            @Override
            public void write(List<? extends UserManagementPojo> users) {
                logger.debug("InnerWriter.write called");
                logger.debug("InnerWriter.write param users={}", users);

                if (users == null || users.isEmpty()) {
                    logger.debug("InnerWriter.write found empty or null user list");
                    return;
                }

                logger.debug("InnerWriter.write processing {} users", users.size());
                for (UserManagementPojo user : users) {
                    logger.debug("InnerWriter.write user={}", user);
                }
            }
        }
    }
}
