package simple.chatgpt.service.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.config.management.loader.SecurityConfigLoader;
import simple.chatgpt.config.management.security.UserConfig;
import simple.chatgpt.mapper.management.UserManagementMapper;
import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.service.management.security.RoleGroupManagementService;
import simple.chatgpt.service.management.security.UserManagementRoleGroupMappingService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.SafeConverter;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    private static final Logger logger = LogManager.getLogger(UserManagementServiceImpl.class);

    private final UserManagementMapper userManagementMapper;
    private final SecurityConfigLoader securityConfigLoader;
    private final RoleGroupManagementService roleGroupService;
    private final UserManagementRoleGroupMappingService mappingService;

    @Autowired
    public UserManagementServiceImpl(UserManagementMapper userManagementMapper,
                                     SecurityConfigLoader securityConfigLoader,
                                     RoleGroupManagementService roleGroupService,
                                     UserManagementRoleGroupMappingService mappingService) {
        logger.debug("UserManagementServiceImpl constructor called");
        logger.debug("userManagementMapper={}", userManagementMapper);
        logger.debug("securityConfigLoader={}", securityConfigLoader);
        logger.debug("roleGroupService={}", roleGroupService);
        logger.debug("mappingService={}", mappingService);

        this.userManagementMapper = userManagementMapper;
        this.securityConfigLoader = securityConfigLoader;
        this.roleGroupService = roleGroupService;
        this.mappingService = mappingService;
    }

    @PostConstruct
    public void initializeDB() {
        logger.debug("initializeDB called");

        if (securityConfigLoader == null) {
            logger.error("Missing required beans: securityConfigLoader={}", securityConfigLoader);
            return;
        }

        List<UserConfig> users = securityConfigLoader.getUsers();
        logger.debug("initializeDB users size={}", users.size());

        // ----------- FETCH ALL ROLE-GROUPS ONCE -----------
        Map<String, RoleGroupManagementPojo> roleGroupByName = roleGroupService
                .getAllRoleGroups()
                .getItems()
                .stream()
                .collect(Collectors.toMap(RoleGroupManagementPojo::getGroupName, rg -> rg));
        logger.debug("initializeDB fetched role groups size={}", roleGroupByName.size());
        logger.debug("initializeDB fetched roleGroupByName={}", roleGroupByName);

        for (UserConfig u : users) {
            logger.debug("initializeDB processing user userName={}", u.getUserName());

            // ----------- CREATE OR FETCH USER -----------
            UserManagementPojo existing = userManagementMapper.findByUserName(u.getUserName());
            if (existing == null) {
                UserManagementPojo user = new UserManagementPojo();
                user.setUserName(u.getUserName());
                user.setUserKey(u.getUserKey());
                user.setPassword(u.getPassword());
                user.setFirstName(u.getFirstName());
                user.setLastName(u.getLastName());
                user.setEmail(u.getEmail());
                user.setAddressLine1(u.getAddressLine1());
                user.setAddressLine2(u.getAddressLine2());
                user.setCity(u.getCity());
                user.setState(u.getState());
                user.setPostCode(u.getPostCode());
                user.setCountry(u.getCountry());
                user.setActive(u.isActive());
                user.setLocked(u.isLocked());

                logger.debug("initializeDB ##############");
                logger.debug("initializeDB before insertUser user={}", user);
                userManagementMapper.insertUser(user);
                logger.debug("initializeDB after insertUser user={}", user);
                logger.debug("initializeDB ##############");

                existing = user;
                logger.debug("Inserted default user userName={}", user.getUserName());
            } else {
                logger.debug("User already exists, skipping creation userName={}", u.getUserName());
            }

            // ----------- MAP USER TO ROLE-GROUP -----------
            String roleGroupName = u.getRoleGroup();
            if (roleGroupName != null && !roleGroupName.isEmpty()) {
                RoleGroupManagementPojo group = roleGroupByName.get(roleGroupName);
                if (group != null) {

                    // Wrap params to call service method
                    Map<String, Object> mappingParams = ParamWrapper.wrap("userId", existing.getId(), "roleGroupId", group.getId());
                    UserManagementRoleGroupMappingPojo existingMapping =
                            mappingService.findByUserIdAndRoleGroupId(mappingParams);

                    if (existingMapping == null) {
                        UserManagementRoleGroupMappingPojo mapping = new UserManagementRoleGroupMappingPojo();
                        mapping.setUserId(existing.getId());
                        mapping.setRoleGroupId(group.getId());

                        logger.debug("initializeDB ##############");
                        logger.debug("initializeDB before insertUserRoleGroup mapping={}", mapping);
                        logger.debug("initializeDB ##############");
                        mappingService.insertUserRoleGroup(ParamWrapper.wrap("mapping", mapping));
                        logger.debug("Mapped user userName={} to roleGroup={} mappingId={}",
                                u.getUserName(), roleGroupName, mapping.getId());
                    } else {
                        logger.debug("Mapping already exists for user {} and roleGroup {}, skipping insert",
                                u.getUserName(), roleGroupName);
                    }

                } else {
                    logger.warn("Role-group '{}' not found, skipping mapping for user '{}'", roleGroupName, u.getUserName());
                }
            }
        }

        logger.debug("initializeDB completed");
    }


    // 🔎 LIST / SEARCH
    @Override
    public PagedResult<UserManagementPojo> searchUsers(Map<String, String> params) {
        logger.debug("searchUsers called with params={}", params);

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0); 
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        // Copy params into a Map<String, Object> for MyBatis
        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.putAll(params);  // copy filters like firstName, city, etc.
        sqlParams.put("offset", offset);
        sqlParams.put("limit", size);

        String sortField = ParamWrapper.unwrap(params, "sortField", "id");
        String sortDirection = ParamWrapper.unwrap(params, "sortDirection", "ASC").toUpperCase();
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
