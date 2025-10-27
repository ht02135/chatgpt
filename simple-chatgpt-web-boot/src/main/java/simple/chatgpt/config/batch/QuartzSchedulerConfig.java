package simple.chatgpt.config.batch;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzSchedulerConfig {

    // ===== Existing Job =====
    @Bean
    public JobDetail userListJobDetail() {
        return JobBuilder.newJob(UserListJobQuartzLauncher.class)
                .withIdentity("userListJobDetail")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger userListJobTrigger(JobDetail userListJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(userListJobDetail)
                .withIdentity("userListJobTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 10 * * ?")) // 10 AM every day
                .build();
    }

    // ===== New Job: userListJobByDelegate =====
    @Bean
    public JobDetail userListJobByDelegateDetail() {
        return JobBuilder.newJob(UserListJobByDelegateQuartzLauncher.class) // <-- launcher for delegate-based job
                .withIdentity("userListJobByDelegateDetail")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger userListJobByDelegateTrigger(JobDetail userListJobByDelegateDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(userListJobByDelegateDetail)
                .withIdentity("userListJobByDelegateTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 30 10 * * ?")) // 10:30 AM every day
                .build();
    }
}
