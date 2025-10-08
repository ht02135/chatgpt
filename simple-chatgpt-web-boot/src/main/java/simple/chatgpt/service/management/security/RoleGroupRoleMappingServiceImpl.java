package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.management.security.RoleGroupRoleMappingMapper;
import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.SafeConverter;

@Service
public class RoleGroupRoleMappingServiceImpl implements RoleGroupRoleMappingService {

    private static final Logger logger = LogManager.getLogger(RoleGroupRoleMappingServiceImpl.class);

    private final RoleGroupRoleMappingMapper mapper;

    @Autowired
    public RoleGroupRoleMappingServiceImpl(
    	RoleGroupRoleMappingMapper mapper) 
   {
        logger.debug("RoleGroupRoleMappingServiceImpl constructor called");
        logger.debug("mapper={}", mapper);
        this.mapper = mapper;
    }

    // ---------------- CREATE ----------------
    @Override
    public RoleGroupRoleMappingPojo insertMapping(Map<String, Object> params) {
        logger.debug("insertMapping called, params={}", params);

        RoleGroupRoleMappingPojo mapping = ParamWrapper.unwrap(params, "mapping");
        logger.debug("insertMapping before insert, mapping={}", mapping);

        // Insert mapping
        mapper.insertMapping(ParamWrapper.wrap("mapping", mapping));
        logger.debug("insertMapping insert completed, mapping id={}", mapping.getId());

        // Re-fetch the fully populated object from DB
        RoleGroupRoleMappingPojo fullMapping = mapper.findById(ParamWrapper.wrap("id", mapping.getId()));
        logger.debug("insertMapping re-fetched fullMapping={}", fullMapping);

        return fullMapping;
    }

    @Override
    public RoleGroupRoleMappingPojo addRoleToGroupIfNotExists(Map<String, Object> params) {
        if (params == null) {
            logger.warn("addRoleToGroupIfNotExists called with null params");
            return null;
        }

        Long roleGroupId = ((Number) ParamWrapper.unwrap(params, "roleGroupId")).longValue();
        Long roleId = ((Number) ParamWrapper.unwrap(params, "roleId")).longValue();
        logger.debug("addRoleToGroupIfNotExists called roleGroupId={} roleId={}", roleGroupId, roleId);

        // Check if mapping already exists
        PagedResult<RoleGroupRoleMappingPojo> existingMappings = findByRoleGroupId(ParamWrapper.wrap("roleGroupId", roleGroupId));
        RoleGroupRoleMappingPojo existing = existingMappings.getItems().stream()
                .filter(m -> roleId.equals(m.getRoleId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            logger.debug("Mapping already exists, skipping insert roleGroupId={} roleId={}", roleGroupId, roleId);
            return existing;
        }

        RoleGroupRoleMappingPojo mapping = new RoleGroupRoleMappingPojo();
        mapping.setRoleGroupId(roleGroupId);
        mapping.setRoleId(roleId);

        // Use insertMapping to insert and fetch fully populated object
        RoleGroupRoleMappingPojo fullMapping = insertMapping(ParamWrapper.wrap("mapping", mapping));
        logger.debug("Inserted new mapping successfully: {}", fullMapping);

        return fullMapping;
    }

    // ---------------- DELETE ----------------
    @Override
    public void deleteMappingById(Map<String, Object> params) {
        logger.debug("deleteMappingById called");
        logger.debug("deleteMappingById params={}", params);
        mapper.deleteMappingById(params);
    }

    @Override
    public void deleteMappingByGroupAndRole(Map<String, Object> params) {
        logger.debug("deleteMappingByGroupAndRole called");
        logger.debug("deleteMappingByGroupAndRole params={}", params);
        mapper.deleteMappingByGroupAndRole(params);
    }

    // ---------------- READ ----------------
    @Override
    public PagedResult<RoleGroupRoleMappingPojo> findAllMappings() {
        logger.debug("findAllMappings called");
        List<RoleGroupRoleMappingPojo> items = mapper.findAllMappings();
        return new PagedResult<>(items, items.size(), 1, items.size());
    }

    @Override
    public PagedResult<RoleGroupRoleMappingPojo> findByRoleGroupId(Map<String, Object> params) {
        logger.debug("findByRoleGroupId called");
        logger.debug("findByRoleGroupId params={}", params);
        List<RoleGroupRoleMappingPojo> items = mapper.findByRoleGroupId(params);
        return new PagedResult<>(items, items.size(), 1, items.size());
    }

    @Override
    public PagedResult<RoleGroupRoleMappingPojo> findByRoleId(Map<String, Object> params) {
        logger.debug("findByRoleId called");
        logger.debug("findByRoleId params={}", params);
        List<RoleGroupRoleMappingPojo> items = mapper.findByRoleId(params);
        return new PagedResult<>(items, items.size(), 1, items.size());
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @Override
    public PagedResult<RoleGroupRoleMappingPojo> findMappings(Map<String, Object> params) {
        logger.debug("findMappings called");
        logger.debug("findMappings params={}", params);

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0); 
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<RoleGroupRoleMappingPojo> items = mapper.findMappings(params);
        long totalCount = mapper.countMappings(params);

        logger.debug("findMappings results size={}", items.size());
        logger.debug("findMappings totalCount={}", totalCount);

        return new PagedResult<>(items, totalCount, page, size);
    }

    @Override
    public PagedResult<RoleGroupRoleMappingPojo> searchMappings(Map<String, Object> params) {
        logger.debug("searchMappings called");
        logger.debug("searchMappings params={}", params);

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0); 
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<RoleGroupRoleMappingPojo> items = mapper.searchMappings(params);
        long totalCount = mapper.countMappings(params);

        logger.debug("searchMappings results size={}", items.size());
        logger.debug("searchMappings totalCount={}", totalCount);

        return new PagedResult<>(items, totalCount, page, size);
    }

    // ---------------- COUNT ----------------
    @Override
    public long countMappings(Map<String, Object> params) {
        logger.debug("countMappings called");
        logger.debug("countMappings params={}", params);
        return mapper.countMappings(params);
    }
}
