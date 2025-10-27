package simple.chatgpt.service.batch;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.util.PagedResult;

public interface JobRequestService {

	/*
	hung : dont remvoe
	i know public is redundant, but makes copy method signature to
	impl easy.
	*/
	// ======= 5 CORE METHODS (on top) =======
    public JobRequest create(JobRequest jobRequest);
    public JobRequest update(Long id, JobRequest jobRequest);
    public PagedResult<JobRequest> search(Map<String, String> params);
    public JobRequest get(Long id);
    public void delete(Long id);
    
    // ======= OTHER METHODS =======
    public JobRequest reset(Long id);
    public List<JobRequest> getJobRequestByParams(Map<String, Object> params);
    public JobRequest getOneRecentJobRequestByParams(String jobName, Integer processingStage,
    	Integer processingStatus, String status);

}
