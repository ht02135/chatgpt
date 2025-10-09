package simple.chatgpt.service.management;

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
        logger.debug("UserManagementServiceImpl START");
        logger.debug("UserManagementServiceImpl userManagementMapper={}", userManagementMapper);
        logger.debug("UserManagementServiceImpl securityConfigLoader={}", securityConfigLoader);
        logger.debug("UserManagementServiceImpl roleGroupService={}", roleGroupService);
        logger.debug("UserManagementServiceImpl mappingService={}", mappingService);

        this.userManagementMapper = userManagementMapper;
        this.securityConfigLoader = securityConfigLoader;
        this.roleGroupService = roleGroupService;
        this.mappingService = mappingService;

        logger.debug("UserManagementServiceImpl DONE");
    }

    @PostConstruct
    public void initializeDB() {
        logger.debug("initializeDB START");

        if (securityConfigLoader == null) {
            logger.error("Missing required beans: securityConfigLoader={}", securityConfigLoader);
            logger.debug("initializeDB DONE");
            return;
        }

        List<UserConfig> users = securityConfigLoader.getUsers();
        logger.debug("initializeDB users size={}", users.size());

        Map<String, RoleGroupManagementPojo> roleGroupByName = roleGroupService
                .getAllRoleGroups()
                .getItems()
                .stream()
                .collect(Collectors.toMap(RoleGroupManagementPojo::getGroupName, rg -> rg));
        logger.debug("initializeDB roleGroupByName size={}", roleGroupByName.size());

        for (UserConfig u : users) {
            logger.debug("initializeDB processing user userName={}", u.getUserName());

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

                logger.debug("initializeDB before insertUser user={}", user);
                userManagementMapper.insertUser(user);
                existing = user;
                logger.debug("initializeDB after insertUser user={}", user);
            }

            String roleGroupName = u.getRoleGroup();
            if (roleGroupName != null && !roleGroupName.isEmpty()) {
                RoleGroupManagementPojo group = roleGroupByName.get(roleGroupName);
                if (group != null) {
                    Map<String, Object> mappingParams = ParamWrapper.wrap("userId", existing.getId(), "roleGroupId", group.getId());
                    UserManagementRoleGroupMappingPojo existingMapping =
                            mappingService.findByUserIdAndRoleGroupId(mappingParams);

                    if (existingMapping == null) {
                        UserManagementRoleGroupMappingPojo mapping = new UserManagementRoleGroupMappingPojo();
                        mapping.setUserId(existing.getId());
                        mapping.setRoleGroupId(group.getId());

                        logger.debug("initializeDB before insertUserRoleGroup mapping={}", mapping);
                        mappingService.insertUserRoleGroup(ParamWrapper.wrap("mapping", mapping));
                        logger.debug("Mapped user userName={} to roleGroup={} mappingId={}",
                                u.getUserName(), roleGroupName, mapping.getId());
                    }
                }
            }
        }

        logger.debug("initializeDB DONE");
    }

    // 🔎 LIST / SEARCH
    @Override
    public PagedResult<UserManagementPojo> searchUsers(Map<String, Object> params) {
        logger.debug("searchUsers START");
        logger.debug("searchUsers params={}", params);

        // Pass params directly to mapper; controller owns defaults & sort
        List<UserManagementPojo> items = userManagementMapper.findUsers(params);
        long totalCount = userManagementMapper.countUsers(params);
        PagedResult<UserManagementPojo> result = new PagedResult<>(
        	items, totalCount, 
        	SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0),
        	SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20));

        logger.debug("searchUsers return={}", result);
        return result;
    }

    // 📖 READ
    @Override
    public UserManagementPojo getUserById(Long id) {
        logger.debug("getUserById START");
        logger.debug("getUserById id={}", id);

        UserManagementPojo user = userManagementMapper.findById(id);

        logger.debug("getUserById return={}", user);
        return user;
    }

    @Override
    public UserManagementPojo getByUserName(String userName) {
        logger.debug("getByUserName START");
        logger.debug("getByUserName userName={}", userName);

        UserManagementPojo user = userManagementMapper.findByUserName(userName);

        logger.debug("getByUserName return={}", user);
        return user;
    }

    @Override
    public UserManagementPojo getByUserKey(String userKey) {
        logger.debug("getByUserKey START");
        logger.debug("getByUserKey userKey={}", userKey);

        UserManagementPojo user = userManagementMapper.findByUserKey(userKey);

        logger.debug("getByUserKey return={}", user);
        return user;
    }

    // ➕ CREATE
    @Override
    public UserManagementPojo createUser(UserManagementPojo user) {
        logger.debug("createUser START");
        logger.debug("createUser user={}", user);

        userManagementMapper.insertUser(user);

        logger.debug("createUser return={}", user);
        return user;
    }

    // ✏️ UPDATE
    @Override
    public UserManagementPojo updateUserById(Long id, UserManagementPojo user) {
        logger.debug("updateUserById START");
        logger.debug("updateUserById id={}", id);
        logger.debug("updateUserById user={}", user);

        user.setId(id);
        userManagementMapper.updateUser(user);

        logger.debug("updateUserById return={}", user);
        return user;
    }

    @Override
    public UserManagementPojo updateUserByUserName(String userName, UserManagementPojo user) {
        logger.debug("updateUserByUserName START");
        logger.debug("updateUserByUserName userName={}", userName);
        logger.debug("updateUserByUserName user={}", user);

        user.setUserName(userName);
        userManagementMapper.updateUserByUserName(user);

        logger.debug("updateUserByUserName return={}", user);
        return user;
    }

    @Override
    public UserManagementPojo updateUserByUserKey(String userKey, UserManagementPojo user) {
        logger.debug("updateUserByUserKey START");
        logger.debug("updateUserByUserKey userKey={}", userKey);
        logger.debug("updateUserByUserKey user={}", user);

        user.setUserKey(userKey);
        userManagementMapper.updateUserByUserKey(user);

        logger.debug("updateUserByUserKey return={}", user);
        return user;
    }

    // 🗑 DELETE
    @Override
    public void deleteUserById(Long id) {
        logger.debug("deleteUserById START");
        logger.debug("deleteUserById id={}", id);

        userManagementMapper.deleteById(id);

        logger.debug("deleteUserById DONE");
    }

    @Override
    public void deleteUserByUserName(String userName) {
        logger.debug("deleteUserByUserName START");
        logger.debug("deleteUserByUserName userName={}", userName);

        userManagementMapper.deleteByUserName(userName);

        logger.debug("deleteUserByUserName DONE");
    }

    @Override
    public void deleteUserByUserKey(String userKey) {
        logger.debug("deleteUserByUserKey START");
        logger.debug("deleteUserByUserKey userKey={}", userKey);

        userManagementMapper.deleteByUserKey(userKey);

        logger.debug("deleteUserByUserKey DONE");
    }
}
