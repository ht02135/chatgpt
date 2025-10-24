package simple.chatgpt.config.batch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
src/main/java/com/example/batch
├── config
│   ├── BatchConfig.java                  # Spring Batch job & step configuration
│   ├── QuartzSchedulerConfig.java        # Quartz job scheduling
│   └── UserListJobQuartzLauncher.java    # Quartz Job adapter to launch Spring Batch job
├── job
│   ├── userListJob
│   │   ├── UserListJobConfig.java          # Job definition, wires all steps
│   │   ├── Step1CreateBatchHeader.java     # Tasklet + StepExecutionListener
│   │   ├── Step2LoadUsers.java            # Chunk/Tasklet + StepExecutionListener
│   │   ├── Step3PopulateUserList.java     # Chunk/Tasklet + StepExecutionListener
│   │   ├── Step4GenerateCsv.java          # Tasklet + StepExecutionListener
│   │   ├── Step5EncryptAndTransfer.java   # Tasklet + StepExecutionListener
├── service
│   ├── BatchService.java                   # Shared service methods for steps
│   ├── management
│   │   ├── UserManagementService.java
│   │   ├── UserManagementListService.java
│   │   └── UserManagementListMemberService.java
└── pojo
    └── management
        ├── UserManagementPojo.java
        ├── UserManagementListPojo.java
        └── UserManagementListMemberPojo.java
*/

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
