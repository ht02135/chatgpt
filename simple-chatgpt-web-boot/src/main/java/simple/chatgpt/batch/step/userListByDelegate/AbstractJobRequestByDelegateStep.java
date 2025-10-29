package simple.chatgpt.batch.step.userListByDelegate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import simple.chatgpt.batch.step.AbstractJobRequestStep;
import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.mapper.management.UserManagementMapper;
import simple.chatgpt.pojo.management.UserManagementPojo;

@Component
public abstract class AbstractJobRequestByDelegateStep extends AbstractJobRequestStep {

    private static final Logger logger = LogManager.getLogger(AbstractJobRequestByDelegateStep.class);

    protected final UserManagementMapper userManagementMapper;

    /**
     * Constructor injection for mappers
     */
    protected AbstractJobRequestByDelegateStep(JobRequestMapper jobRequestMapper,
                                         UserManagementMapper userManagementMapper) {
        super(jobRequestMapper);
        this.userManagementMapper = userManagementMapper;
    }

    // -----------------------------------------------------
    // UserManagement lookup convenience methods
    // -----------------------------------------------------

    public List<UserManagementPojo> getUserByParams(Map<String, Object> params) {
        logger.debug("getUserByParams called with params={}", params);
        List<UserManagementPojo> mappings = userManagementMapper.search(params);
        logger.debug("getUserByParams result count={}", (mappings != null ? mappings.size() : 0));
        return mappings;
    }

    public List<UserManagementPojo> getAllUsers() {
        logger.debug("getAllUsers called");
        Map<String, Object> params = new HashMap<>();
        List<UserManagementPojo> users = getUserByParams(params);
        logger.debug("getAllUsers result count={}", (users != null ? users.size() : 0));
        return users;
    }
}
