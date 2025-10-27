package simple.chatgpt.batch.job.writer;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
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
public class LoadUsersCursorJob {

    private static final Logger logger = LogManager.getLogger(LoadUsersCursorJob.class);

    /*
    hung: dont remove it
    demonstrates MyBatisCursorItemReader with inner Step and inner reader/writer classes
    */

    @Bean(name = "loadUsersCursorJobBean")  // <-- renamed bean to avoid conflicts
    public Job loadUsersCursorJob(JobBuilderFactory jobBuilderFactory,
                                  StepBuilderFactory stepBuilderFactory,
                                  SqlSessionFactory sqlSessionFactory) {
        logger.debug("loadUsersCursorJob called");
        logger.debug("loadUsersCursorJob param jobBuilderFactory={}", jobBuilderFactory);
        logger.debug("loadUsersCursorJob param stepBuilderFactory={}", stepBuilderFactory);
        logger.debug("loadUsersCursorJob param sqlSessionFactory={}", sqlSessionFactory);

        Step loadUsersStep = new LoadUsersStep(stepBuilderFactory, sqlSessionFactory).buildStep();

        return jobBuilderFactory.get("loadUsersCursorJob")
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

            return stepBuilderFactory.get("loadUsersCursorStep")
                    .<UserManagementPojo, UserManagementPojo>chunk(50)
                    .reader(itemReader())
                    .processor(itemProcessor())
                    .writer(itemWriter())
                    .build();
        }

        private ItemReader<UserManagementPojo> itemReader() {
            logger.debug("LoadUsersStep.itemReader called");
            return new InnerCursorReader();
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
        private class InnerCursorReader implements ItemReader<UserManagementPojo> {

            private final MyBatisCursorItemReader<UserManagementPojo> delegate;

            public InnerCursorReader() {
                logger.debug("InnerCursorReader constructor called");
                logger.debug("InnerCursorReader param sqlSessionFactory={}", sqlSessionFactory);

                delegate = new MyBatisCursorItemReader<>();
                delegate.setSqlSessionFactory(sqlSessionFactory);
                delegate.setQueryId("simple.chatgpt.mapper.management.UserManagementMapper.search");

                Map<String, Object> params = Map.of("params.active", true);
                delegate.setParameterValues(params);

                logger.debug("InnerCursorReader initialized delegate={}", delegate);
                logger.debug("InnerCursorReader parameterValues={}", params);
            }

            @Override
            public UserManagementPojo read() throws Exception {
                logger.debug("InnerCursorReader.read called");
                UserManagementPojo user = delegate.read();
                logger.debug("InnerCursorReader.read returned user={}", user);
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
