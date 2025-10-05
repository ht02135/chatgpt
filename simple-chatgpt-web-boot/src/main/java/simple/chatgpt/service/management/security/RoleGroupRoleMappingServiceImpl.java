package simple.chatgpt.service.management.security;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.management.security.RoleGroupRoleMappingMapper;
import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.util.GenericCache;

@Service
public class RoleGroupRoleMappingServiceImpl implements RoleGroupRoleMappingService {

    private static final Logger logger = LogManager.getLogger(RoleGroupRoleMappingServiceImpl.class);

    private final RoleGroupRoleMappingMapper roleGroupRoleMappingMapper;
    private final GenericCache<Long, List<RoleGroupRoleMappingPojo>> roleGroupRoleMappingCache;

    @Autowired
    public RoleGroupRoleMappingServiceImpl(RoleGroupRoleMappingMapper roleGroupRoleMappingMapper,
                                           @Qualifier("roleGroupRoleMappingCache") GenericCache<Long, List<RoleGroupRoleMappingPojo>> roleGroupRoleMappingCache) {
        logger.debug("RoleGroupRoleMappingServiceImpl constructor called");
        logger.debug("RoleGroupRoleMappingServiceImpl roleGroupRoleMappingMapper={}", roleGroupRoleMappingMapper);
        logger.debug("RoleGroupRoleMappingServiceImpl roleGroupRoleMappingCache={}", roleGroupRoleMappingCache);

        this.roleGroupRoleMappingMapper = roleGroupRoleMappingMapper;
        this.roleGroupRoleMappingCache = roleGroupRoleMappingCache;
    }
    
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

    @Override
    public List<RoleGroupRoleMappingPojo> findAllMappings() {
        logger.debug("findAllMappings called");
        return roleGroupRoleMappingMapper.findAllMappings();
    }

    @Override
    public List<RoleGroupRoleMappingPojo> findByRoleGroupId(Long roleGroupId) {
        logger.debug("findByRoleGroupId called");
        logger.debug("findByRoleGroupId roleGroupId={}", roleGroupId);

        return roleGroupRoleMappingCache.get(roleGroupId, k -> {
            List<RoleGroupRoleMappingPojo> dbList = roleGroupRoleMappingMapper.findByRoleGroupId(k);
            logger.debug("findByRoleGroupId loaded from DB size={}", dbList.size());
            return dbList;
        });
    }

    @Override
    public List<RoleGroupRoleMappingPojo> findByRoleId(Long roleId) {
        logger.debug("findByRoleId called");
        logger.debug("findByRoleId roleId={}", roleId);

        return roleGroupRoleMappingMapper.findByRoleId(roleId);
    }

    @Override
    public RoleGroupRoleMappingPojo addRoleToGroup(RoleGroupRoleMappingPojo mapping) {
        logger.debug("addRoleToGroup called");
        logger.debug("addRoleToGroup mapping={}", mapping);

        roleGroupRoleMappingMapper.insertMapping(mapping);
        logger.debug("addRoleToGroup inserted mapping id={}", mapping.getId());

        // invalidate cache for this role group
        roleGroupRoleMappingCache.invalidate(mapping.getRoleGroupId());
        logger.debug("addRoleToGroup invalidated cache for roleGroupId={}", mapping.getRoleGroupId());

        return mapping;
    }

    @Override
    public void removeMappingById(Long id) {
        logger.debug("removeMappingById called");
        logger.debug("removeMappingById id={}", id);

        RoleGroupRoleMappingPojo mapping = roleGroupRoleMappingMapper.findAllMappings().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst().orElse(null);

        if (mapping != null) {
            roleGroupRoleMappingMapper.deleteMappingById(id);
            logger.debug("removeMappingById deleted mapping id={}", id);
            roleGroupRoleMappingCache.invalidate(mapping.getRoleGroupId());
            logger.debug("removeMappingById invalidated cache for roleGroupId={}", mapping.getRoleGroupId());
        }
    }

    @Override
    public void removeMappingByGroupAndRole(Long roleGroupId, Long roleId) {
        logger.debug("removeMappingByGroupAndRole called");
        logger.debug("removeMappingByGroupAndRole roleGroupId={} roleId={}", roleGroupId, roleId);

        roleGroupRoleMappingMapper.deleteMappingByGroupAndRole(roleGroupId, roleId);
        logger.debug("removeMappingByGroupAndRole deleted mapping");

        roleGroupRoleMappingCache.invalidate(roleGroupId);
        logger.debug("removeMappingByGroupAndRole invalidated cache for roleGroupId={}", roleGroupId);
    }
    
    public RoleGroupRoleMappingPojo addRoleToGroupIfNotExists(Long roleGroupId, Long roleId) {
        logger.debug("addRoleToGroupIfNotExists called");
        logger.debug("addRoleToGroupIfNotExists roleGroupId={} roleId={}", roleGroupId, roleId);

        // 1️⃣ Check existing mappings for this group
        List<RoleGroupRoleMappingPojo> existingMappings = findByRoleGroupId(roleGroupId);

        boolean mappingExists = existingMappings.stream()
                .anyMatch(m -> roleId.equals(m.getRoleId()));

        if (mappingExists) {
            logger.debug("Mapping already exists, skipping insert roleGroupId={} roleId={}", roleGroupId, roleId);
            // Return existing mapping if needed, else null
            return existingMappings.stream()
                    .filter(m -> roleId.equals(m.getRoleId()))
                    .findFirst()
                    .orElse(null);
        }

        // 2️⃣ Mapping does not exist, create new
        RoleGroupRoleMappingPojo mappingPojo = new RoleGroupRoleMappingPojo();
        mappingPojo.setRoleGroupId(roleGroupId);
        mappingPojo.setRoleId(roleId);

        addRoleToGroup(mappingPojo); // uses existing method which also invalidates cache
        logger.debug("Inserted new mapping roleGroupId={} roleId={}", roleGroupId, roleId);

        return mappingPojo;
    }

}
