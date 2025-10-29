package simple.chatgpt.batch.launcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserListJobQuartzLauncher implements org.quartz.Job {

    private static final Logger logger = LogManager.getLogger(UserListJobQuartzLauncher.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private org.springframework.batch.core.Job userListJob; // Spring Batch job

    @Override
    public void execute(JobExecutionContext context) throws org.quartz.JobExecutionException {
        try {
            logger.debug("Launching userListJob from Quartz at {}", System.currentTimeMillis());

            jobLauncher.run(userListJob, new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters());

        } catch (Exception e) {
            logger.error("Failed to launch userListJob", e);
            throw new org.quartz.JobExecutionException(e.getMessage(), e);
        }
    }
}
