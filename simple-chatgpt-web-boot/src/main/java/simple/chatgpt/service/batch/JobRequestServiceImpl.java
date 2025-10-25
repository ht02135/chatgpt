package simple.chatgpt.service.batch;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.pojo.batch.JobRequest;

@Service
public class JobRequestServiceImpl implements JobRequestService {

    private static final Logger logger = LogManager.getLogger(JobRequestServiceImpl.class);

    @Autowired
    private JobRequestMapper jobRequestMapper;

    // ======= 5 CORE METHODS (on top) =======

    @Override
    public void create(JobRequest jobRequest) {
        logger.debug("create called");
        logger.debug("create jobRequest={}", jobRequest);
        jobRequestMapper.create(jobRequest);
    }

    @Override
    public void update(Long id, JobRequest jobRequest) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update jobRequest={}", jobRequest);
        jobRequestMapper.update(id, jobRequest);
    }

    @Override
    public List<JobRequest> search(Map<String, Object> params) {
        logger.debug("search called");
        logger.debug("search params={}", params);
        List<JobRequest> result = jobRequestMapper.search(params);
        logger.debug("search result={}", result);
        return result;
    }

    @Override
    public JobRequest get(Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);
        JobRequest result = jobRequestMapper.get(id);
        logger.debug("get result={}", result);
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
    public JobRequest getLiveJobRequestByJobName(String jobName) {
        logger.debug("getLiveJobRequestByJobName called");
        logger.debug("getLiveJobRequestByJobName jobName={}", jobName);
        JobRequest result = jobRequestMapper.getLiveJobRequestByJobName(jobName);
        logger.debug("getLiveJobRequestByJobName result={}", result);
        return result;
    }
}
