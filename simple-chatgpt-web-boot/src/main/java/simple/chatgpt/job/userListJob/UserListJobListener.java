package simple.chatgpt.job.userListJob;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserListJobListener extends JobExecutionListenerSupport {
    private static final Logger logger = LogManager.getLogger(UserListJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.debug("Job {} starting", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.debug("Job {} finished with status {}", jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());
    }
}