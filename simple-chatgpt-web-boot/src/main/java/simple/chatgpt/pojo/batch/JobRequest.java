package simple.chatgpt.pojo.batch;

import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name = "job_request")
public class JobRequest {

    private static final Logger logger = LogManager.getLogger(JobRequest.class);

    public static final String STATUS_SUBMITTED = "SUBMITTED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer processingStage;
    private Integer processingStatus;
    private String status;
    private String jobName;
    private String errorMessage;

    private String downloadUrl;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private Map<String, Object> stepData;

    public JobRequest() {
        logger.debug("JobRequest() constructor called");
    }

    // ---------- GETTERS / SETTERS WITH LOGGING ----------

    public Long getId() {
        logger.debug("getId() called");
        return id;
    }

    public void setId(Long id) {
        logger.debug("setId called id={}", id);
        this.id = id;
    }

    public Integer getProcessingStage() {
        logger.debug("getProcessingStage() called");
        return processingStage;
    }

    public void setProcessingStage(Integer processingStage) {
        logger.debug("setProcessingStage called processingStage={}", processingStage);
        this.processingStage = processingStage;
        this.updatedDate = LocalDateTime.now();
    }

    public Integer getProcessingStatus() {
        logger.debug("getProcessingStatus() called");
        return processingStatus;
    }

    public void setProcessingStatus(Integer processingStatus) {
        logger.debug("setProcessingStatus called processingStatus={}", processingStatus);
        this.processingStatus = processingStatus;
        this.updatedDate = LocalDateTime.now();
    }

    public String getStatus() {
        logger.debug("getStatus() called");
        return status;
    }

    public void setStatus(String status) {
        logger.debug("setStatus called status={}", status);
        this.status = status;
        this.updatedDate = LocalDateTime.now();
    }

    public String getJobName() {
        logger.debug("getJobName() called");
        return jobName;
    }

    public void setJobName(String jobName) {
        logger.debug("setJobName called jobName={}", jobName);
        this.jobName = jobName;
    }

    public String getErrorMessage() {
        logger.debug("getErrorMessage() called");
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        logger.debug("setErrorMessage called errorMessage={}", errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getDownloadUrl() {
        logger.debug("getDownloadUrl() called");
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        logger.debug("setDownloadUrl called downloadUrl={}", downloadUrl);
        this.downloadUrl = downloadUrl;
        this.updatedDate = LocalDateTime.now();
    }

    public LocalDateTime getCreatedDate() {
        logger.debug("getCreatedDate() called");
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        logger.debug("setCreatedDate called createdDate={}", createdDate);
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        logger.debug("getUpdatedDate() called");
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        logger.debug("setUpdatedDate called updatedDate={}", updatedDate);
        this.updatedDate = updatedDate;
    }

    public Map<String, Object> getStepData() {
        logger.debug("getStepData() called");
        return stepData;
    }

    public void setStepData(Map<String, Object> stepData) {
        logger.debug("setStepData called stepData={}", stepData);
        this.stepData = stepData;
        this.updatedDate = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        logger.debug("onCreate() called");
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("onUpdate() called");
        updatedDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "JobRequest{" +
                "id=" + id +
                ", processingStage=" + processingStage +
                ", processingStatus=" + processingStatus +
                ", status='" + status + '\'' +
                ", jobName='" + jobName + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", stepData=" + stepData +
                '}';
    }
}
