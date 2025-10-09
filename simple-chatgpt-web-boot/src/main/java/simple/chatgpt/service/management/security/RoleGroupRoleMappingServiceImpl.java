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
    public RoleGroupRoleMappingServiceImpl(RoleGroupRoleMappingMapper mapper) {
        logger.debug("RoleGroupRoleMappingServiceImpl START");
        logger.debug("RoleGroupRoleMappingServiceImpl mapper={}", mapper);
        this.mapper = mapper;
        logger.debug("RoleGroupRoleMappingServiceImpl DONE");
    }

    // ---------------- CREATE ----------------
    @Override
    public RoleGroupRoleMappingPojo insertMapping(Map<String, Object> params) {
        logger.debug("insertMapping START");
        logger.debug("insertMapping params={}", params);

        RoleGroupRoleMappingPojo mapping = ParamWrapper.unwrap(params, "mapping");

        mapper.insertMapping(ParamWrapper.wrap("mapping", mapping));

        RoleGroupRoleMappingPojo fullMapping = mapper.findById(ParamWrapper.wrap("id", mapping.getId()));
        logger.debug("insertMapping return={}", fullMapping);
        return fullMapping;
    }

    @Override
    public RoleGroupRoleMappingPojo addRoleToGroupIfNotExists(Map<String, Object> params) {
        logger.debug("addRoleToGroupIfNotExists START");
        logger.debug("addRoleToGroupIfNotExists params={}", params);

        if (params == null) {
            logger.debug("addRoleToGroupIfNotExists return=null");
            return null;
        }

        Long roleGroupId = ((Number) ParamWrapper.unwrap(params, "roleGroupId")).longValue();
        Long roleId = ((Number) ParamWrapper.unwrap(params, "roleId")).longValue();

        PagedResult<RoleGroupRoleMappingPojo> existingMappings = findByRoleGroupId(ParamWrapper.wrap("roleGroupId", roleGroupId));
        RoleGroupRoleMappingPojo existing = existingMappings.getItems().stream()
                .filter(m -> roleId.equals(m.getRoleId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            logger.debug("addRoleToGroupIfNotExists return={}", existing);
            return existing;
        }

        RoleGroupRoleMappingPojo mapping = new RoleGroupRoleMappingPojo();
        mapping.setRoleGroupId(roleGroupId);
        mapping.setRoleId(roleId);

        RoleGroupRoleMappingPojo fullMapping = insertMapping(ParamWrapper.wrap("mapping", mapping));
        logger.debug("addRoleToGroupIfNotExists return={}", fullMapping);
        return fullMapping;
    }

    // ---------------- DELETE ----------------
    @Override
    public void deleteMappingById(Map<String, Object> params) {
        logger.debug("deleteMappingById START");
        logger.debug("deleteMappingById params={}", params);

        mapper.deleteMappingById(params);

        logger.debug("deleteMappingById DONE");
    }

    @Override
    public void deleteMappingByGroupAndRole(Map<String, Object> params) {
        logger.debug("deleteMappingByGroupAndRole START");
        logger.debug("deleteMappingByGroupAndRole params={}", params);

        mapper.deleteMappingByGroupAndRole(params);

        logger.debug("deleteMappingByGroupAndRole DONE");
    }

    // ---------------- READ ----------------
    @Override
    public PagedResult<RoleGroupRoleMappingPojo> findAllMappings() {
        logger.debug("findAllMappings START");

        List<RoleGroupRoleMappingPojo> items = mapper.findAllMappings();
        PagedResult<RoleGroupRoleMappingPojo> result = new PagedResult<>(items, items.size(), 1, items.size());

        logger.debug("findAllMappings return={}", result);
        return result;
    }

    @Override
    public PagedResult<RoleGroupRoleMappingPojo> findByRoleGroupId(Map<String, Object> params) {
        logger.debug("findByRoleGroupId START");
        logger.debug("findByRoleGroupId params={}", params);

        List<RoleGroupRoleMappingPojo> items = mapper.findByRoleGroupId(params);
        PagedResult<RoleGroupRoleMappingPojo> result = new PagedResult<>(items, items.size(), 1, items.size());

        logger.debug("findByRoleGroupId return={}", result);
        return result;
    }

    @Override
    public PagedResult<RoleGroupRoleMappingPojo> findByRoleId(Map<String, Object> params) {
        logger.debug("findByRoleId START");
        logger.debug("findByRoleId params={}", params);

        List<RoleGroupRoleMappingPojo> items = mapper.findByRoleId(params);
        PagedResult<RoleGroupRoleMappingPojo> result = new PagedResult<>(items, items.size(), 1, items.size());

        logger.debug("findByRoleId return={}", result);
        return result;
    }
    
    @Override
    public RoleGroupRoleMappingPojo findById(Map<String, Object> params) {
        logger.debug("findById START");
        logger.debug("findById params={}", params);

        if (params == null || !params.containsKey("id")) {
            logger.debug("findById: missing id param");
            return null;
        }

        RoleGroupRoleMappingPojo result = mapper.findById(params);

        logger.debug("findById return={}", result);
        return result;
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @Override
    public PagedResult<RoleGroupRoleMappingPojo> findMappings(Map<String, Object> params) {
        logger.debug("findMappings START");
        logger.debug("findMappings params={}", params);

        /*
          hung: DONT REMOVE THIS CODE
        */
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<RoleGroupRoleMappingPojo> items = mapper.findMappings(params);
        long totalCount = mapper.countMappings(params);

        PagedResult<RoleGroupRoleMappingPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("findMappings return={}", result);
        return result;
    }

    @Override
    public PagedResult<RoleGroupRoleMappingPojo> searchMappings(Map<String, Object> params) {
        logger.debug("searchMappings START");
        logger.debug("searchMappings params={}", params);

        /*
          hung: DONT REMOVE THIS CODE
        */
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<RoleGroupRoleMappingPojo> items = mapper.searchMappings(params);
        long totalCount = mapper.countMappings(params);

        PagedResult<RoleGroupRoleMappingPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("searchMappings return={}", result);
        return result;
    }

    // ---------------- COUNT ----------------
    @Override
    public long countMappings(Map<String, Object> params) {
        logger.debug("countMappings START");
        logger.debug("countMappings params={}", params);

        long count = mapper.countMappings(params);
        logger.debug("countMappings return={}", count);
        return count;
    }
}
