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

@Service
public class RoleGroupRoleMappingServiceImpl implements RoleGroupRoleMappingService {

    private static final Logger logger = LogManager.getLogger(RoleGroupRoleMappingServiceImpl.class);

    private final RoleGroupRoleMappingMapper mapper;

    @Autowired
    public RoleGroupRoleMappingServiceImpl(RoleGroupRoleMappingMapper mapper) {
        logger.debug("RoleGroupRoleMappingServiceImpl constructor called");
        logger.debug("mapper={}", mapper);
        this.mapper = mapper;
    }

    // ---------------- CREATE ----------------
    @Override
    public int insertMapping(Map<String, Object> params) {
        logger.debug("insertMapping called");
        logger.debug("insertMapping params={}", params);
        return mapper.insertMapping(params);
    }

    @Override
    public RoleGroupRoleMappingPojo addRoleToGroupIfNotExists(Map<String, Object> params) {
        if (params == null) {
            logger.warn("addRoleToGroupIfNotExists called with null params");
            return null;
        }

        Long roleGroupId = ((Number) params.get("roleGroupId")).longValue();
        Long roleId = ((Number) params.get("roleId")).longValue();

        logger.debug("addRoleToGroupIfNotExists called roleGroupId={} roleId={}", roleGroupId, roleId);

        // Check if mapping already exists
        PagedResult<RoleGroupRoleMappingPojo> existingMappings = findByRoleGroupId(Map.of("roleGroupId", roleGroupId));
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

        int rowsInserted = insertMapping(Map.of("mapping", mapping));
        if (rowsInserted < 1) {
            logger.warn("Failed to insert mapping roleGroupId={} roleId={}", roleGroupId, roleId);
            return null;
        }

        logger.debug("Inserted new mapping successfully: {}", mapping);
        return mapping;
    }

    // ---------------- DELETE ----------------
    @Override
    public int deleteMappingById(Map<String, Object> params) {
        logger.debug("deleteMappingById called");
        logger.debug("deleteMappingById params={}", params);
        return mapper.deleteMappingById(params);
    }

    @Override
    public int deleteMappingByGroupAndRole(Map<String, Object> params) {
        logger.debug("deleteMappingByGroupAndRole called");
        logger.debug("deleteMappingByGroupAndRole params={}", params);
        return mapper.deleteMappingByGroupAndRole(params);
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

        int page = params.get("page") != null ? (int) params.get("page") : 1;
        int size = params.get("size") != null ? (int) params.get("size") : 20;
        int offset = (page - 1) * size;

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

        int page = params.get("page") != null ? (int) params.get("page") : 1;
        int size = params.get("size") != null ? (int) params.get("size") : 20;
        int offset = (page - 1) * size;

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
