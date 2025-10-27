package simple.chatgpt.controller.batch;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.service.batch.JobRequestService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/batch/jobRequests", produces = MediaType.APPLICATION_JSON_VALUE)
public class JobRequestController {
    private static final Logger logger = LogManager.getLogger(JobRequestController.class);

    private final JobRequestService jobRequestService;

    public JobRequestController(JobRequestService jobRequestService) {
        this.jobRequestService = jobRequestService;

        logger.debug("JobRequestController constructor called");
        logger.debug("jobRequestService={}", jobRequestService);
    }

    // ------------------ CREATE ------------------
    @PostMapping("/create")
    public ResponseEntity<Response<JobRequest>> create(@RequestBody JobRequest jobRequest) {
        logger.debug("create START jobRequest={}", jobRequest);

        if (jobRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing jobRequest payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        JobRequest created = jobRequestService.create(jobRequest);
        logger.debug("create DONE created={}", created);

        return ResponseEntity.ok(Response.success("Created successfully", created, HttpStatus.OK.value()));
    }

    // ------------------ UPDATE ------------------
    @PutMapping("/update")
    public ResponseEntity<Response<JobRequest>> update(@RequestParam Long id, @RequestBody JobRequest jobRequest) {
        logger.debug("update START id={} jobRequest={}", id, jobRequest);

        if (id == null || jobRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id or jobRequest payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        JobRequest updated = jobRequestService.update(id, jobRequest);
        logger.debug("update DONE updated={}", updated);

        return ResponseEntity.ok(Response.success("Updated successfully", updated, HttpStatus.OK.value()));
    }

    // ------------------ GET ------------------
    @GetMapping("/get")
    public ResponseEntity<Response<JobRequest>> get(@RequestParam Long id) {
        logger.debug("get START id={}", id);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        JobRequest jobRequest = jobRequestService.get(id);
        logger.debug("get return={}", jobRequest);

        return ResponseEntity.ok(Response.success("Fetched successfully", jobRequest, HttpStatus.OK.value()));
    }

    // ------------------ SEARCH ------------------
    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<JobRequest>>> search(@RequestParam Map<String, String> params) {
        logger.debug("search START params={}", params);

        // Service returns paginated result
        PagedResult<JobRequest> result = jobRequestService.search(params);
        logger.debug("search return={}", result);

        return ResponseEntity.ok(Response.success("Fetched successfully", result, HttpStatus.OK.value()));
    }

    // ------------------ DELETE ------------------
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> delete(@RequestParam Long id) {
        logger.debug("delete START id={}", id);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        jobRequestService.delete(id);
        logger.debug("delete DONE id={}", id);

        return ResponseEntity.ok(Response.success("Deleted successfully", null, HttpStatus.OK.value()));
    }
    
    // ------------------ RESET ------------------
    @PostMapping("/reset")
    public ResponseEntity<Response<JobRequest>> reset(@RequestParam Long id) {
        logger.debug("reset START id={}", id);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        try {
            JobRequest resetJobRequest = jobRequestService.reset(id);
            logger.debug("reset DONE id={} resetJobRequest={}", id, resetJobRequest);

            return ResponseEntity.ok(Response.success("Reset successfully", resetJobRequest, HttpStatus.OK.value()));
        } catch (Exception e) {
            logger.error("reset FAILED id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Reset failed: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

}
