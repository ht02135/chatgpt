package simple.chatgpt.service.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import simple.chatgpt.service.management.security.RoleGroupManagementService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.SafeConverter;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    private static final Logger logger = LogManager.getLogger(UserManagementServiceImpl.class);

    private final UserManagementMapper userManagementMapper;
    private final SecurityConfigLoader securityConfigLoader;
    private final PasswordEncoder passwordEncoder;
    private final RoleGroupManagementService roleGroupService; // <-- injected service

    @Autowired
    public UserManagementServiceImpl(UserManagementMapper userManagementMapper,
                                     SecurityConfigLoader securityConfigLoader,
                                     PasswordEncoder passwordEncoder,
                                     RoleGroupManagementService roleGroupService) {
        logger.debug("UserManagementServiceImpl START");
        logger.debug("UserManagementServiceImpl userManagementMapper={}", userManagementMapper);
        logger.debug("UserManagementServiceImpl securityConfigLoader={}", securityConfigLoader);
        logger.debug("UserManagementServiceImpl passwordEncoder={}", passwordEncoder);
        logger.debug("UserManagementServiceImpl roleGroupService={}", roleGroupService);

        this.userManagementMapper = userManagementMapper;
        this.securityConfigLoader = securityConfigLoader;
        this.passwordEncoder = passwordEncoder;
        this.roleGroupService = roleGroupService;

        logger.debug("UserManagementServiceImpl DONE");
    }

    @PostConstruct
    public void initializeDB() {
    }
    
    // =========================================================================
    // 5 CORE METHODS (PRIMARY)
    // =========================================================================

    @Override
    public UserManagementPojo create(UserManagementPojo user) {
        logger.debug("create START");
        logger.debug("create user={}", user);

        /*
        hung : dont remove it
        ///////////////////////
        Encode password before saving
        BCryptPasswordEncoder, then this is hashing, not encryption.
        | Concept        | One-way? | Can you get the original back? | Example Use                        |
		| -------------- | -------- | ------------------------------ | ---------------------------------- |
		| **Encryption** | No       | Yes (if you have the key)      | Encrypting messages, tokens, files |
		| **Hashing**    | Yes      | No (irreversible)              | Storing passwords securely         |
        */
        if (user.getPassword() != null) {
            String encoded = passwordEncoder.encode(user.getPassword());
            user.setPassword(encoded);
            logger.debug("create encoded password={}", encoded);
        }

        userManagementMapper.create(user);
        logger.debug("create return={}", user);
        return user;
    }

    @Override
    public UserManagementPojo update(Long id, UserManagementPojo user) {
        logger.debug("update START");
        logger.debug("update id={}", id);
        logger.debug("update user={}", user);

        // Fetch current user from DB
        UserManagementPojo existingUser = userManagementMapper.get(id);
        logger.debug("update existingUser={}", existingUser);

        /*
         hung: compare password using passwordEncoder.matches()
         because existingUser.getPassword() is encoded, but user.getPassword() is plain
         /////////////
         boolean matches(CharSequence rawPassword, String encodedPassword)
         Check whether the raw (plain) password, when encoded, matches the stored 
         encoded password.
         */
        boolean passwordSame = passwordEncoder.matches(user.getPassword(), existingUser.getPassword());
        logger.debug("update passwordSame={}", passwordSame);

        if (!passwordSame) {
            // Password changed → encode new one
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            logger.debug("update password changed, encoded password={}", encodedPassword);
        } else {
            // Password unchanged → keep existing
            user.setPassword(existingUser.getPassword());
            logger.debug("update password unchanged, keeping existing");
        }

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

    @Override
	public List<UserManagementPojo> getUserByParams(Map<String, Object> params)
	{
        logger.debug("getUserByParams called");

        List<UserManagementPojo> mappings = userManagementMapper.search(params);
        return mappings;
	}
	
    @Override
	public List<UserManagementPojo> getAll()
	{
        logger.debug("getAll called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        // No offset/limit => all rows
        List<UserManagementPojo> users = getUserByParams(params);
        
        return users;
	}
	
	// #{params.userName}
	@Override
	public UserManagementPojo getUserByUserName(String userName) {
	    logger.debug("getUserByUserName called");
	    logger.debug("getUserByUserName userName={}", userName);

	    Map<String, Object> params = new HashMap<>();
	    params.put("userName", userName);

	    List<UserManagementPojo> users = getUserByParams(params);
	    logger.debug("getUserByUserName users={}", users);

	    if (users == null || users.isEmpty()) {
	        logger.debug("getUserByUserName: no user found for userName={}", userName);
	        return null;
	    }

	    UserManagementPojo foundUser = users.get(0);
	    logger.debug("getUserByUserName foundUser={}", foundUser);

	    return foundUser;
	}
	
	@Override
	public List<String> getRoleGroupNamesByUserName(String userName) {
	    logger.debug("getRoleGroupNamesByUserName called");
	    logger.debug("getRoleGroupNamesByUserName userName={}", userName);

	    UserManagementPojo user = getUserByUserName(userName);
	    logger.debug("getRoleGroupNamesByUserName user={}", user);
	    if (user == null) {
	        return List.of(); // empty list if user not found
	    }

	    String delimitRoleGroupNames = user.getDelimitRoleGroups();
	    logger.debug("getRoleGroupNamesByUserName delimitRoleGroupNames={}", delimitRoleGroupNames);
	    if (delimitRoleGroupNames == null || delimitRoleGroupNames.isBlank()) {
	        return List.of(); // empty list if no role groups
	    }

	    // Split by "|" and filter out empty strings
	    String[] tokens = delimitRoleGroupNames.split("\\|");
	    List<String> roleGroupNames = new ArrayList<>();
	    for (String token : tokens) {
	        if (!token.isBlank()) {
	            roleGroupNames.add(token.trim());
	        }
	    }

	    logger.debug("getRoleGroupNamesByUserName roleGroupNames={}", roleGroupNames);
	    return roleGroupNames;
	}

	@Override
	public List<String> getRoleNamesByUserName(String userName) {
	    logger.debug("getRoleNamesByUserName called");
	    logger.debug("getRoleNamesByUserName userName={}", userName);

	    List<String> roleGroupNames = getRoleGroupNamesByUserName(userName);
	    logger.debug("getRoleNamesByUserName roleGroupNames={}", roleGroupNames);
	    if (roleGroupNames.isEmpty()) {
	        return List.of(); // empty list if no role groups
	    }

	    List<String> roleNames = new ArrayList<>();
	    java.util.Set<String> roleNameSet = new java.util.LinkedHashSet<>();

	    for (String groupName : roleGroupNames) {
	        logger.debug("getRoleNamesByUserName groupName={}", groupName);

	        RoleGroupManagementPojo roleGroup = roleGroupService.getRoleGroupByGroupName(groupName);
	        logger.debug("getRoleNamesByUserName roleGroup={}", roleGroup);
	        if (roleGroup == null) continue;

	        String delimitRoles = roleGroup.getDelimitRoles();
	        logger.debug("getRoleNamesByUserName delimitRoles={}", delimitRoles);
	        if (delimitRoles == null || delimitRoles.isBlank()) continue;

	        String[] tokens = delimitRoles.split("\\|");
	        for (String token : tokens) {
	            if (!token.isBlank()) {
	                roleNameSet.add(token.trim());
	            }
	        }
	    }

	    roleNames = new ArrayList<>(roleNameSet);
	    logger.debug("getRoleNamesByUserName roles={}", roleNames);
	    return roleNames;
	}
	
}
