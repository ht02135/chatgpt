package simple.chatgpt.config.batch;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableBatchProcessing
@EnableTransactionManagement
@PropertySource("classpath:batch-database.properties")
public class BatchConfig {

   private static final Logger logger = LogManager.getLogger(BatchConfig.class);

   private final Environment env;

   @Autowired
   public BatchConfig(Environment env) {
       logger.debug("BatchConfig constructor called");
       logger.debug("BatchConfig param env={}", env);
       this.env = env;
   }

   // -------------------------------------------------------------------------
   // DataSource for Spring Batch metadata tables
   // -------------------------------------------------------------------------
   @Bean(name = "batchDataSource")
   public DataSource batchDataSource() {
       logger.debug("batchDataSource called");
       DriverManagerDataSource dataSource = new DriverManagerDataSource();
       dataSource.setDriverClassName(env.getProperty("batch.jdbc.driver"));
       dataSource.setUrl(env.getProperty("batch.jdbc.url"));
       dataSource.setUsername(env.getProperty("batch.db.username"));
       dataSource.setPassword(env.getProperty("batch.db.password"));
       logger.debug("batchDataSource created with driver={}, url={}, username={}",
               env.getProperty("batch.jdbc.driver"),
               env.getProperty("batch.jdbc.url"),
               env.getProperty("batch.db.username"));
       return dataSource;
   }

   // -------------------------------------------------------------------------
   // Transaction Manager for Batch
   // -------------------------------------------------------------------------
   @Bean(name = "batchTransactionManager")
   public PlatformTransactionManager batchTransactionManager() {
       logger.debug("batchTransactionManager called");
       PlatformTransactionManager tx = new ResourcelessTransactionManager();
       logger.debug("batchTransactionManager created");
       return tx;
   }

   // -------------------------------------------------------------------------
   // Job Repository (core Batch metadata persistence)
   // -------------------------------------------------------------------------
   @Bean
   public JobRepository jobRepository(DataSource batchDataSource, PlatformTransactionManager batchTransactionManager) throws Exception {
       logger.debug("jobRepository called");
       logger.debug("jobRepository param batchDataSource={}", batchDataSource);
       logger.debug("jobRepository param batchTransactionManager={}", batchTransactionManager);

       JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
       factory.setDataSource(batchDataSource);
       factory.setTransactionManager(batchTransactionManager);
       factory.afterPropertiesSet();

       JobRepository jobRepository = factory.getObject();
       logger.debug("jobRepository created with dataSource={} transactionManager={}", batchDataSource, batchTransactionManager);
       return jobRepository;
   }

   // -------------------------------------------------------------------------
   // Job Explorer (for querying job instances/executions)
   // -------------------------------------------------------------------------
   @Bean
   public JobExplorer jobExplorer(DataSource batchDataSource) throws Exception {
       logger.debug("jobExplorer called");
       logger.debug("jobExplorer param batchDataSource={}", batchDataSource);

       JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
       factory.setDataSource(batchDataSource);
       factory.afterPropertiesSet();

       JobExplorer explorer = factory.getObject();
       logger.debug("jobExplorer created with dataSource={}", batchDataSource);
       return explorer;
   }

   // -------------------------------------------------------------------------
   // Job Launcher (executes jobs)
   // -------------------------------------------------------------------------
   @Bean
   public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
       logger.debug("jobLauncher called");
       logger.debug("jobLauncher param jobRepository={}", jobRepository);

       SimpleJobLauncher launcher = new SimpleJobLauncher();
       launcher.setJobRepository(jobRepository);
       launcher.afterPropertiesSet();

       logger.debug("jobLauncher created with jobRepository={}", jobRepository);
       return launcher;
   }

   // -------------------------------------------------------------------------
   // Job Registry (for job lookup and registration)
   // -------------------------------------------------------------------------
   @Bean
   public MapJobRegistry jobRegistry() {
       logger.debug("jobRegistry called");
       MapJobRegistry registry = new MapJobRegistry();
       logger.debug("jobRegistry created");
       return registry;
   }
}
