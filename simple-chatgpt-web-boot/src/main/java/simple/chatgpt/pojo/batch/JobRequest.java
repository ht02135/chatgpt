package simple.chatgpt.pojo.batch;

import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import simple.chatgpt.util.JsonDataConverter;

@Entity
@Table(name = "job_request")
public class JobRequest {

    private static final Logger logger = LogManager.getLogger(JobRequest.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "processing_stage")
    private Integer processingStage;

    @Column(name = "processing_status")
    private Integer processingStatus;

    @Column(name = "status")
    private String status;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    /*
     * hung: step_data stored as JSON, tracking job’s stage snapshots
     */
    @Convert(converter = JsonDataConverter.class)
    @Column(name = "step_data", columnDefinition = "TEXT")
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
        logger.debug("setId called");
        logger.debug("setId id={}", id);
        this.id = id;
    }

    public Integer getProcessingStage() {
        logger.debug("getProcessingStage() called");
        return processingStage;
    }

    public void setProcessingStage(Integer processingStage) {
        logger.debug("setProcessingStage called");
        logger.debug("setProcessingStage processingStage={}", processingStage);
        this.processingStage = processingStage;
        this.updatedDate = LocalDateTime.now();
    }

    public Integer getProcessingStatus() {
        logger.debug("getProcessingStatus() called");
        return processingStatus;
    }

    public void setProcessingStatus(Integer processingStatus) {
        logger.debug("setProcessingStatus called");
        logger.debug("setProcessingStatus processingStatus={}", processingStatus);
        this.processingStatus = processingStatus;
        this.updatedDate = LocalDateTime.now();
    }

    public String getStatus() {
        logger.debug("getStatus() called");
        return status;
    }

    public void setStatus(String status) {
        logger.debug("setStatus called");
        logger.debug("setStatus status={}", status);
        this.status = status;
        this.updatedDate = LocalDateTime.now();
    }

    public String getJobName() {
        logger.debug("getJobName() called");
        return jobName;
    }

    public void setJobName(String jobName) {
        logger.debug("setJobName called");
        logger.debug("setJobName jobName={}", jobName);
        this.jobName = jobName;
    }

    public String getErrorMessage() {
        logger.debug("getErrorMessage() called");
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        logger.debug("setErrorMessage called");
        logger.debug("setErrorMessage errorMessage={}", errorMessage);
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedDate() {
        logger.debug("getCreatedDate() called");
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        logger.debug("setCreatedDate called");
        logger.debug("setCreatedDate createdDate={}", createdDate);
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        logger.debug("getUpdatedDate() called");
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        logger.debug("setUpdatedDate called");
        logger.debug("setUpdatedDate updatedDate={}", updatedDate);
        this.updatedDate = updatedDate;
    }

    public Map<String, Object> getStepData() {
        logger.debug("getStepData() called");
        return stepData;
    }

    public void setStepData(Map<String, Object> stepData) {
        logger.debug("setStepData called");
        logger.debug("setStepData stepData={}", stepData);
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
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", stepData=" + stepData +
                '}';
    }
}
