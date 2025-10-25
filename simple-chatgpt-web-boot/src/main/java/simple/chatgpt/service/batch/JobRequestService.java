package simple.chatgpt.service.batch;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.pojo.management.UserManagementPojo;

public interface JobRequestService {

	/*
	hung : dont remvoe
	i know public is redundant, but makes copy method signature to
	impl easy.
	*/
	// ======= 5 CORE METHODS (on top) =======
	public void create(JobRequest jobRequest);
	public void update(Long id, JobRequest jobRequest);
	public List<JobRequest> search(Map<String, Object> params);
	public JobRequest get(Long id);
	public void delete(Long id);
    
    // ======= OTHER METHODS =======
    
	// live jobRequest is processing_stage=100 processing_status=1 status=SUBMITTED
    public JobRequest getLiveJobRequestByJobName(String jobName);
    
}
