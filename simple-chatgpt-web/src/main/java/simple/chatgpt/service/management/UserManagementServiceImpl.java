package simple.chatgpt.service.management;

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

        List<UserManagementPojo> items = userManagementMapper.findUsers(params);
        long totalCount = userManagementMapper.countUsers(params);
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "20"));

        PagedResult<UserManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("searchUsers result={}", result);
        return result;
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
        logger.debug("createUser called with user={}", user);
        userManagementMapper.insertUser(user);
        logger.debug("createUser result={}", user);
        return user;
    }

    // ✏️ UPDATE
    @Override
    public UserManagementPojo updateUserById(Long id, UserManagementPojo user) {
        logger.debug("updateUserById called with id={}, user={}", id, user);
        user.setId(id);
        userManagementMapper.updateUser(user);
        logger.debug("updateUserById result={}", user);
        return user;
    }

    @Override
    public UserManagementPojo updateUserByUserName(String userName, UserManagementPojo user) {
        logger.debug("updateUserByUserName called with userName={}, user={}", userName, user);
        user.setUserName(userName);
        userManagementMapper.updateUserByUserName(user);
        logger.debug("updateUserByUserName result={}", user);
        return user;
    }

    @Override
    public UserManagementPojo updateUserByUserKey(String userKey, UserManagementPojo user) {
        logger.debug("updateUserByUserKey called with userKey={}, user={}", userKey, user);
        user.setUserKey(userKey);
        userManagementMapper.updateUserByUserKey(user);
        logger.debug("updateUserByUserKey result={}", user);
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
