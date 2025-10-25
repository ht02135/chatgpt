package simple.chatgpt.mapper.batch;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.batch.JobRequest;

public interface JobRequestMapper {

    // ======= 5 CORE METHODS (following UserManagementMapper) =======

    void create(@Param("jobRequest") JobRequest jobRequest);

    void update(@Param("id") Long id, @Param("jobRequest") JobRequest jobRequest);

    List<JobRequest> search(@Param("params") Map<String, Object> params);

    JobRequest get(@Param("id") Long id);

    void delete(@Param("id") Long id);

    // ======= ADDITIONAL UTILITY METHODS (optional for future use) =======
    
    /*
     * hung: live jobRequest means processing_stage=100, processing_status=1, status=SUBMITTED
     */
    JobRequest getLiveJobRequestByJobName(@Param("jobName") String jobName);
}
