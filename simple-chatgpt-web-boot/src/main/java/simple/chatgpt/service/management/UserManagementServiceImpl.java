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
                userManagementMapper.create(user);
                existing = user;
                logger.debug("initializeDB after insertUser existing={}", existing);
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
    
    // =========================================================================
    // 5 CORE METHODS (PRIMARY)
    // =========================================================================

    @Override
    public UserManagementPojo create(UserManagementPojo user) {
        logger.debug("create START");
        logger.debug("create user={}", user);
        userManagementMapper.create(user);
        logger.debug("create return={}", user);
        return user;
    }

    @Override
    public UserManagementPojo update(Long id, UserManagementPojo user) {
        logger.debug("update START");
        logger.debug("update id={}", id);
        logger.debug("update user={}", user);
        userManagementMapper.update(id, user);
        logger.debug("update return={}", user);
        return user;
    }

    @Override
    public PagedResult<UserManagementPojo> search(Map<String, String> params) {
        logger.debug("search START");
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

        // force uppercase for sortDirection
        params.put("sortDirection", params.get("sortDirection").toUpperCase());

        // Hung : mapper expect Map<String, Object> for offset and limit
    	Map<String, Object> mapperParams = new HashMap<>(params);
        mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
        mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));
        
        List<UserManagementPojo> items = userManagementMapper.search(mapperParams);
        long totalCount = items.size(); // replace with count query if available

        PagedResult<UserManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("search return={}", result);
        return result;
    }

    @Override
    public UserManagementPojo get(Long id) {
        logger.debug("get START");
        logger.debug("get id={}", id);
        UserManagementPojo user = userManagementMapper.get(id);
        logger.debug("get return={}", user);
        return user;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete START");
        logger.debug("delete id={}", id);
        userManagementMapper.delete(id);
        logger.debug("delete DONE");
    }
    
    // =========================================================================
    // ORIGINAL METHODS (USED BY CORE)
    // =========================================================================

    public UserManagementPojo getByUserName(String userName) {
        logger.debug("getByUserName START");
        logger.debug("getByUserName userName={}", userName);

        UserManagementPojo user = userManagementMapper.findByUserName(userName);

        logger.debug("getByUserName return={}", user);
        return user;
    }
}
