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
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;

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

    // ---------------- CREATE ----------------
    @Override
    public UserManagementRoleGroupMappingPojo insertUserRoleGroup(Map<String, Object> params) {
        UserManagementRoleGroupMappingPojo mapping = (UserManagementRoleGroupMappingPojo) params.get("mapping");
        logger.debug("insertUserRoleGroup called, mapping={}", mapping);

        mappingMapper.insertUserRoleGroup(ParamWrapper.wrap("params", ParamWrapper.wrap("mapping", mapping)));
        userRoleGroupMappingCache.invalidate(mapping.getUserId());
        logger.debug("Inserted mapping and invalidated cache for userId={}", mapping.getUserId());

        return mapping;
    }

    // ---------------- UPDATE ----------------
    @Override
    public UserManagementRoleGroupMappingPojo updateUserRoleGroup(Map<String, Object> params) {
        UserManagementRoleGroupMappingPojo mapping = (UserManagementRoleGroupMappingPojo) params.get("mapping");
        logger.debug("updateUserRoleGroup called, mapping={}", mapping);

        mappingMapper.updateUserRoleGroup(ParamWrapper.wrap("params", ParamWrapper.wrap("mapping", mapping)));
        userRoleGroupMappingCache.invalidate(mapping.getUserId());
        logger.debug("Updated mapping and invalidated cache for userId={}", mapping.getUserId());

        return mapping;
    }

    // ---------------- DELETE ----------------
    @Override
    public void deleteUserRoleGroupById(Map<String, Object> params) {
        Long id = (Long) params.get("id");
        logger.debug("deleteUserRoleGroupById called, id={}", id);

        mappingMapper.deleteUserRoleGroupById(ParamWrapper.wrap("params", ParamWrapper.wrap("id", id)));
    }

    @Override
    public void deleteUserRoleGroupByUserAndGroup(Map<String, Object> params) {
        Long userId = (Long) params.get("userId");
        Long roleGroupId = (Long) params.get("roleGroupId");
        logger.debug("deleteUserRoleGroupByUserAndGroup called, userId={} roleGroupId={}", userId, roleGroupId);

        mappingMapper.deleteUserRoleGroupByUserAndGroup(ParamWrapper.wrap("params", ParamWrapper.wrap("userId", userId, "roleGroupId", roleGroupId)));
        userRoleGroupMappingCache.invalidate(userId);
        logger.debug("Deleted mapping and invalidated cache for userId={}", userId);
    }

    // ---------------- READ ----------------
    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> findAllUserRoleGroups() {
        logger.debug("findAllUserRoleGroups called");

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.findAllUserRoleGroups();
        long totalCount = items.size();

        logger.debug("findAllUserRoleGroups result size={}", items.size());
        return new PagedResult<>(items, totalCount, 1, (int) totalCount);
    }

    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> findByUserId(Map<String, Object> params) {
        Long userId = (Long) params.get("userId");
        logger.debug("findByUserId called, userId={}", userId);

        List<UserManagementRoleGroupMappingPojo> items = userRoleGroupMappingCache.get(userId,
                k -> mappingMapper.findByUserId(ParamWrapper.wrap("params", ParamWrapper.wrap("userId", k))));
        long totalCount = items.size();

        logger.debug("findByUserId result size={}", items.size());
        return new PagedResult<>(items, totalCount, 1, (int) totalCount);
    }

    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> findByRoleGroupId(Map<String, Object> params) {
        Long roleGroupId = (Long) params.get("roleGroupId");
        logger.debug("findByRoleGroupId called, roleGroupId={}", roleGroupId);

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.findByRoleGroupId(ParamWrapper.wrap("params", ParamWrapper.wrap("roleGroupId", roleGroupId)));
        long totalCount = items.size();

        logger.debug("findByRoleGroupId result size={}", items.size());
        return new PagedResult<>(items, totalCount, 1, (int) totalCount);
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> findUserRoleGroups(Map<String, Object> params) {
        logger.debug("findUserRoleGroups called, params={}", params);

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.findUserRoleGroups(ParamWrapper.wrap("params", params));
        long totalCount = mappingMapper.countUserRoleGroups(ParamWrapper.wrap("params", params));

        int page = (int) params.getOrDefault("page", 1);
        int size = (int) params.getOrDefault("size", 20);

        logger.debug("findUserRoleGroups result size={}", items.size());
        return new PagedResult<>(items, totalCount, page, size);
    }

    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> searchUserRoleGroups(Map<String, Object> params) {
        logger.debug("searchUserRoleGroups called, params={}", params);

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.searchUserRoleGroups(ParamWrapper.wrap("params", params));
        long totalCount = mappingMapper.countUserRoleGroups(ParamWrapper.wrap("params", params));

        int page = (int) params.getOrDefault("page", 1);
        int size = (int) params.getOrDefault("size", 20);

        logger.debug("searchUserRoleGroups result size={}", items.size());
        return new PagedResult<>(items, totalCount, page, size);
    }

    // ---------------- COUNT ----------------
    @Override
    public long countUserRoleGroups(Map<String, Object> params) {
        logger.debug("countUserRoleGroups called, params={}", params);
        return mappingMapper.countUserRoleGroups(ParamWrapper.wrap("params", params));
    }
}
