package simple.chatgpt.service.management.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.management.security.UserManagementRoleGroupMappingMapper;
import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.SafeConverter;

@Service
public class UserManagementRoleGroupMappingServiceImpl implements UserManagementRoleGroupMappingService {

	private static final Logger logger = LogManager.getLogger(UserManagementRoleGroupMappingServiceImpl.class);

	private final UserManagementRoleGroupMappingMapper mappingMapper;

	@Autowired
	public UserManagementRoleGroupMappingServiceImpl(UserManagementRoleGroupMappingMapper mappingMapper) {
		logger.debug("UserManagementRoleGroupMappingServiceImpl constructor START");
		logger.debug("UserManagementRoleGroupMappingServiceImpl mappingMapper={}", mappingMapper);
		this.mappingMapper = mappingMapper;
		logger.debug("UserManagementRoleGroupMappingServiceImpl constructor DONE");
	}

	// ==============================================================
	// ================ 5 CORE METHODS (on top) =====================
	// ==============================================================

	@Override
	public UserManagementRoleGroupMappingPojo create(UserManagementRoleGroupMappingPojo mapping) {
		logger.debug("create called");
		logger.debug("create mapping={}", mapping);
		mappingMapper.create(mapping);
		return mapping;
	}

	@Override
	public UserManagementRoleGroupMappingPojo update(Long id, UserManagementRoleGroupMappingPojo mapping) {
		logger.debug("update called");
		logger.debug("update id={}", id);
		logger.debug("update mapping={}", mapping);
		mappingMapper.update(id, mapping);
		return mapping;
	}

	@Override
	public PagedResult<UserManagementRoleGroupMappingPojo> search(Map<String, String> params) {
		logger.debug("search called");
		logger.debug("search params={}", params);

		if (!params.containsKey("page"))
			params.put("page", "0");
		if (!params.containsKey("size"))
			params.put("size", "20");
		int page = SafeConverter.toIntOrDefault(params.get("page"), 0);
		int size = SafeConverter.toIntOrDefault(params.get("size"), 20);
		int offset = page * size;

		if (!params.containsKey("offset"))
			params.put("offset", String.valueOf(offset));
		if (!params.containsKey("limit"))
			params.put("limit", String.valueOf(size));
		if (!params.containsKey("sortField"))
			params.put("sortField", "id");
		if (!params.containsKey("sortDirection"))
			params.put("sortDirection", "ASC");
		params.put("sortDirection", params.get("sortDirection").toUpperCase());

		// Hung : mapper expect Map<String, Object> for offset and limit
		Map<String, Object> mapperParams = new HashMap<>(params);
		mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
		mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));

		List<UserManagementRoleGroupMappingPojo> items = mappingMapper.search(mapperParams);
		long totalCount = items.size();
		PagedResult<UserManagementRoleGroupMappingPojo> result = new PagedResult<>(items, totalCount, page, size);
		logger.debug("search return={}", result);
		return result;
	}

	@Override
	public UserManagementRoleGroupMappingPojo get(Long id) {
		logger.debug("get called");
		logger.debug("get id={}", id);
		UserManagementRoleGroupMappingPojo mapping = mappingMapper.get(id);
		logger.debug("get return={}", mapping);
		return mapping;
	}

	@Override
	public void delete(Long id) {
		logger.debug("delete called");
		logger.debug("delete id={}", id);
		mappingMapper.delete(id);
	}

	// ======= OTHER METHODS =======

	public List<UserManagementRoleGroupMappingPojo> getMappingsByParams(Map<String, Object> params)
	{
        logger.debug("getMappingsByParams called");

        List<UserManagementRoleGroupMappingPojo> mappings = mappingMapper.search(params);
        return mappings;
	}
	
	// #{params.userId}
	public List<UserManagementRoleGroupMappingPojo> getMappingsByUserId(Long userId)
	{
        logger.debug("getMappingsByUserId called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId); 
        List<UserManagementRoleGroupMappingPojo> mappings = getMappingsByParams(params);
        
        return mappings;
	}
	
	public List<UserManagementRoleGroupMappingPojo> getAll()
	{
        logger.debug("getAll called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        // No offset/limit => all rows
        List<UserManagementRoleGroupMappingPojo> mappings = getMappingsByParams(params);
        
        return mappings;	
	}
}
