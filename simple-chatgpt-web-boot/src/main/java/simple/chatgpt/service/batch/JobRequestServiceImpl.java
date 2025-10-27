package simple.chatgpt.service.batch;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.util.PagedResult;

@Service
public class JobRequestServiceImpl implements JobRequestService {

    private static final Logger logger = LogManager.getLogger(JobRequestServiceImpl.class);

    @Autowired
    private JobRequestMapper jobRequestMapper;

    // ======= 5 CORE METHODS (on top) =======

    @Override
    public JobRequest create(JobRequest jobRequest) {
        logger.debug("create called");
        logger.debug("create jobRequest={}", jobRequest);
        jobRequestMapper.create(jobRequest);
        logger.debug("create return={}", jobRequest);
        return jobRequest;
    }

    @Override
    public JobRequest update(Long id, JobRequest jobRequest) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update jobRequest={}", jobRequest);
        jobRequestMapper.update(id, jobRequest);
        logger.debug("update return={}", jobRequest);
        return jobRequest;
    }

    @Override
    public PagedResult<JobRequest> search(Map<String, String> params) {
        logger.debug("search called");
        logger.debug("search params={}", params);

        // Extract pagination params
        int page = params.get("page") != null ? Integer.parseInt(params.get("page")) : 0;
        int size = params.get("size") != null ? Integer.parseInt(params.get("size")) : 10;

        Map<String, Object> objParams = new HashMap<>(params);
        List<JobRequest> list = jobRequestMapper.search(objParams);
        long totalCount = list != null ? list.size() : 0;

        logger.debug("search list={}", list);
        logger.debug("search totalCount={}", totalCount);
        logger.debug("search page={}, size={}", page, size);

        PagedResult<JobRequest> pagedResult = new PagedResult<>(list, totalCount, page, size);
        logger.debug("search pagedResult={}", pagedResult);

        return pagedResult;
    }

    @Override
    public JobRequest get(Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);
        JobRequest result = jobRequestMapper.get(id);
        logger.debug("get return={}", result);
        return result;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);
        jobRequestMapper.delete(id);
    }

    // ======= OTHER METHODS =======
    
    @Override
    public JobRequest reset(Long id) {
        logger.debug("reset called with id={}", id);

        // Fetch the JobRequest by id (assuming you have a DAO/repository)
        JobRequest jobRequest = get(id);

        // Reset specific fields
        jobRequest.setProcessingStage(100);                
        jobRequest.setProcessingStatus(1);       
        jobRequest.setStatus(JobRequest.STATUS_SUBMITTED);      

        // Clear all other fields
        jobRequest.setDownloadUrl("");
        jobRequest.setErrorMessage("");

        // Save the reset object
        JobRequest result = update(id, jobRequest);

        logger.debug("reset id={}", id);
        logger.debug("reset result={}", result);

        return result;
    }

    
    @Override
	public List<JobRequest> getJobRequestByParams(Map<String, Object> params)
	{
        logger.debug("getJobRequestByParams called");

        List<JobRequest> mappings = jobRequestMapper.search(params);
        return mappings;
	}

    @Override
    public JobRequest getOneRecentJobRequestByParams(String jobName, Integer processingStage,
                                                     Integer processingStatus, String status) {
        logger.debug("getOneRecentJobRequestByParams called");
        logger.debug("getOneRecentJobRequestByParams jobName={}", jobName);
        logger.debug("getOneRecentJobRequestByParams processingStage={}", processingStage);
        logger.debug("getOneRecentJobRequestByParams processingStatus={}", processingStatus);
        logger.debug("getOneRecentJobRequestByParams status={}", status);

        Map<String, Object> params = new HashMap<>();
        params.put("jobName", jobName);
        params.put("processingStage", processingStage);
        params.put("processingStatus", processingStatus);
        params.put("status", status);

        logger.debug("getOneRecentJobRequestByParams params={}", params);

        List<JobRequest> results = getJobRequestByParams(params);
        logger.debug("getOneRecentJobRequestByParams results={}", results);

        if (results == null || results.isEmpty()) {
            logger.debug("getOneRecentJobRequestByParams no JobRequest found");
            return null;
        }

        JobRequest mostRecent = results.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(JobRequest::getUpdatedDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .findFirst()
                .orElse(null);

        logger.debug("getOneRecentJobRequestByParams mostRecent={}", mostRecent);
        return mostRecent;
    }
}
