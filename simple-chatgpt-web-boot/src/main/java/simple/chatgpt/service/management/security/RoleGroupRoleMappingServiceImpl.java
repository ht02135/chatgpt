package simple.chatgpt.service.management.security;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.management.security.RoleGroupRoleMappingMapper;
import simple.chatgpt.mapper.management.security.UserManagementRoleGroupMappingMapper;
import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.util.GenericCache;

@Service
public class RoleGroupRoleMappingServiceImpl implements RoleGroupRoleMappingService {

    private static final Logger logger = LogManager.getLogger(RoleGroupRoleMappingServiceImpl.class);

    private final RoleGroupRoleMappingMapper roleGroupRoleMappingMapper;
    private final UserManagementRoleGroupMappingMapper userRoleGroupMappingMapper;
    private final GenericCache<Long, List<RoleGroupRoleMappingPojo>> roleGroupRoleMappingCache;
    private final GenericCache<Long, List<UserManagementRoleGroupMappingPojo>> userRoleGroupMappingCache;

    @Autowired
    public RoleGroupRoleMappingServiceImpl(RoleGroupRoleMappingMapper roleGroupRoleMappingMapper,
                                           UserManagementRoleGroupMappingMapper userRoleGroupMappingMapper,
                                           @Qualifier("roleGroupRoleMappingCache") GenericCache<Long, List<RoleGroupRoleMappingPojo>> roleGroupRoleMappingCache,
                                           @Qualifier("userRoleGroupMappingCache") GenericCache<Long, List<UserManagementRoleGroupMappingPojo>> userRoleGroupMappingCache) {
        logger.debug("RoleGroupRoleMappingServiceImpl constructor called");
        logger.debug("RoleGroupRoleMappingServiceImpl roleGroupRoleMappingMapper={}", roleGroupRoleMappingMapper);
        logger.debug("RoleGroupRoleMappingServiceImpl userRoleGroupMappingMapper={}", userRoleGroupMappingMapper);
        logger.debug("RoleGroupRoleMappingServiceImpl roleGroupRoleMappingCache={}", roleGroupRoleMappingCache);
        logger.debug("RoleGroupRoleMappingServiceImpl userRoleGroupMappingCache={}", userRoleGroupMappingCache);

        this.roleGroupRoleMappingMapper = roleGroupRoleMappingMapper;
        this.userRoleGroupMappingMapper = userRoleGroupMappingMapper;
        this.roleGroupRoleMappingCache = roleGroupRoleMappingCache;
        this.userRoleGroupMappingCache = userRoleGroupMappingCache;
    }

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
}
