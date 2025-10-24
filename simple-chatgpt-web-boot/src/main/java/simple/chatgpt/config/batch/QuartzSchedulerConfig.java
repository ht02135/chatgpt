package simple.chatgpt.config.batch;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

@Configuration
public class QuartzSchedulerConfig {

    /**
     * Define Quartz JobDetail for the Quartz scheduler.
     * This references the Quartz job (UserListJobQuartzLauncher)
     * which launches the Spring Batch job.
     */
    @Bean
    public JobDetail userListJobDetail() {
        return JobBuilder.newJob(UserListJobQuartzLauncher.class) // <-- updated
                .withIdentity("userListJobDetail")
                .storeDurably() // keeps the job in the scheduler even if no trigger
                .build();
    }

    /**
     * Define a Quartz Trigger to schedule the job execution.
     * In this example, it runs every 24 hours.
     */
    @Bean
    public Trigger userListJobTrigger(JobDetail userListJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(userListJobDetail)
                .withIdentity("userListJobTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(24)
                        .repeatForever())
                .build();
    }
}
