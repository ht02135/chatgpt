package simple.chatgpt.batch.config;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableBatchProcessing
@PropertySource("classpath:batch-database.properties")
public class BatchConfig {

    private static final Logger logger = LogManager.getLogger(BatchConfig.class);

    private final Environment env;

    public BatchConfig(Environment env) {
        logger.debug("BatchConfig constructor called, env={}", env);
        this.env = env;
    }

    // Custom DataSource for batch
    @Bean(name = "batchDataSource")
    public DataSource batchDataSource() {
        logger.debug("batchDataSource called");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("batch.jdbc.driver"));
        dataSource.setUrl(env.getProperty("batch.jdbc.url"));
        dataSource.setUsername(env.getProperty("batch.db.username"));
        dataSource.setPassword(env.getProperty("batch.db.password"));
        logger.debug("batchDataSource created with driver={}", env.getProperty("batch.jdbc.driver"));
        logger.debug("batchDataSource url={}", env.getProperty("batch.jdbc.url"));
        logger.debug("batchDataSource username={}", env.getProperty("batch.db.username"));
        return dataSource;
    }

    // Custom transaction manager
    @Bean(name = "batchTransactionManager")
    public PlatformTransactionManager batchTransactionManager() {
        logger.debug("batchTransactionManager called");
        PlatformTransactionManager tx = new ResourcelessTransactionManager();
        logger.debug("batchTransactionManager created");
        return tx;
    }

    // ObjectMapper for LocalDateTime support
    @Bean
    public ObjectMapper objectMapper() {
        logger.debug("objectMapper called");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        logger.debug("objectMapper created with JavaTimeModule and WRITE_DATES_AS_TIMESTAMPS disabled");
        return mapper;
    }

    // Serializer for Spring Batch ExecutionContext
    @Bean
    public Jackson2ExecutionContextStringSerializer batchExecutionContextSerializer(ObjectMapper objectMapper) {
        logger.debug("batchExecutionContextSerializer called, objectMapper={}", objectMapper);
        Jackson2ExecutionContextStringSerializer serializer = new Jackson2ExecutionContextStringSerializer();
        serializer.setObjectMapper(objectMapper);
        logger.debug("batchExecutionContextSerializer created");
        return serializer;
    }
}
