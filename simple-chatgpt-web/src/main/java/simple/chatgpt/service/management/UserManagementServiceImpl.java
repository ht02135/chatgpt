package simple.chatgpt.service.management;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.UserManagementMapper;
import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.util.PagedResult;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    private final UserManagementMapper userManagementMapper;

    public UserManagementServiceImpl(UserManagementMapper userManagementMapper) {
        this.userManagementMapper = userManagementMapper;
    }

    // 🔎 LIST / SEARCH
    @Override
    public PagedResult<UserManagementPojo> searchUsers(Map<String, String> params) {
        List<UserManagementPojo> items = userManagementMapper.findUsers(params);
        long totalCount = userManagementMapper.countUsers(params);
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "20"));
        return new PagedResult<>(items, totalCount, page, size);
    }

    // 📖 READ
    @Override
    public UserManagementPojo getUserById(Long id) {
        return userManagementMapper.findById(id);
    }

    @Override
    public UserManagementPojo getByUserName(String userName) {
        return userManagementMapper.findByUserName(userName);
    }

    @Override
    public UserManagementPojo getByUserKey(String userKey) {
        return userManagementMapper.findByUserKey(userKey);
    }

    // ➕ CREATE
    @Override
    public UserManagementPojo createUser(UserManagementPojo user) {
        userManagementMapper.insertUser(user);
        return user;
    }

    // ✏️ UPDATE
    @Override
    public UserManagementPojo updateUserById(Long id, UserManagementPojo user) {
        user.setId(id);
        userManagementMapper.updateUser(user);
        return user;
    }

    @Override
    public UserManagementPojo updateUserByUserName(String userName, UserManagementPojo user) {
        user.setUserName(userName);
        userManagementMapper.updateUserByUserName(user);
        return user;
    }

    @Override
    public UserManagementPojo updateUserByUserKey(String userKey, UserManagementPojo user) {
        user.setUserKey(userKey);
        userManagementMapper.updateUserByUserKey(user);
        return user;
    }

    // 🗑 DELETE
    @Override
    public void deleteUserById(Long id) {
        userManagementMapper.deleteById(id);
    }

    @Override
    public void deleteUserByUserName(String userName) {
        userManagementMapper.deleteByUserName(userName);
    }

    @Override
    public void deleteUserByUserKey(String userKey) {
        userManagementMapper.deleteByUserKey(userKey);
    }
}
