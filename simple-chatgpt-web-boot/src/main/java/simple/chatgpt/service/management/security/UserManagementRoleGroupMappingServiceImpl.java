package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.management.security.UserManagementRoleGroupMappingMapper;
import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.SafeConverter;

@Service
public class UserManagementRoleGroupMappingServiceImpl implements UserManagementRoleGroupMappingService {

    private static final Logger logger = LogManager.getLogger(UserManagementRoleGroupMappingServiceImpl.class);

    private final UserManagementRoleGroupMappingMapper mappingMapper;

    @Autowired
    public UserManagementRoleGroupMappingServiceImpl(UserManagementRoleGroupMappingMapper mappingMapper) {
        logger.debug("UserManagementRoleGroupMappingServiceImpl constructor called");
        logger.debug("mappingMapper={}", mappingMapper);

        this.mappingMapper = mappingMapper;
    }

    // ---------------- CREATE ----------------
    @Override
    public UserManagementRoleGroupMappingPojo insertUserRoleGroup(Map<String, Object> params) {
        UserManagementRoleGroupMappingPojo mapping = ParamWrapper.unwrap(params, "mapping");

        logger.debug("insertUserRoleGroup called, mapping={}", mapping);
        mappingMapper.insertUserRoleGroup(ParamWrapper.wrap("mapping", mapping));

        // Re-fetch full mapping from DB
        UserManagementRoleGroupMappingPojo fullMapping = mappingMapper.findById(
                ParamWrapper.wrap("id", mapping.getId())
        );
        logger.debug("insertUserRoleGroup re-fetched fullMapping={}", fullMapping);

        return fullMapping;
    }

    // ---------------- UPDATE ----------------
    @Override
    public UserManagementRoleGroupMappingPojo updateUserRoleGroup(Map<String, Object> params) {
        UserManagementRoleGroupMappingPojo mapping = ParamWrapper.unwrap(params, "mapping");
        logger.debug("updateUserRoleGroup called, mapping={}", mapping);

        mappingMapper.updateUserRoleGroup(ParamWrapper.wrap("mapping", mapping));
        return mapping;
    }

    // ---------------- DELETE ----------------
    @Override
    public void deleteUserRoleGroupById(Map<String, Object> params) {
        Long id = ParamWrapper.unwrap(params, "id");
        logger.debug("deleteUserRoleGroupById called, id={}", id);

        mappingMapper.deleteUserRoleGroupById(ParamWrapper.wrap("id", id));
    }

    @Override
    public void deleteUserRoleGroupByUserAndGroup(Map<String, Object> params) {
        Long userId = ParamWrapper.unwrap(params, "userId");
        Long roleGroupId = ParamWrapper.unwrap(params, "roleGroupId");
        logger.debug("deleteUserRoleGroupByUserAndGroup called, userId={}, roleGroupId={}", userId, roleGroupId);

        mappingMapper.deleteUserRoleGroupByUserAndGroup(
                ParamWrapper.wrap("userId", userId, "roleGroupId", roleGroupId)
        );
    }

    // ---------------- READ ----------------
    @Override
    public UserManagementRoleGroupMappingPojo findById(Map<String, Object> params) {
        Long id = ParamWrapper.unwrap(params, "id");
        logger.debug("findById called, id={}", id);

        return mappingMapper.findById(ParamWrapper.wrap("id", id));
    }

    @Override
    public UserManagementRoleGroupMappingPojo findByUserIdAndRoleGroupId(Map<String, Object> params) {
        Long userId = ParamWrapper.unwrap(params, "userId");
        Long roleGroupId = ParamWrapper.unwrap(params, "roleGroupId");
        logger.debug("findByUserIdAndRoleGroupId called, userId={}, roleGroupId={}", userId, roleGroupId);

        return mappingMapper.findByUserIdAndRoleGroupId(
                ParamWrapper.wrap("userId", userId, "roleGroupId", roleGroupId)
        );
    }

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
        Long userId = ParamWrapper.unwrap(params, "userId");
        logger.debug("findByUserId called, userId={}", userId);

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.findByUserId(
                ParamWrapper.wrap("userId", userId)
        );
        long totalCount = items.size();

        logger.debug("findByUserId result size={}", items.size());
        return new PagedResult<>(items, totalCount, 1, (int) totalCount);
    }

    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> findByRoleGroupId(Map<String, Object> params) {
        Long roleGroupId = ParamWrapper.unwrap(params, "roleGroupId");
        logger.debug("findByRoleGroupId called, roleGroupId={}", roleGroupId);

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.findByRoleGroupId(
                ParamWrapper.wrap("roleGroupId", roleGroupId)
        );
        long totalCount = items.size();

        logger.debug("findByRoleGroupId result size={}", items.size());
        return new PagedResult<>(items, totalCount, 1, (int) totalCount);
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> findUserRoleGroups(Map<String, Object> params) {
        logger.debug("findUserRoleGroups called, params={}", params);

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.findUserRoleGroups(
                ParamWrapper.wrap(params)
        );
        long totalCount = mappingMapper.countUserRoleGroups(ParamWrapper.wrap(params));

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0); 
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        logger.debug("findUserRoleGroups result size={}", items.size());
        return new PagedResult<>(items, totalCount, page, size);
    }

    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> searchUserRoleGroups(Map<String, Object> params) {
        logger.debug("searchUserRoleGroups called, params={}", params);

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.searchUserRoleGroups(
                ParamWrapper.wrap(params)
        );
        long totalCount = mappingMapper.countUserRoleGroups(ParamWrapper.wrap(params));

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0); 
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        logger.debug("searchUserRoleGroups result size={}", items.size());
        return new PagedResult<>(items, totalCount, page, size);
    }

    // ---------------- COUNT ----------------
    @Override
    public long countUserRoleGroups(Map<String, Object> params) {
        logger.debug("countUserRoleGroups called, params={}", params);
        return mappingMapper.countUserRoleGroups(ParamWrapper.wrap(params));
    }
}
