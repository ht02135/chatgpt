package simple.chatgpt.service.management.security;

import java.util.List;

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
    public List<UserManagementRoleGroupMappingPojo> findByUserId(Long userId) {
        logger.debug("findByUserId called, userId={}", userId);
        return userRoleGroupMappingCache.get(userId, k -> mappingMapper.findByUserId(k));
    }

    @Override
    public List<UserManagementRoleGroupMappingPojo> findByRoleGroupId(Long roleGroupId) {
        logger.debug("findByRoleGroupId called, roleGroupId={}", roleGroupId);
        return mappingMapper.findByRoleGroupId(roleGroupId);
    }

    @Override
    public UserManagementRoleGroupMappingPojo addUserToRoleGroup(UserManagementRoleGroupMappingPojo mapping) {
        logger.debug("addUserToRoleGroup called, mapping={}", mapping);
        mappingMapper.insertUserRoleGroup(mapping);
        userRoleGroupMappingCache.invalidate(mapping.getUserId());
        logger.debug("Inserted mapping and invalidated cache for userId={}", mapping.getUserId());
        return mapping;
    }

    @Override
    public void removeMappingById(Long id) {
        logger.debug("removeMappingById called, id={}", id);
        mappingMapper.deleteUserRoleGroupById(id);
        // cache invalidation handled if needed outside
    }

    @Override
    public void removeMappingByUserAndGroup(Long userId, Long roleGroupId) {
        logger.debug("removeMappingByUserAndGroup called, userId={} roleGroupId={}", userId, roleGroupId);
        mappingMapper.deleteUserRoleGroupByUserAndGroup(userId, roleGroupId);
        userRoleGroupMappingCache.invalidate(userId);
        logger.debug("Deleted mapping and invalidated cache for userId={}", userId);
    }

    // Additional helper for PageRoleGroupManagementServiceImpl initialization
    public void addUserRoleGroupMappingIfNotExists(Long roleGroupId, Long pageRoleGroupId) {
        logger.debug("addUserRoleGroupMappingIfNotExists called, roleGroupId={} pageRoleGroupId={}", roleGroupId, pageRoleGroupId);

        boolean exists = mappingMapper.findByRoleGroupId(roleGroupId).stream()
                .anyMatch(m -> pageRoleGroupId.equals(m.getPageRoleGroupId()));

        if (!exists) {
            UserManagementRoleGroupMappingPojo mapping = new UserManagementRoleGroupMappingPojo();
            mapping.setRoleGroupId(roleGroupId);
            mapping.setPageRoleGroupId(pageRoleGroupId);
            addUserToRoleGroup(mapping);
            logger.debug("Inserted new user-role group mapping: roleGroupId={} pageRoleGroupId={}", roleGroupId, pageRoleGroupId);
        } else {
            logger.debug("Mapping already exists, skipping insert: roleGroupId={} pageRoleGroupId={}", roleGroupId, pageRoleGroupId);
        }
    }
}
