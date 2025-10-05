package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.management.security.UserManagementRoleGroupMappingMapper;
import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.util.GenericCache;

@Service
public class UserManagementRoleGroupMappingServiceImpl implements UserManagementRoleGroupMappingService {

    private static final Logger logger = LogManager.getLogger(UserManagementRoleGroupMappingServiceImpl.class);

    private final UserManagementRoleGroupMappingMapper mappingMapper;
    private final GenericCache<Long, List<UserManagementRoleGroupMappingPojo>> userRoleGroupMappingCache;

    @Autowired
    public UserManagementRoleGroupMappingServiceImpl(
            UserManagementRoleGroupMappingMapper mappingMapper,
            @Qualifier("userRoleGroupMappingCache") GenericCache<Long, List<UserManagementRoleGroupMappingPojo>> userRoleGroupMappingCache) {

        logger.debug("UserManagementRoleGroupMappingServiceImpl constructor called");
        logger.debug("mappingMapper={}", mappingMapper);
        logger.debug("userRoleGroupMappingCache={}", userRoleGroupMappingCache);

        this.mappingMapper = mappingMapper;
        this.userRoleGroupMappingCache = userRoleGroupMappingCache;
    }

    @Override
    public List<UserManagementRoleGroupMappingPojo> findAll() {
        logger.debug("findAll called");
        return mappingMapper.findAllUserRoleGroups();
    }

    @Override
    public List<UserManagementRoleGroupMappingPojo> findByUserId(Map<String, Object> params) {
        Long userId = (Long) params.get("userId");
        logger.debug("findByUserId called, userId={}", userId);

        return userRoleGroupMappingCache.get(userId, k -> mappingMapper.findByUserId(Map.of("params", Map.of("userId", k))));
    }

    @Override
    public List<UserManagementRoleGroupMappingPojo> findByRoleGroupId(Map<String, Object> params) {
        Long roleGroupId = (Long) params.get("roleGroupId");
        logger.debug("findByRoleGroupId called, roleGroupId={}", roleGroupId);

        return mappingMapper.findByRoleGroupId(Map.of("params", Map.of("roleGroupId", roleGroupId)));
    }

    @Override
    public UserManagementRoleGroupMappingPojo addUserToRoleGroup(Map<String, Object> params) {
        UserManagementRoleGroupMappingPojo mapping = (UserManagementRoleGroupMappingPojo) params.get("mapping");
        logger.debug("addUserToRoleGroup called, mapping={}", mapping);

        mappingMapper.insertUserRoleGroup(Map.of("params", Map.of("mapping", mapping)));
        userRoleGroupMappingCache.invalidate(mapping.getUserId());
        logger.debug("Inserted mapping and invalidated cache for userId={}", mapping.getUserId());

        return mapping;
    }

    @Override
    public void removeMappingById(Map<String, Object> params) {
        Long id = (Long) params.get("id");
        logger.debug("removeMappingById called, id={}", id);

        mappingMapper.deleteUserRoleGroupById(Map.of("params", Map.of("id", id)));
    }

    @Override
    public void removeMappingByUserAndGroup(Map<String, Object> params) {
        Long userId = (Long) params.get("userId");
        Long roleGroupId = (Long) params.get("roleGroupId");
        logger.debug("removeMappingByUserAndGroup called, userId={} roleGroupId={}", userId, roleGroupId);

        mappingMapper.deleteUserRoleGroupByUserAndGroup(Map.of("params", Map.of("userId", userId, "roleGroupId", roleGroupId)));
        userRoleGroupMappingCache.invalidate(userId);
        logger.debug("Deleted mapping and invalidated cache for userId={}", userId);
    }
}
