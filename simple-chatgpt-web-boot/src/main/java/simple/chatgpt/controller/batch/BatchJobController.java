package simple.chatgpt.controller.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class BatchJobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job userListJob;

    @GetMapping("/runUserListJob")
    public String runJob() {
        try {
            jobLauncher.run(userListJob, new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters());
            return "Job started!";
        } catch (Exception e) {
            return "Job failed: " + e.getMessage();
        }
    }
}