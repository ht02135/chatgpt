package simple.chatgpt.service.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.management.UserManagementMapper;
import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.util.PagedResult;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    private static final Logger logger = LogManager.getLogger(UserManagementServiceImpl.class);

    private final UserManagementMapper userManagementMapper;

    public UserManagementServiceImpl(UserManagementMapper userManagementMapper) {
        this.userManagementMapper = userManagementMapper;
    }

    // 🔎 LIST / SEARCH
    @Override
    public PagedResult<UserManagementPojo> searchUsers(Map<String, String> params) {
        logger.debug("searchUsers called with params={}", params);

        // Convert page and size to integers
        int page = 0;
        int size = 20;
        try {
            page = Integer.parseInt(params.getOrDefault("page", "0"));
        } catch (NumberFormatException e) {
            logger.warn("Invalid page parameter: {}, defaulting to 0", params.get("page"), e);
        }
        try {
            size = Integer.parseInt(params.getOrDefault("size", "20"));
        } catch (NumberFormatException e) {
            logger.warn("Invalid size parameter: {}, defaulting to 20", params.get("size"), e);
        }
        int offset = page * size;

        // Copy params into a Map<String, Object> for MyBatis
        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.putAll(params);  // copy filters like firstName, city, etc.
        sqlParams.put("offset", offset);
        sqlParams.put("limit", size);

        // Resolve sortField
        // String sortField = resolveSortField(params.get("sortField"));
        String sortField = params.get("sortField");
        String sortDirection = params.getOrDefault("sortDirection", "ASC").toUpperCase();
        sqlParams.put("sortField", sortField);
        sqlParams.put("sortDirection", sortDirection);

        logger.debug("searchUsers sqlParams={}", sqlParams);

        List<UserManagementPojo> items = null;
        long totalCount = 0;

        try {
            items = userManagementMapper.findUsers(sqlParams);
            totalCount = userManagementMapper.countUsers(sqlParams);
            logger.debug("searchUsers items={}", items);
            logger.debug("searchUsers totalCount={}", totalCount);
        } catch (Exception e) {
            logger.error("Error executing searchUsers query with params={}", sqlParams, e);
            throw new RuntimeException("Database error during searchUsers", e);
        }

        PagedResult<UserManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("searchUsers result={}", result);

        return result;
    }

    // ---------------- Helper Method ----------------
    private String resolveSortField(String frontEndField) {
        Map<String, String> sortFieldMap = Map.of(
            "id", "id",
            "userName", "user_name",
            "userKey", "user_key",
            "firstName", "first_name",
            "lastName", "last_name",
            "email", "email",
            "city", "city",
            "country", "country",
            "createdAt", "created_at",
            "updatedAt", "updated_at"
        );

        String dbColumn = sortFieldMap.get(frontEndField);
        if (dbColumn == null) {
            logger.debug("Invalid sortField '{}', defaulting to 'id'", frontEndField);
            dbColumn = "id";
        } else {
            logger.debug("Resolved sortField '{}' -> '{}'", frontEndField, dbColumn);
        }
        return dbColumn;
    }

    // 📖 READ
    @Override
    public UserManagementPojo getUserById(Long id) {
        logger.debug("getUserById called with id={}", id);
        UserManagementPojo user = userManagementMapper.findById(id);
        logger.debug("getUserById result={}", user);
        return user;
    }

    @Override
    public UserManagementPojo getByUserName(String userName) {
        logger.debug("getByUserName called with userName={}", userName);
        UserManagementPojo user = userManagementMapper.findByUserName(userName);
        logger.debug("getByUserName result={}", user);
        return user;
    }

    @Override
    public UserManagementPojo getByUserKey(String userKey) {
        logger.debug("getByUserKey called with userKey={}", userKey);
        UserManagementPojo user = userManagementMapper.findByUserKey(userKey);
        logger.debug("getByUserKey result={}", user);
        return user;
    }

    // ➕ CREATE
    @Override
    public UserManagementPojo createUser(UserManagementPojo user) {
        logger.debug("#############");
        logger.debug("createUser called with user={}", user);
        userManagementMapper.insertUser(user);
        logger.debug("createUser result={}", user);
        logger.debug("#############");
        return user;
    }

    // ✏️ UPDATE
    @Override
    public UserManagementPojo updateUserById(Long id, UserManagementPojo user) {
        logger.debug("#############");
        logger.debug("updateUserById called with id={}, user={}", id, user);
        user.setId(id);
        userManagementMapper.updateUser(user);
        logger.debug("updateUserById result={}", user);
        logger.debug("#############");
        return user;
    }

    @Override
    public UserManagementPojo updateUserByUserName(String userName, UserManagementPojo user) {
        logger.debug("#############");
        logger.debug("updateUserByUserName called with userName={}, user={}", userName, user);
        user.setUserName(userName);
        userManagementMapper.updateUserByUserName(user);
        logger.debug("updateUserByUserName result={}", user);
        logger.debug("#############");
        return user;
    }

    @Override
    public UserManagementPojo updateUserByUserKey(String userKey, UserManagementPojo user) {
        logger.debug("#############");
        logger.debug("updateUserByUserKey called with userKey={}, user={}", userKey, user);
        user.setUserKey(userKey);
        userManagementMapper.updateUserByUserKey(user);
        logger.debug("updateUserByUserKey result={}", user);
        logger.debug("#############");
        return user;
    }

    // 🗑 DELETE
    @Override
    public void deleteUserById(Long id) {
        logger.debug("deleteUserById called with id={}", id);
        userManagementMapper.deleteById(id);
        logger.debug("deleteUserById completed for id={}", id);
    }

    @Override
    public void deleteUserByUserName(String userName) {
        logger.debug("deleteUserByUserName called with userName={}", userName);
        userManagementMapper.deleteByUserName(userName);
        logger.debug("deleteUserByUserName completed for userName={}", userName);
    }

    @Override
    public void deleteUserByUserKey(String userKey) {
        logger.debug("deleteUserByUserKey called with userKey={}", userKey);
        userManagementMapper.deleteByUserKey(userKey);
        logger.debug("deleteUserByUserKey completed for userKey={}", userKey);
    }
}
