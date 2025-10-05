package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

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

    private final RoleGroupRoleMappingMapper mapper;
    private final GenericCache<Long, List<RoleGroupRoleMappingPojo>> cache;

    @Autowired
    public RoleGroupRoleMappingServiceImpl(RoleGroupRoleMappingMapper mapper,
                                           @Qualifier("roleGroupRoleMappingCache") GenericCache<Long, List<RoleGroupRoleMappingPojo>> cache) {
        logger.debug("RoleGroupRoleMappingServiceImpl constructor called");
        logger.debug("mapper={}", mapper);
        logger.debug("cache={}", cache);

        this.mapper = mapper;
        this.cache = cache;
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
    public List<RoleGroupRoleMappingPojo> findAllMappings(Map<String, Object> params) {
        logger.debug("findAllMappings called params={}", params);
        return mapper.findMappings(Map.of("params", params));
    }

    @Override
    public List<RoleGroupRoleMappingPojo> findByRoleGroup(Map<String, Object> params) {
        Long roleGroupId = (Long) params.get("roleGroupId");
        logger.debug("findByRoleGroup called roleGroupId={}", roleGroupId);

        return cache.get(roleGroupId, k -> {
            List<RoleGroupRoleMappingPojo> dbList = mapper.findByRoleGroupId(Map.of("params", Map.of("roleGroupId", k)));
            logger.debug("findByRoleGroup loaded from DB size={}", dbList.size());
            return dbList;
        });
    }

    @Override
    public List<RoleGroupRoleMappingPojo> findByRole(Map<String, Object> params) {
        Long roleId = (Long) params.get("roleId");
        logger.debug("findByRole called roleId={}", roleId);

        return mapper.findByRoleId(Map.of("params", Map.of("roleId", roleId)));
    }

    @Override
    public RoleGroupRoleMappingPojo addRoleToGroup(Map<String, Object> params) {
        RoleGroupRoleMappingPojo mapping = (RoleGroupRoleMappingPojo) params.get("mapping");
        logger.debug("addRoleToGroup called mapping={}", mapping);

        mapper.insertMapping(Map.of("params", Map.of("mapping", mapping)));
        logger.debug("addRoleToGroup inserted mapping id={}", mapping.getId());

        cache.invalidate(mapping.getRoleGroupId());
        logger.debug("addRoleToGroup invalidated cache for roleGroupId={}", mapping.getRoleGroupId());
        return mapping;
    }

    @Override
    public RoleGroupRoleMappingPojo addRoleToGroupIfNotExists(Map<String, Object> params) {
        Long roleGroupId = (Long) params.get("roleGroupId");
        Long roleId = (Long) params.get("roleId");
        logger.debug("addRoleToGroupIfNotExists called roleGroupId={} roleId={}", roleGroupId, roleId);

        List<RoleGroupRoleMappingPojo> existingMappings = findByRoleGroup(Map.of("roleGroupId", roleGroupId));
        RoleGroupRoleMappingPojo existing = existingMappings.stream()
                .filter(m -> roleId.equals(m.getRoleId()))
                .findFirst().orElse(null);

        if (existing != null) {
            logger.debug("Mapping already exists, skipping insert roleGroupId={} roleId={}", roleGroupId, roleId);
            return existing;
        }

        RoleGroupRoleMappingPojo mapping = new RoleGroupRoleMappingPojo();
        mapping.setRoleGroupId(roleGroupId);
        mapping.setRoleId(roleId);

        return addRoleToGroup(Map.of("mapping", mapping));
    }

    @Override
    public void removeMapping(Map<String, Object> params) {
        logger.debug("removeMapping called params={}", params);

        if (params.containsKey("id")) {
            Long id = (Long) params.get("id");
            RoleGroupRoleMappingPojo mapping = findAllMappings(Map.of()).stream()
                    .filter(m -> id.equals(m.getId()))
                    .findFirst().orElse(null);

            if (mapping != null) {
                mapper.deleteMappingById(Map.of("params", Map.of("id", id)));
                logger.debug("removeMapping deleted mapping id={}", id);
                cache.invalidate(mapping.getRoleGroupId());
                logger.debug("removeMapping invalidated cache for roleGroupId={}", mapping.getRoleGroupId());
            }
        } else if (params.containsKey("roleGroupId") && params.containsKey("roleId")) {
            Long roleGroupId = (Long) params.get("roleGroupId");
            Long roleId = (Long) params.get("roleId");

            mapper.deleteMappingByGroupAndRole(Map.of("params", Map.of("roleGroupId", roleGroupId, "roleId", roleId)));
            logger.debug("removeMapping deleted mapping roleGroupId={} roleId={}", roleGroupId, roleId);
            cache.invalidate(roleGroupId);
            logger.debug("removeMapping invalidated cache for roleGroupId={}", roleGroupId);
        }
    }
}
