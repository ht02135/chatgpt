package simple.chatgpt.service.management.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.management.security.RoleGroupRoleMappingMapper;
import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.SafeConverter;

@Service
public class RoleGroupRoleMappingServiceImpl implements RoleGroupRoleMappingService {

    private static final Logger logger = LogManager.getLogger(RoleGroupRoleMappingServiceImpl.class);

	private final RoleGroupRoleMappingMapper mapper;

    @Autowired
    public RoleGroupRoleMappingServiceImpl(RoleGroupRoleMappingMapper mapper) {
        logger.debug("RoleGroupRoleMappingServiceImpl START");
        logger.debug("mapper={}", mapper);
        this.mapper = mapper;
        logger.debug("RoleGroupRoleMappingServiceImpl DONE");
    }

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @Override
    public RoleGroupRoleMappingPojo create(RoleGroupRoleMappingPojo mapping) {
        logger.debug("create called");
        logger.debug("create mapping={}", mapping);
        mapper.create(mapping);
        return mapping;
    }

    @Override
    public RoleGroupRoleMappingPojo update(Long id, RoleGroupRoleMappingPojo mapping) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update mapping={}", mapping);
        mapper.update(id, mapping);
        return mapping;
    }

    @Override
    public PagedResult<RoleGroupRoleMappingPojo> search(Map<String, String> params) {
        logger.debug("search called");
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

        // Hung : mapper expect Map<String, Object> for offset and limit
    	Map<String, Object> mapperParams = new HashMap<>(params);
        mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
        mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));
        
        List<RoleGroupRoleMappingPojo> items = mapper.search(mapperParams);
        long totalCount = items.size();
        PagedResult<RoleGroupRoleMappingPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("search return={}", result);
        return result;
    }

    @Override
    public RoleGroupRoleMappingPojo get(Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);
        RoleGroupRoleMappingPojo mapping = mapper.get(id);
        logger.debug("get return={}", mapping);
        return mapping;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);
        mapper.delete(id);
    }

    // ======= OTHER METHODS =======

    @Override
    public List<RoleGroupRoleMappingPojo> getMappingsByParams(Map<String, Object> params)
    {
        logger.debug("getMembersByParams called");

        List<RoleGroupRoleMappingPojo> mappings = mapper.search(params);
        return mappings;
    }
    
    // mapper uses #{params.listId}
    @Override
	public List<RoleGroupRoleMappingPojo> getMappingsByRoleGroupId(Long roleGroupId)
    {
        logger.debug("getMembersByListId called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        params.put("roleGroupId", roleGroupId); 
        List<RoleGroupRoleMappingPojo> mappings = getMappingsByParams(params);
        
        return mappings;
    }
    
    @Override
    public List<RoleGroupRoleMappingPojo> getAll() {
        logger.debug("getAll called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        // No offset/limit => all rows
        List<RoleGroupRoleMappingPojo> mappings = getMappingsByParams(params);
        
        return mappings;
    }
    
    public void deleteByRoleGroupId(Long roleGroupId) {
        List<RoleGroupRoleMappingPojo> mappings = getMappingsByRoleGroupId(roleGroupId);
        for (RoleGroupRoleMappingPojo mapping : mappings) {
            delete(mapping.getId());
        }
    }
}
