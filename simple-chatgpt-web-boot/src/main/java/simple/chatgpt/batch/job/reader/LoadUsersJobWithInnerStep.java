package simple.chatgpt.batch.job.reader;

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

/*
hung: dont remove it
demonstrates MyBatisCursorItemReader with inner Step class and private inner reader class
*/

@Configuration
public class LoadUsersJobWithInnerStep {

    private static final Logger logger = LogManager.getLogger(LoadUsersJobWithInnerStep.class);

    @Bean(name = "loadUsersJobWithInnerStepBean")  // <-- renamed bean to avoid conflict
    public Job loadUsersJobWithInnerStep(JobBuilderFactory jobBuilderFactory,
                                         StepBuilderFactory stepBuilderFactory,
                                         SqlSessionFactory sqlSessionFactory) {
        logger.debug("loadUsersJobWithInnerStep called");
        logger.debug("loadUsersJobWithInnerStep param jobBuilderFactory={}", jobBuilderFactory);
        logger.debug("loadUsersJobWithInnerStep param stepBuilderFactory={}", stepBuilderFactory);
        logger.debug("loadUsersJobWithInnerStep param sqlSessionFactory={}", sqlSessionFactory);

        Step loadUsersInnerStep = new LoadUsersStep(stepBuilderFactory, sqlSessionFactory).buildStep();

        return jobBuilderFactory.get("loadUsersJobWithInnerStep")
                .start(loadUsersInnerStep)
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

            return stepBuilderFactory.get("loadUsersStepWithInnerReader")
                    .<UserManagementPojo, UserManagementPojo>chunk(50)
                    .reader(itemReader())
                    .processor(itemProcessor())
                    .writer(itemWriter())
                    .build();
        }

        // ------------------- READER -------------------
        private ItemReader<UserManagementPojo> itemReader() {
            logger.debug("LoadUsersStep.itemReader called");
            return new InnerUserReader();
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
        private class InnerUserReader implements ItemReader<UserManagementPojo> {

            private final MyBatisCursorItemReader<UserManagementPojo> delegate;

            public InnerUserReader() {
                logger.debug("InnerUserReader constructor called");

                delegate = new MyBatisCursorItemReader<>();
                delegate.setSqlSessionFactory(sqlSessionFactory);
                delegate.setQueryId("simple.chatgpt.mapper.management.UserManagementMapper.search");
                delegate.setParameterValues(Map.of("params.active", true));

                logger.debug("InnerUserReader initialized delegate={}", delegate);
            }

            @Override
            public UserManagementPojo read() throws Exception {
                logger.debug("InnerUserReader.read called");
                UserManagementPojo user = delegate.read();
                logger.debug("InnerUserReader.read returned user={}", user);
                return user;
            }
        }
    }
}
