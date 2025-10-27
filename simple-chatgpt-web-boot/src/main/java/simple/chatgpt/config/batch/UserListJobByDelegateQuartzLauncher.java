package simple.chatgpt.config.batch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserListJobByDelegateQuartzLauncher implements org.quartz.Job {

    private static final Logger logger = LogManager.getLogger(UserListJobByDelegateQuartzLauncher.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private org.springframework.batch.core.Job userListJobByDelegate; // Spring Batch job

    @Override
    public void execute(JobExecutionContext context) throws org.quartz.JobExecutionException {
        try {
            logger.debug("Launching userListJobByDelegate from Quartz at {}", System.currentTimeMillis());

            jobLauncher.run(userListJobByDelegate, new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters());

        } catch (Exception e) {
            logger.error("Failed to launch userListJobByDelegate", e);
            throw new org.quartz.JobExecutionException(e.getMessage(), e);
        }
    }
}
