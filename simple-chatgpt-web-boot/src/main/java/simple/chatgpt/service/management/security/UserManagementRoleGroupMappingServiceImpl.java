package simple.chatgpt.service.management.security;

import java.util.HashMap;
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
        logger.debug("UserManagementRoleGroupMappingServiceImpl constructor START");
        logger.debug("UserManagementRoleGroupMappingServiceImpl mappingMapper={}", mappingMapper);
        this.mappingMapper = mappingMapper;
        logger.debug("UserManagementRoleGroupMappingServiceImpl constructor DONE");
    }

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @Override
    public UserManagementRoleGroupMappingPojo create(UserManagementRoleGroupMappingPojo mapping) {
        logger.debug("create called");
        logger.debug("create mapping={}", mapping);
        mappingMapper.create(mapping);
        return mapping;
    }

    @Override
    public UserManagementRoleGroupMappingPojo update(Long id, UserManagementRoleGroupMappingPojo mapping) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update mapping={}", mapping);
        mappingMapper.update(id, mapping);
        return mapping;
    }

    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> search(Map<String, String> params) {
        logger.debug("search called");
        logger.debug("search params={}", params);

        if (!params.containsKey("page")) params.put("page", "0");
        if (!params.containsKey("size")) params.put("size", "20");
        int page = SafeConverter.toIntOrDefault(params.get("page"), 0);
        int size = SafeConverter.toIntOrDefault(params.get("size"), 20);
        int offset = page * size;

        if (!params.containsKey("offset")) params.put("offset", String.valueOf(offset));
        if (!params.containsKey("limit")) params.put("limit", String.valueOf(size));
        if (!params.containsKey("sortField")) params.put("sortField", "id");
        if (!params.containsKey("sortDirection")) params.put("sortDirection", "ASC");
        params.put("sortDirection", params.get("sortDirection").toUpperCase());

        // Hung : mapper expect Map<String, Object> for offset and limit
    	Map<String, Object> mapperParams = new HashMap<>(params);
        mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
        mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));
        
        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.search(mapperParams);
        long totalCount = items.size();
        PagedResult<UserManagementRoleGroupMappingPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("search return={}", result);
        return result;
    }

    @Override
    public UserManagementRoleGroupMappingPojo get(Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);
        UserManagementRoleGroupMappingPojo mapping = mappingMapper.get(id);
        logger.debug("get return={}", mapping);
        return mapping;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);
        mappingMapper.delete(id);
    }

    // ======= OTHER METHODS =======
    
    // ---------------- CREATE ----------------
    @Override
    public UserManagementRoleGroupMappingPojo insertUserRoleGroup(Map<String, Object> params) {
        logger.debug("insertUserRoleGroup START");
        logger.debug("insertUserRoleGroup params={}", params);

        UserManagementRoleGroupMappingPojo mapping = ParamWrapper.unwrap(params, "mapping");
        if (mapping == null) {
            logger.error("insertUserRoleGroup: mapping is null");
            throw new IllegalArgumentException("Missing mapping payload");
        }

        mappingMapper.insertUserRoleGroup(params);  // pass original params directly
        logger.debug("insertUserRoleGroup mapping inserted id={}", mapping.getId());

        UserManagementRoleGroupMappingPojo fullMapping = mappingMapper.findById(params);
        logger.debug("insertUserRoleGroup DONE fullMapping={}", fullMapping);
        return fullMapping;
    }

    // ---------------- UPDATE ----------------
    @Override
    public UserManagementRoleGroupMappingPojo updateUserRoleGroup(Map<String, Object> params) {
        logger.debug("updateUserRoleGroup START");
        logger.debug("updateUserRoleGroup params={}", params);

        UserManagementRoleGroupMappingPojo mapping = ParamWrapper.unwrap(params, "mapping");
        if (mapping == null || mapping.getId() == null) {
            throw new IllegalArgumentException("Mapping or mapping id is missing");
        }

        mappingMapper.deleteUserRoleGroupById(params);
        logger.debug("updateUserRoleGroup old mapping deleted id={}", mapping.getId());

        mappingMapper.insertUserRoleGroup(params);
        logger.debug("updateUserRoleGroup new mapping inserted={}", mapping);

        logger.debug("updateUserRoleGroup DONE");
        return mapping;
    }

    // ---------------- DELETE ----------------
    @Override
    public void deleteUserRoleGroupById(Map<String, Object> params) {
        logger.debug("deleteUserRoleGroupById START");
        logger.debug("deleteUserRoleGroupById params={}", params);

        mappingMapper.deleteUserRoleGroupById(params);

        logger.debug("deleteUserRoleGroupById DONE");
    }

    @Override
    public void deleteUserRoleGroupByUserAndGroup(Map<String, Object> params) {
        logger.debug("deleteUserRoleGroupByUserAndGroup START");
        logger.debug("deleteUserRoleGroupByUserAndGroup params={}", params);

        mappingMapper.deleteUserRoleGroupByUserAndGroup(params);

        logger.debug("deleteUserRoleGroupByUserAndGroup DONE");
    }

    // ---------------- READ ----------------
    @Override
    public UserManagementRoleGroupMappingPojo findById(Map<String, Object> params) {
        logger.debug("findById START");
        logger.debug("findById params={}", params);

        UserManagementRoleGroupMappingPojo result = mappingMapper.findById(params);

        logger.debug("findById DONE result={}", result);
        return result;
    }

    @Override
    public UserManagementRoleGroupMappingPojo findByUserIdAndRoleGroupId(Map<String, Object> params) {
        logger.debug("findByUserIdAndRoleGroupId START");
        logger.debug("findByUserIdAndRoleGroupId params={}", params);

        UserManagementRoleGroupMappingPojo result = mappingMapper.findByUserIdAndRoleGroupId(params);

        logger.debug("findByUserIdAndRoleGroupId DONE result={}", result);
        return result;
    }

    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> findAllUserRoleGroups() {
        logger.debug("findAllUserRoleGroups START");

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.findAllUserRoleGroups();
        long totalCount = items.size();

        PagedResult<UserManagementRoleGroupMappingPojo> result =
                new PagedResult<>(items, totalCount, 1, (int) totalCount);

        logger.debug("findAllUserRoleGroups DONE result={}", result);
        return result;
    }

    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> findByUserId(Map<String, Object> params) {
        logger.debug("findByUserId START");
        logger.debug("findByUserId params={}", params);

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.findByUserId(params);
        long totalCount = items.size();

        PagedResult<UserManagementRoleGroupMappingPojo> result =
                new PagedResult<>(items, totalCount, 1, (int) totalCount);

        logger.debug("findByUserId DONE result={}", result);
        return result;
    }

    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> findByRoleGroupId(Map<String, Object> params) {
        logger.debug("findByRoleGroupId START");
        logger.debug("findByRoleGroupId params={}", params);

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.findByRoleGroupId(params);
        long totalCount = items.size();

        PagedResult<UserManagementRoleGroupMappingPojo> result =
                new PagedResult<>(items, totalCount, 1, (int) totalCount);

        logger.debug("findByRoleGroupId DONE result={}", result);
        return result;
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> findUserRoleGroups(Map<String, Object> params) {
        logger.debug("findUserRoleGroups START");
        logger.debug("findUserRoleGroups params={}", params);

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.findUserRoleGroups(params);
        long totalCount = mappingMapper.countUserRoleGroups(params);

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);

        PagedResult<UserManagementRoleGroupMappingPojo> result =
                new PagedResult<>(items, totalCount, page, size);

        logger.debug("findUserRoleGroups DONE result={}", result);
        return result;
    }

    @Override
    public PagedResult<UserManagementRoleGroupMappingPojo> searchUserRoleGroups(Map<String, Object> params) {
        logger.debug("searchUserRoleGroups START");
        logger.debug("searchUserRoleGroups params={}", params);

        List<UserManagementRoleGroupMappingPojo> items = mappingMapper.searchUserRoleGroups(params);
        long totalCount = mappingMapper.countUserRoleGroups(params);

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);

        PagedResult<UserManagementRoleGroupMappingPojo> result =
                new PagedResult<>(items, totalCount, page, size);

        logger.debug("searchUserRoleGroups DONE result={}", result);
        return result;
    }

    // ---------------- COUNT ----------------
    @Override
    public long countUserRoleGroups(Map<String, Object> params) {
        logger.debug("countUserRoleGroups START");
        logger.debug("countUserRoleGroups params={}", params);

        long count = mappingMapper.countUserRoleGroups(params);

        logger.debug("countUserRoleGroups DONE count={}", count);
        return count;
    }
}
