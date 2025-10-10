package simple.chatgpt.service.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.config.management.loader.SecurityConfigLoader;
import simple.chatgpt.mapper.management.UserManagementMapper;
import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.SafeConverter;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    private static final Logger logger = LogManager.getLogger(UserManagementServiceImpl.class);

    private final UserManagementMapper userManagementMapper;
    private final SecurityConfigLoader securityConfigLoader;

    @Autowired
    public UserManagementServiceImpl(UserManagementMapper userManagementMapper,
                                     SecurityConfigLoader securityConfigLoader) {
        logger.debug("UserManagementServiceImpl START");
        logger.debug("UserManagementServiceImpl userManagementMapper={}", userManagementMapper);
        logger.debug("UserManagementServiceImpl securityConfigLoader={}", securityConfigLoader);

        this.userManagementMapper = userManagementMapper;
        this.securityConfigLoader = securityConfigLoader;

        logger.debug("UserManagementServiceImpl DONE");
    }

    @PostConstruct
    public void initializeDB() {
    }
    
    // =========================================================================
    // 5 CORE METHODS (PRIMARY)
    // =========================================================================

    @Override
    public UserManagementPojo create(UserManagementPojo user) {
        logger.debug("create START");
        logger.debug("create user={}", user);
        userManagementMapper.create(user);
        logger.debug("create return={}", user);
        return user;
    }

    @Override
    public UserManagementPojo update(Long id, UserManagementPojo user) {
        logger.debug("update START");
        logger.debug("update id={}", id);
        logger.debug("update user={}", user);
        userManagementMapper.update(id, user);
        logger.debug("update return={}", user);
        return user;
    }

    @Override
    public PagedResult<UserManagementPojo> search(Map<String, String> params) {
        logger.debug("search START");
        logger.debug("search params={}", params);

        if (!params.containsKey("page")) params.put("page", "0");
        if (!params.containsKey("size")) params.put("size", "20");

        int page = SafeConverter.toIntOrDefault(params.get("page"), 0);
        int size = SafeConverter.toIntOrDefault(params.get("size"), 20);
        int offset = page * size;

        if (!params.containsKey("offset")) params.put("offset", String.valueOf(offset));
        if (!params.containsKey("limit")) params.put("limit", String.valueOf(size));

        if (!params.containsKey("sortField")) params.put("sortField", "id");
        if (!params.containsKey("sortDirection")) params.put("sortDirection", "ASC");
        params.put("sortDirection", params.get("sortDirection").toUpperCase());

        // force uppercase for sortDirection
        params.put("sortDirection", params.get("sortDirection").toUpperCase());

        // Hung : mapper expect Map<String, Object> for offset and limit
    	Map<String, Object> mapperParams = new HashMap<>(params);
        mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
        mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));
        
        List<UserManagementPojo> items = userManagementMapper.search(mapperParams);
        long totalCount = items.size(); // replace with count query if available

        PagedResult<UserManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("search return={}", result);
        return result;
    }

    @Override
    public UserManagementPojo get(Long id) {
        logger.debug("get START");
        logger.debug("get id={}", id);
        UserManagementPojo user = userManagementMapper.get(id);
        logger.debug("get return={}", user);
        return user;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete START");
        logger.debug("delete id={}", id);
        userManagementMapper.delete(id);
        logger.debug("delete DONE");
    }
    
    // =========================================================================
    // ORIGINAL METHODS (USED BY CORE)
    // =========================================================================

	public List<UserManagementPojo> getUserdByParams(Map<String, Object> params)
	{
        logger.debug("getUserdByParams called");

        List<UserManagementPojo> mappings = userManagementMapper.search(params);
        return mappings;
	}
	
	// #{params.userName}
	public List<UserManagementPojo> getUserdByUserName(String userName)
	{
        logger.debug("getMappingsByUrlPattern called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName); 
        List<UserManagementPojo> users = getUserdByParams(params);
        
        return users;
	}
	
	public List<UserManagementPojo> getAll()
	{
        logger.debug("getAll called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        // No offset/limit => all rows
        List<UserManagementPojo> users = getUserdByParams(params);
        
        return users;
	}
	
}
