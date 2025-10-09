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

    // ---------------- CREATE ----------------
    @Override
    public RoleGroupRoleMappingPojo insertMapping(Map<String, Object> params) {
        logger.debug("insertMapping START");
        logger.debug("params={}", params);

        mapper.insertMapping(params);
        RoleGroupRoleMappingPojo fullMapping = mapper.findById(params);

        logger.debug("insertMapping DONE return={}", fullMapping);
        return fullMapping;
    }

    @Override
    public RoleGroupRoleMappingPojo addRoleToGroupIfNotExists(Map<String, Object> params) {
        logger.debug("addRoleToGroupIfNotExists START");
        logger.debug("params={}", params);

        PagedResult<RoleGroupRoleMappingPojo> existingMappings = findByRoleGroupId(params);
        Long roleId = ((Number) params.get("roleId")).longValue();

        RoleGroupRoleMappingPojo existing = existingMappings.getItems().stream()
                .filter(m -> roleId.equals(m.getRoleId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            logger.debug("addRoleToGroupIfNotExists DONE return={}", existing);
            return existing;
        }

        RoleGroupRoleMappingPojo fullMapping = insertMapping(params);
        logger.debug("addRoleToGroupIfNotExists DONE return={}", fullMapping);
        return fullMapping;
    }

    // ---------------- DELETE ----------------
    @Override
    public void deleteMappingById(Map<String, Object> params) {
        logger.debug("deleteMappingById START");
        logger.debug("params={}", params);

        mapper.deleteMappingById(params);

        logger.debug("deleteMappingById DONE");
    }

    @Override
    public void deleteMappingByGroupAndRole(Map<String, Object> params) {
        logger.debug("deleteMappingByGroupAndRole START");
        logger.debug("params={}", params);

        mapper.deleteMappingByGroupAndRole(params);

        logger.debug("deleteMappingByGroupAndRole DONE");
    }

    // ---------------- READ ----------------
    @Override
    public PagedResult<RoleGroupRoleMappingPojo> findAllMappings() {
        logger.debug("findAllMappings START");

        List<RoleGroupRoleMappingPojo> items = mapper.findAllMappings();
        PagedResult<RoleGroupRoleMappingPojo> result = new PagedResult<>(items, items.size(), 1, items.size());

        logger.debug("findAllMappings DONE return={}", result);
        return result;
    }

    @Override
    public PagedResult<RoleGroupRoleMappingPojo> findByRoleGroupId(Map<String, Object> params) {
        logger.debug("findByRoleGroupId START");
        logger.debug("params={}", params);

        List<RoleGroupRoleMappingPojo> items = mapper.findByRoleGroupId(params);
        PagedResult<RoleGroupRoleMappingPojo> result = new PagedResult<>(items, items.size(), 1, items.size());

        logger.debug("findByRoleGroupId DONE return={}", result);
        return result;
    }

    @Override
    public PagedResult<RoleGroupRoleMappingPojo> findByRoleId(Map<String, Object> params) {
        logger.debug("findByRoleId START");
        logger.debug("params={}", params);

        List<RoleGroupRoleMappingPojo> items = mapper.findByRoleId(params);
        PagedResult<RoleGroupRoleMappingPojo> result = new PagedResult<>(items, items.size(), 1, items.size());

        logger.debug("findByRoleId DONE return={}", result);
        return result;
    }

    @Override
    public RoleGroupRoleMappingPojo findById(Map<String, Object> params) {
        logger.debug("findById START");
        logger.debug("params={}", params);

        RoleGroupRoleMappingPojo result = mapper.findById(params);

        logger.debug("findById DONE return={}", result);
        return result;
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @Override
    public PagedResult<RoleGroupRoleMappingPojo> findMappings(Map<String, Object> params) {
        logger.debug("findMappings START");
        logger.debug("params={}", params);

        int page = SafeConverter.toIntOrDefault(params.get("page"), 0);
        int size = SafeConverter.toIntOrDefault(params.get("size"), 20);
        params.put("offset", page * size);
        params.put("limit", size);

        List<RoleGroupRoleMappingPojo> items = mapper.findMappings(params);
        long totalCount = mapper.countMappings(params);

        PagedResult<RoleGroupRoleMappingPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("findMappings DONE return={}", result);
        return result;
    }

    @Override
    public PagedResult<RoleGroupRoleMappingPojo> searchMappings(Map<String, Object> params) {
        logger.debug("searchMappings START");
        logger.debug("params={}", params);

        int page = SafeConverter.toIntOrDefault(params.get("page"), 0);
        int size = SafeConverter.toIntOrDefault(params.get("size"), 20);
        params.put("offset", page * size);
        params.put("limit", size);

        List<RoleGroupRoleMappingPojo> items = mapper.searchMappings(params);
        long totalCount = mapper.countMappings(params);

        PagedResult<RoleGroupRoleMappingPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("searchMappings DONE return={}", result);
        return result;
    }

    // ---------------- COUNT ----------------
    @Override
    public long countMappings(Map<String, Object> params) {
        logger.debug("countMappings START");
        logger.debug("params={}", params);

        long count = mapper.countMappings(params);

        logger.debug("countMappings DONE return={}", count);
        return count;
    }
}
