package simple.chatgpt.service.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import simple.chatgpt.config.management.loader.SecurityConfigLoader;
import simple.chatgpt.mapper.management.UserManagementMapper;
import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.service.management.security.RoleGroupManagementService;
import simple.chatgpt.service.management.security.UserManagementRoleGroupMappingService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.SafeConverter;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    private static final Logger logger = LogManager.getLogger(UserManagementServiceImpl.class);

    private final UserManagementMapper userManagementMapper;
    private final SecurityConfigLoader securityConfigLoader;
    private final PasswordEncoder passwordEncoder;
    private final UserManagementRoleGroupMappingService mappingService;
    private final RoleGroupManagementService roleGroupService;

    @Autowired
    public UserManagementServiceImpl(UserManagementMapper userManagementMapper,
                                     SecurityConfigLoader securityConfigLoader,
                                     PasswordEncoder passwordEncoder,
                                     UserManagementRoleGroupMappingService mappingService,
                                     RoleGroupManagementService roleGroupService) {
        logger.debug("UserManagementServiceImpl START");
        logger.debug("UserManagementServiceImpl userManagementMapper={}", userManagementMapper);
        logger.debug("UserManagementServiceImpl securityConfigLoader={}", securityConfigLoader);
        logger.debug("UserManagementServiceImpl passwordEncoder={}", passwordEncoder);
        logger.debug("UserManagementServiceImpl mappingService={}", mappingService);
        logger.debug("UserManagementServiceImpl roleGroupService={}", roleGroupService);

        this.userManagementMapper = userManagementMapper;
        this.securityConfigLoader = securityConfigLoader;
        this.passwordEncoder = passwordEncoder;
        this.mappingService = mappingService;
        this.roleGroupService = roleGroupService;

        logger.debug("UserManagementServiceImpl DONE");
    }

    @PostConstruct
    public void initializeDB() {
        logger.debug("initializeDB called");
    }

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public UserManagementPojo create(UserManagementPojo user) {
        logger.debug("create START");
        logger.debug("create user={}", user);

        if (user.getPassword() != null) {
            String encoded = passwordEncoder.encode(user.getPassword());
            user.setPassword(encoded);
            logger.debug("create encoded password={}", encoded);
        }

        userManagementMapper.create(user);

        // Sync role groups if provided
        if (user.getRoleGroups() != null && !user.getRoleGroups().isEmpty()) {
            syncUserRoleGroups(user.getId(), user.getRoleGroups());
        }

        // Populate role groups
        user.setRoleGroups(getUserRoleGroups(user.getId()));
        logger.debug("create return={}", user);
        return user;
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public UserManagementPojo update(Long id, UserManagementPojo user) {
        logger.debug("update START");
        logger.debug("update id={}", id);
        logger.debug("update user={}", user);

        UserManagementPojo existingUser = userManagementMapper.get(id);
        logger.debug("update existingUser={}", existingUser);

        if (!user.getPassword().equals(existingUser.getPassword())) {
            String encoded = passwordEncoder.encode(user.getPassword());
            user.setPassword(encoded);
            logger.debug("update password changed, encoded password={}", encoded);
        } else {
            user.setPassword(existingUser.getPassword());
            logger.debug("update password unchanged, keeping existing");
        }

        userManagementMapper.update(id, user);

        // Sync role groups if provided
        if (user.getRoleGroups() != null) {
            syncUserRoleGroups(id, user.getRoleGroups());
        }

        // Populate role groups
        user.setRoleGroups(getUserRoleGroups(id));
        logger.debug("update return={}", user);
        return user;
    }

    // -----------------------------
    // GET
    // -----------------------------
    @Override
    public UserManagementPojo get(Long id) {
        logger.debug("get START");
        logger.debug("get id={}", id);

        UserManagementPojo user = userManagementMapper.get(id);
        if (user != null) {
            user.setRoleGroups(getUserRoleGroups(id));
        }

        logger.debug("get return={}", user);
        return user;
    }

    // -----------------------------
    // SEARCH
    // -----------------------------
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

        Map<String, Object> mapperParams = new HashMap<>(params);
        mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
        mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));

        List<UserManagementPojo> items = userManagementMapper.search(mapperParams);

        // Populate role groups for all users
        for (UserManagementPojo user : items) {
            user.setRoleGroups(getUserRoleGroups(user.getId()));
        }

        long totalCount = items.size(); // replace with count query if available
        PagedResult<UserManagementPojo> result = new PagedResult<>(items, totalCount, page, size);

        logger.debug("search return={}", result);
        return result;
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @Override
    public void delete(Long id) {
        logger.debug("delete START");
        logger.debug("delete id={}", id);

        // Optionally remove mappings first
        List<UserManagementRoleGroupMappingPojo> mappings = mappingService.getMappingsByUserId(id);
        for (UserManagementRoleGroupMappingPojo m : mappings) {
            mappingService.delete(m.getId());
            logger.debug("delete removed mapping={}", m);
        }

        userManagementMapper.delete(id);
        logger.debug("delete DONE");
    }

    // -----------------------------
    // OTHER HELPER METHODS
    // -----------------------------
    public List<UserManagementPojo> getUserByParams(Map<String, Object> params) {
        logger.debug("getUserByParams called");
        List<UserManagementPojo> mappings = userManagementMapper.search(params);
        // populate roleGroups
        for (UserManagementPojo u : mappings) {
            u.setRoleGroups(getUserRoleGroups(u.getId()));
        }
        return mappings;
    }

    public List<UserManagementPojo> getAll() {
        logger.debug("getAll called");
        Map<String, Object> params = new HashMap<>();
        List<UserManagementPojo> users = getUserByParams(params);
        return users;
    }

    @Override
    public UserManagementPojo getUserByUserName(String userName) {
        logger.debug("getUserByUserName called userName={}", userName);

        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);

        List<UserManagementPojo> users = getUserByParams(params);
        if (users == null || users.isEmpty()) {
            logger.debug("getUserByUserName: no user found for userName={}", userName);
            return null;
        }

        UserManagementPojo foundUser = users.get(0);
        logger.debug("getUserByUserName foundUser={}", foundUser);
        return foundUser;
    }
    
    // -----------------------------
    // HELPER: Get User Role Groups
    // -----------------------------
    private List<RoleGroupManagementPojo> getUserRoleGroups(Long userId) {
        logger.debug("getUserRoleGroups called userId={}", userId);

        List<UserManagementRoleGroupMappingPojo> mappings = mappingService.getMappingsByUserId(userId);
        List<Long> roleGroupIds = mappings.stream().map(UserManagementRoleGroupMappingPojo::getRoleGroupId).toList();

        List<RoleGroupManagementPojo> roleGroups = roleGroupService.getAll().stream()
                .filter(r -> roleGroupIds.contains(r.getId()))
                .collect(Collectors.toList());

        logger.debug("getUserRoleGroups return={}", roleGroups);
        return roleGroups;
    }

    // -----------------------------
    // HELPER: Sync Role Groups
    // -----------------------------
    private void syncUserRoleGroups(Long userId, List<RoleGroupManagementPojo> newRoleGroups) {
        logger.debug("syncUserRoleGroups called userId={} newRoleGroups={}", userId, newRoleGroups);

        List<UserManagementRoleGroupMappingPojo> existingMappings = mappingService.getMappingsByUserId(userId);

        // Determine which mappings to remove
        List<UserManagementRoleGroupMappingPojo> toRemove = existingMappings.stream()
                .filter(m -> newRoleGroups.stream().noneMatch(r -> r.getId().equals(m.getRoleGroupId())))
                .toList();

        // Determine which mappings to add
        List<RoleGroupManagementPojo> toAdd = newRoleGroups.stream()
                .filter(r -> existingMappings.stream().noneMatch(m -> m.getRoleGroupId().equals(r.getId())))
                .toList();

        // Remove outdated mappings
        for (UserManagementRoleGroupMappingPojo remove : toRemove) {
            mappingService.delete(remove.getId());
            logger.debug("syncUserRoleGroups deleted mapping={}", remove);
        }

        // Add new mappings
        for (RoleGroupManagementPojo add : toAdd) {
            UserManagementRoleGroupMappingPojo mapping = new UserManagementRoleGroupMappingPojo();
            mapping.setUserId(userId);
            mapping.setRoleGroupId(add.getId());
            mappingService.create(mapping);
            logger.debug("syncUserRoleGroups added mapping={}", mapping);
        }
    }
}
