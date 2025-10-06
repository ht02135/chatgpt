package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.management.security.RoleGroupRoleMappingMapper;
import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;

@Service
public class RoleGroupRoleMappingServiceImpl implements RoleGroupRoleMappingService {

    private static final Logger logger = LogManager.getLogger(RoleGroupRoleMappingServiceImpl.class);

    private final RoleGroupRoleMappingMapper mapper;

    /*
    hung: Note on initialization

    RoleGroupRoleMappingServiceImpl does NOT need an initializeDB() method because:
    1️ Initial loading of role-group → role mappings from configuration XML
       is handled by RoleGroupManagementServiceImpl.initializeDB().
    2️ RoleGroupManagementServiceImpl uses RoleGroupRoleMappingService
       methods (addRoleToGroupIfNotExists) to populate the DB and cache.
    3️ RoleGroupRoleMappingServiceImpl relies on its cache and mapper for
       runtime operations (find, add, remove). Any updates made through the
       service automatically manage caching.
    4️ Adding an initDB here would duplicate logic, risk inconsistent state,
       and violate single responsibility principle.

    In short, this service focuses on CRUD and caching, while RoleGroupManagementServiceImpl
    handles initial bootstrapping of mappings.
    */
    
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

        Number roleGroupIdNum = (Number) params.get("roleGroupId");
        Number roleIdNum = (Number) params.get("roleId");

        if (roleGroupIdNum == null || roleIdNum == null) {
            logger.warn("addRoleToGroupIfNotExists missing roleGroupId or roleId, params={}", params);
            return null;
        }

        Long roleGroupId = roleGroupIdNum.longValue();
        Long roleId = roleIdNum.longValue();

        logger.debug("addRoleToGroupIfNotExists called roleGroupId={} roleId={}", roleGroupId, roleId);

        // 1️⃣ Check existing mappings using service method
        List<RoleGroupRoleMappingPojo> existingMappings = findByRoleGroupId(Map.of("roleGroupId", roleGroupId));

        RoleGroupRoleMappingPojo existing = existingMappings.stream()
                .filter(m -> roleId.equals(m.getRoleId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            logger.debug("Mapping already exists, skipping insert roleGroupId={} roleId={}", roleGroupId, roleId);
            return existing;
        }

        // 2️⃣ Not found → create new mapping using mapper
        RoleGroupRoleMappingPojo mapping = new RoleGroupRoleMappingPojo();
        mapping.setRoleGroupId(roleGroupId);
        mapping.setRoleId(roleId);

        // Insert via mapper and ensure generated ID is populated
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
    public List<RoleGroupRoleMappingPojo> findAllMappings() {
        logger.debug("findAllMappings called");
        return mapper.findAllMappings();
    }

    @Override
    public List<RoleGroupRoleMappingPojo> findByRoleGroupId(Map<String, Object> params) {
        logger.debug("findByRoleGroupId called");
        logger.debug("findByRoleGroupId params={}", params);
        return mapper.findByRoleGroupId(params);
    }

    @Override
    public List<RoleGroupRoleMappingPojo> findByRoleId(Map<String, Object> params) {
        logger.debug("findByRoleId called");
        logger.debug("findByRoleId params={}", params);
        return mapper.findByRoleId(params);
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @Override
    public List<RoleGroupRoleMappingPojo> findMappings(Map<String, Object> params) {
        logger.debug("findMappings called");
        logger.debug("findMappings params={}", params);
        return mapper.findMappings(params);
    }

    @Override
    public List<RoleGroupRoleMappingPojo> searchMappings(Map<String, Object> params) {
        logger.debug("searchMappings called");
        logger.debug("searchMappings params={}", params);
        return mapper.searchMappings(params);
    }

    // ---------------- COUNT ----------------
    @Override
    public long countMappings(Map<String, Object> params) {
        logger.debug("countMappings called");
        logger.debug("countMappings params={}", params);
        return mapper.countMappings(params);
    }
}
