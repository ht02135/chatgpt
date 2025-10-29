package simple.chatgpt.controller.batch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.batch.step.BatchJobConstants;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.service.batch.JobRequestService; // <-- import service
import simple.chatgpt.util.Response;

@RestController
@RequestMapping("/batch")
public class BatchJobController {
	
    private static final Logger logger = LogManager.getLogger(BatchJobController.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job userListJob;

    @Autowired
    private JobRequestService jobRequestService; // <-- inject service

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
    
    // ------------------ DOWNLOAD USER LIST ------------------
    /*
    Key Points / Comments:
	1>Return type void: Because we write directly to HttpServletResponse. 
	No Resource or ResponseEntity is returned, avoiding Jackson serialization 
	issues.
	2>Headers: Sets Content-Disposition and Content-Length so browser treats 
	it as a download.
	3>Streaming: Uses BufferedInputStream and ServletOutputStream to 
	efficiently stream file bytes.
	4>Error handling: Returns 404 if file/job not found, 500 if streaming 
	fails.
    */
    @GetMapping("/downloads")
    public void downloadFile(@RequestParam Long jobRequestId, HttpServletResponse response) {
        // hung: start logging
        logger.debug("downloadFile START");
        logger.debug("downloadFile jobRequestId={}", jobRequestId);

        // Fetch the JobRequest by ID
        JobRequest jobRequest = jobRequestService.get(jobRequestId);
        if (jobRequest == null) {
            logger.warn("JobRequest not found: jobRequestId={}", jobRequestId);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // return 404
            return;
        }

        // Get the file name from the job request
        String fileName = jobRequest.getDownloadUrl(); // or jobRequest.getFileName()
        File file = new File(BatchJobConstants.USER_LIST_BASE_DIR, fileName);
        logger.debug("downloadFile fileName={}", fileName);
        logger.debug("downloadFile file={}", file);

        if (!file.exists() || !file.isFile()) {
            logger.warn("File not found: {}", file.getAbsolutePath());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // return 404
            return;
        }

        // hung: set proper headers for download
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        response.setContentLengthLong(file.length());

        // hung: stream file directly to HTTP response
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
             ServletOutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();

        } catch (IOException e) {
            // hung: log and return 500 if streaming fails
            logger.error("Error streaming file: {}", file.getAbsolutePath(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
