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
        logger.debug("UserManagementRoleGroupMappingServiceImpl: START");
        logger.debug("UserManagementRoleGroupMappingServiceImpl: mappingMapper={}", mappingMapper);
        this.mappingMapper = mappingMapper;
        logger.debug("UserManagementRoleGroupMappingServiceImpl: DONE");
    }

    // ---------------- CREATE ----------------
    @Override
    public UserManagementRoleGroupMappingPojo insertUserRoleGroup(Map<String, Object> params) {
        logger.debug("insertUserRoleGroup: START");
        logger.debug("insertUserRoleGroup: params={}", params);

        UserManagementRoleGroupMappingPojo mapping = ParamWrapper.unwrap(params, "mapping");
        if (mapping == null) {
            logger.error("insertUserRoleGroup: mapping is null");
            throw new IllegalArgumentException("Missing mapping payload");
        }

        // ✅ Insert the object
        mappingMapper.insertUserRoleGroup(ParamWrapper.wrap("mapping", mapping));
        logger.debug("insertUserRoleGroup: mapping inserted, id={}", mapping.getId());

        // Optional: fetch full object if database sets additional fields
        UserManagementRoleGroupMappingPojo fullMapping = mappingMapper.findById(
                ParamWrapper.wrap("id", mapping.getId())
        );

        logger.debug("insertUserRoleGroup: fullMapping={}", fullMapping);
        return fullMapping;
    }

    // ---------------- UPDATE ----------------
    @Override
    public UserManagementRoleGroupMappingPojo updateUserRoleGroup(Map<String, Object> params) {
        logger.debug("updateUserRoleGroup: START");
        logger.debug("updateUserRoleGroup: params={}", params);

        // Extract the mapping POJO
        UserManagementRoleGroupMappingPojo mapping = ParamWrapper.unwrap(params, "mapping");
        if (mapping == null || mapping.getId() == null) {
            throw new IllegalArgumentException("Mapping or mapping id is missing");
        }

        // Delete old mapping by id
        mappingMapper.deleteUserRoleGroupById(ParamWrapper.wrap("id", mapping.getId()));
        logger.debug("updateUserRoleGroup: old mapping deleted id={}", mapping.getId());

        // Insert new mapping
        mappingMapper.insertUserRoleGroup(ParamWrapper.wrap("mapping", mapping));
        logger.debug("updateUserRoleGroup: new mapping inserted={}", mapping);

        return mapping;
    }

    // ---------------- DELETE ----------------
    @Override
    public void deleteUserRoleGroupById(Map<String, Object> params) {
        logger.debug("deleteUserRoleGroupById: START");
        logger.debug("deleteUserRoleGroupById: params={}", params);

        Long id = ParamWrapper.unwrap(params, "id");
        mappingMapper.deleteUserRoleGroupById(ParamWrapper.wrap("id", id));

        logger.debug("deleteUserRoleGroupById: DONE");
    }

    @Override
    public void deleteUserRoleGroupByUserAndGroup(Map<String, Object> params) {
        logger.debug("deleteUserRoleGroupByUserAndGroup: START");
        logger.debug("deleteUserRoleGroupByUserAndGroup: params={}", params);

        Long userId = ParamWrapper.unwrap(params, "userId");
        Long roleGroupId = ParamWrapper.unwrap(params, "roleGroupId");

        mappingMapper.deleteUserRoleGroupByUserAndGroup(
                ParamWrapper.wrap("userId", userId, "roleGroupId", roleGroupId)
        );

        logger.debug("deleteUserRoleGroupByUserAndGroup: DONE");
    }

    // ---------------- READ ----------------
    @Override
    public UserManagementRoleGroupMappingPojo findById(Map<String, Object> params) {
        logger.debug("findById: START");
        logger.debug("findById: params={}", params);

        Long id = ParamWrapper.unwrap(params, "id");
        UserManagementRoleGroupMappingPojo result = mappingMapper.findById(ParamWrapper.wrap("id", id));

        logger.debug("findById: result={}", result);
        return result;
    }

    @Override
    public UserManagementRoleGroupMappingPojo findByUserIdAndRoleGroupId(Map<String, Object> params) {
        logger.debug("findByUserIdAndRoleGroupId: START");
        logger.debug("findByUserIdAndRoleGroupId: params={}", params);

        Long userId = ParamWrapper.unwrap(params, "userId");
        Long roleGroupId = ParamWrapper.unwrap(params, "roleGroupId");

        UserManagementRoleGroupMappingPojo result = mappingMapper.findByUserIdAndRoleGroupId(
                ParamWrapper.wrap("userId", userId, "roleGroupId", roleGroupId)
        );

        logger.debug("findByUserIdAndRoleGroupId: result={}", result);
        return result;
    }

    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> findAllUserRoleGroups() {
        logger.debug("findAllUserRoleGroups: START");

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.findAllUserRoleGroups();
        long totalCount = items.size();

        PagedResult<UserManagementRoleGroupMappingPojo> result =
                new PagedResult<>(items, totalCount, 1, (int) totalCount);

        logger.debug("findAllUserRoleGroups: result={}", result);
        return result;
    }

    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> findByUserId(Map<String, Object> params) {
        logger.debug("findByUserId: START");
        logger.debug("findByUserId: params={}", params);

        Long userId = ParamWrapper.unwrap(params, "userId");
        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.findByUserId(
                ParamWrapper.wrap("userId", userId)
        );
        long totalCount = items.size();

        PagedResult<UserManagementRoleGroupMappingPojo> result =
                new PagedResult<>(items, totalCount, 1, (int) totalCount);

        logger.debug("findByUserId: result={}", result);
        return result;
    }

    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> findByRoleGroupId(Map<String, Object> params) {
        logger.debug("findByRoleGroupId: START");
        logger.debug("findByRoleGroupId: params={}", params);

        Long roleGroupId = ParamWrapper.unwrap(params, "roleGroupId");
        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.findByRoleGroupId(
                ParamWrapper.wrap("roleGroupId", roleGroupId)
        );
        long totalCount = items.size();

        PagedResult<UserManagementRoleGroupMappingPojo> result =
                new PagedResult<>(items, totalCount, 1, (int) totalCount);

        logger.debug("findByRoleGroupId: result={}", result);
        return result;
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> findUserRoleGroups(Map<String, Object> params) {
        logger.debug("findUserRoleGroups: START");
        logger.debug("findUserRoleGroups: params={}", params);

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.findUserRoleGroups(
                ParamWrapper.wrap(params)
        );
        long totalCount = mappingMapper.countUserRoleGroups(ParamWrapper.wrap(params));

        /*
          hung: DONT REMOVE THIS CODE
        */
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        PagedResult<UserManagementRoleGroupMappingPojo> result =
                new PagedResult<>(items, totalCount, page, size);

        logger.debug("findUserRoleGroups: result={}", result);
        return result;
    }

    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> searchUserRoleGroups(Map<String, Object> params) {
        logger.debug("searchUserRoleGroups: START");
        logger.debug("searchUserRoleGroups: params={}", params);

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.searchUserRoleGroups(
                ParamWrapper.wrap(params)
        );
        long totalCount = mappingMapper.countUserRoleGroups(ParamWrapper.wrap(params));

        /*
          hung: DONT REMOVE THIS CODE
        */
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        PagedResult<UserManagementRoleGroupMappingPojo> result =
                new PagedResult<>(items, totalCount, page, size);

        logger.debug("searchUserRoleGroups: result={}", result);
        return result;
    }

    // ---------------- COUNT ----------------
    @Override
    public long countUserRoleGroups(Map<String, Object> params) {
        logger.debug("countUserRoleGroups: START");
        logger.debug("countUserRoleGroups: params={}", params);

        long count = mappingMapper.countUserRoleGroups(ParamWrapper.wrap(params));

        logger.debug("countUserRoleGroups: count={}", count);
        return count;
    }
}
