package simple.chatgpt.controller.batch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.util.Response;

@RestController
@RequestMapping("/batch")
public class BatchJobController {
	
    private static final Logger logger = LogManager.getLogger(BatchJobController.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job userListJob;

    // ------------------ RUN USER LIST JOB ------------------
    @GetMapping("/runUserListJob")
    public ResponseEntity<Response<Void>> runJob() {
        logger.debug("runUserListJob START");

        if (userListJob == null) {
            logger.error("runUserListJob failed: userListJob bean is null");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("userListJob bean is not available", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

        try {
            jobLauncher.run(userListJob, new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters());

            logger.debug("runUserListJob SUCCESS");
            return ResponseEntity.ok(Response.success("Job started successfully", null, HttpStatus.OK.value()));

        } catch (Exception e) {
            logger.error("runUserListJob FAILED", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Job failed: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}