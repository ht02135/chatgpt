package simple.chatgpt.service.management.security;

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
import simple.chatgpt.mapper.management.security.UserManagementRoleGroupMappingMapper;
import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.service.management.PropertyManagementService;
import simple.chatgpt.service.management.UserManagementService;
import simple.chatgpt.util.EncryptionUtil;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.PropertyKey;
import simple.chatgpt.util.SafeConverter;

@Service
public class UserManagementRoleGroupMappingServiceImpl implements UserManagementRoleGroupMappingService {

    private static final Logger logger = LogManager.getLogger(UserManagementRoleGroupMappingServiceImpl.class);

    private final UserManagementRoleGroupMappingMapper mappingMapper;
    private final UserManagementService userService;                
    private final SecurityConfigLoader securityConfigLoader;       
    private final RoleGroupManagementService roleGroupService;     
    private final PropertyManagementService propertyService; // <-- new service

    @Autowired
    public UserManagementRoleGroupMappingServiceImpl(
            UserManagementRoleGroupMappingMapper mappingMapper,
            UserManagementService userService,
            SecurityConfigLoader securityConfigLoader,
            RoleGroupManagementService roleGroupService,
            PropertyManagementService propertyService) {  // <-- inject here

        logger.debug("UserManagementRoleGroupMappingServiceImpl constructor START");
        logger.debug("UserManagementRoleGroupMappingServiceImpl mappingMapper={}", mappingMapper);
        logger.debug("UserManagementRoleGroupMappingServiceImpl userService={}", userService);
        logger.debug("UserManagementRoleGroupMappingServiceImpl securityConfigLoader={}", securityConfigLoader);
        logger.debug("UserManagementRoleGroupMappingServiceImpl roleGroupService={}", roleGroupService);
        logger.debug("UserManagementRoleGroupMappingServiceImpl propertyService={}", propertyService); // log

        this.mappingMapper = mappingMapper;
        this.userService = userService;
        this.securityConfigLoader = securityConfigLoader;
        this.roleGroupService = roleGroupService;
        this.propertyService = propertyService; // assign

        logger.debug("UserManagementRoleGroupMappingServiceImpl constructor DONE");
    }

	@PostConstruct
	public void initializeDB() {
	    logger.debug("initializeDB called");

	    try {
	        // ======================================================
	        // STEP 1: Load users from XML config
	        // ======================================================
	        List<UserConfig> xmlUsers = securityConfigLoader.getUsers();
	        logger.debug("initializeDB xmlUsers={}", xmlUsers);

	        if (xmlUsers == null || xmlUsers.isEmpty()) {
	            logger.debug("initializeDB no users found in XML, skipping");
	            return;
	        }

	        // ======================================================
	        // STEP 2: Load all role groups for mapping
	        // ======================================================
	        List<RoleGroupManagementPojo> allRoleGroups = roleGroupService.getAll();
	        logger.debug("initializeDB allRoleGroups={}", allRoleGroups);

	        Map<String, RoleGroupManagementPojo> roleGroupMap = new HashMap<>();
	        for (RoleGroupManagementPojo rg : allRoleGroups) {
	            roleGroupMap.put(rg.getGroupName(), rg);
	            logger.debug("initializeDB added roleGroup to map groupName={}", rg.getGroupName());
	        }
	        logger.debug("initializeDB roleGroupMap keys={}", roleGroupMap.keySet());

	        // ======================================================
	        // STEP 3: Process each user from XML
	        // ======================================================
	        for (UserConfig userConfig : xmlUsers) {
	            logger.debug("initializeDB processing userConfig={}", userConfig);

	            String decryptedUserConfigPassword = decryptJasyptEncPassword(userConfig.getPassword());
	            logger.debug("initializeDB processing userConfig.getPassword()={}", userConfig.getPassword());
	            logger.debug("initializeDB processing decryptedUserConfigPassword={}", decryptedUserConfigPassword);
	            
	            // ==================================================
	            // STEP 3a: Ensure user exists via UserManagementService
	            // ==================================================
	            logger.debug("initializeDB STEP 3a: Ensure user exists via UserManagementService called");

	            UserManagementPojo existingUser = userService.getUserByUserName(userConfig.getUserName());
	            logger.debug("initializeDB existingUser={}", existingUser);

	            UserManagementPojo userPojo;

	            if (existingUser == null) {
	                logger.debug("initializeDB user not found, creating new user userName={}", userConfig.getUserName());

	                userPojo = new UserManagementPojo();
	                logger.debug("initializeDB creating new UserManagementPojo userPojo={}", userPojo);

	                userPojo.setUserName(userConfig.getUserName());
	                logger.debug("initializeDB userName={}", userConfig.getUserName());

	                userPojo.setUserKey(userConfig.getUserKey());
	                logger.debug("initializeDB userKey={}", userConfig.getUserKey());

	                userPojo.setPassword(decryptedUserConfigPassword);
	                logger.debug("initializeDB password=[PROTECTED]");

	                userPojo.setFirstName(userConfig.getFirstName());
	                logger.debug("initializeDB firstName={}", userConfig.getFirstName());

	                userPojo.setLastName(userConfig.getLastName());
	                logger.debug("initializeDB lastName={}", userConfig.getLastName());

	                userPojo.setEmail(userConfig.getEmail());
	                logger.debug("initializeDB email={}", userConfig.getEmail());

	                userPojo.setAddressLine1(userConfig.getAddressLine1());
	                logger.debug("initializeDB addressLine1={}", userConfig.getAddressLine1());

	                userPojo.setAddressLine2(userConfig.getAddressLine2());
	                logger.debug("initializeDB addressLine2={}", userConfig.getAddressLine2());

	                userPojo.setCity(userConfig.getCity());
	                logger.debug("initializeDB city={}", userConfig.getCity());

	                userPojo.setState(userConfig.getState());
	                logger.debug("initializeDB state={}", userConfig.getState());

	                userPojo.setPostCode(userConfig.getPostCode());
	                logger.debug("initializeDB postCode={}", userConfig.getPostCode());

	                userPojo.setCountry(userConfig.getCountry());
	                logger.debug("initializeDB country={}", userConfig.getCountry());

	                userPojo.setActive(userConfig.isActive());
	                logger.debug("initializeDB active={}", userConfig.isActive());

	                userPojo.setLocked(userConfig.isLocked());
	                logger.debug("initializeDB locked={}", userConfig.isLocked());
	                
	                userPojo.setDelimitRoleGroups(userConfig.getDelimitRoleGroups());
	                logger.debug("initializeDB delimitRoleGroups={}", userConfig.getDelimitRoleGroups());

	                userService.create(userPojo);
	                logger.debug("initializeDB created new userPojo={}", userPojo);
	            } else {
	                userPojo = existingUser;

	                // RELOAD_USER_PASSWORD
	                boolean reloadUserPassword = true;
	                try {
	                    reloadUserPassword = propertyService.getBoolean(PropertyKey.RELOAD_USER_PASSWORD);
	                    logger.debug("After propertyService.getBoolean: {}", reloadUserPassword);
	                } catch (Exception e) {
	                    logger.error("Exception fetching property", e);
	                    throw e;
	                }

	                if (reloadUserPassword) {
	                    try {
	                    	if (!decryptedUserConfigPassword.equals(userPojo.getPassword())) {
		                        userPojo.setPassword(decryptedUserConfigPassword);
		                        userService.update(userPojo.getId(), userPojo);
		                        logger.debug("initializeDB password reloaded from XML for existing user userPojo={}", userPojo);
	                    	}
	                    } catch (Exception e) {
	                        logger.error("initializeDB Failed to update password for user userPojo={}", userPojo, e);
	                        throw e;
	                    }
	                }

	                logger.debug("initializeDB found existing userPojo={}", userPojo);
	            }

	            // ==================================================
	            // STEP 3b: Map user to role group
	            // ==================================================
	            logger.debug("initializeDB STEP 3b: Map user to role group called");

	            RoleGroupManagementPojo roleGroupPojo = roleGroupMap.get(userConfig.getRoleGroup());
	            logger.debug("initializeDB roleGroupPojo={}", roleGroupPojo);

	            if (roleGroupPojo == null) {
	                logger.warn("initializeDB skipping mapping: roleGroup not found for user={}", userPojo.getUserName());
	                continue;
	            }

	            // Check if mapping already exists
	            Map<String, Object> mappingParams = new HashMap<>();
	            mappingParams.put("userId", userPojo.getId());
	            mappingParams.put("roleGroupId", roleGroupPojo.getId());
	            logger.debug("initializeDB mappingParams={}", mappingParams);

	            List<UserManagementRoleGroupMappingPojo> existingMappings = getMappingsByParams(mappingParams);
	            logger.debug("initializeDB existingMappings.size={}", existingMappings.size());

	            if (existingMappings.isEmpty()) {
	                UserManagementRoleGroupMappingPojo mapping = new UserManagementRoleGroupMappingPojo();
	                logger.debug("initializeDB creating new mapping={}", mapping);

	                mapping.setUserId(userPojo.getId());
	                logger.debug("initializeDB userId={}", userPojo.getId());

	                mapping.setRoleGroupId(roleGroupPojo.getId());
	                logger.debug("initializeDB roleGroupId={}", roleGroupPojo.getId());

	                create(mapping);
	                logger.debug("initializeDB created new mapping={}", mapping);
	            } else {
	                logger.debug("initializeDB mapping already exists for user={} roleGroup={}",
	                        userPojo.getUserName(), roleGroupPojo.getGroupName());
	            }
	        }

	        logger.debug("initializeDB completed successfully");
	    } catch (Exception e) {
	        logger.error("initializeDB failed", e);
	        throw new RuntimeException("Failed to initialize user-role-group mappings", e);
	    }

	    logger.debug("initializeDB DONE");
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

		if (!params.containsKey("page"))
			params.put("page", "0");
		if (!params.containsKey("size"))
			params.put("size", "20");
		int page = SafeConverter.toIntOrDefault(params.get("page"), 0);
		int size = SafeConverter.toIntOrDefault(params.get("size"), 20);
		int offset = page * size;

		if (!params.containsKey("offset"))
			params.put("offset", String.valueOf(offset));
		if (!params.containsKey("limit"))
			params.put("limit", String.valueOf(size));
		if (!params.containsKey("sortField"))
			params.put("sortField", "id");
		if (!params.containsKey("sortDirection"))
			params.put("sortDirection", "ASC");
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

	public List<UserManagementRoleGroupMappingPojo> getMappingsByParams(Map<String, Object> params)
	{
        logger.debug("getMappingsByParams called");

        List<UserManagementRoleGroupMappingPojo> mappings = mappingMapper.search(params);
        return mappings;
	}
	
	// #{params.userId}
	public List<UserManagementRoleGroupMappingPojo> getMappingsByUserId(Long userId)
	{
        logger.debug("getMappingsByUserId called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId); 
        List<UserManagementRoleGroupMappingPojo> mappings = getMappingsByParams(params);
        
        return mappings;
	}
	
	public List<UserManagementRoleGroupMappingPojo> getAll()
	{
        logger.debug("getAll called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        // No offset/limit => all rows
        List<UserManagementRoleGroupMappingPojo> mappings = getMappingsByParams(params);
        
        return mappings;	
	}
	
    // ==============================================================
    // HELPER METHODS FOR CONTROLLER
    // ==============================================================

    @Override
    public void syncUserRoleGroups(Long userId, List<RoleGroupManagementPojo> newRoleGroups) {
        logger.debug("syncUserRoleGroups called userId={} newRoleGroups={}", userId, newRoleGroups);

        List<UserManagementRoleGroupMappingPojo> existingMappings = getMappingsByUserId(userId);

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
            delete(remove.getId());
            logger.debug("syncUserRoleGroups deleted mapping={}", remove);
        }

        // Add new mappings
        for (RoleGroupManagementPojo add : toAdd) {
            UserManagementRoleGroupMappingPojo mapping = new UserManagementRoleGroupMappingPojo();
            mapping.setUserId(userId);
            mapping.setRoleGroupId(add.getId());
            create(mapping);
            logger.debug("syncUserRoleGroups added mapping={}", mapping);
        }
    }

    @Override
    public List<RoleGroupManagementPojo> getUserRoleGroups(Long userId) {
        logger.debug("getUserRoleGroups called userId={}", userId);

        List<UserManagementRoleGroupMappingPojo> mappings = getMappingsByUserId(userId);
        List<Long> roleGroupIds = mappings.stream()
                .map(UserManagementRoleGroupMappingPojo::getRoleGroupId)
                .toList();

        List<RoleGroupManagementPojo> roleGroups = roleGroupService.getAll().stream()
                .filter(r -> roleGroupIds.contains(r.getId()))
                .collect(Collectors.toList());

        logger.debug("getUserRoleGroups return={}", roleGroups);
        return roleGroups;
    }

    @Override
    public void deleteMappingsByUserId(Long userId) {
        logger.debug("deleteMappingsByUserId called userId={}", userId);
        List<UserManagementRoleGroupMappingPojo> mappings = getMappingsByUserId(userId);
        for (UserManagementRoleGroupMappingPojo mapping : mappings) {
            delete(mapping.getId());
            logger.debug("deleteMappingsByUserId deleted mapping={}", mapping);
        }
    }
    
    public String decryptJasyptEncPassword(String encPassword) {
    	logger.debug("decryptJasyptEncPassword called");
    	logger.debug("decryptJasyptEncPassword encPassword={}", encPassword);
    	
    	String decrypted = EncryptionUtil.decrypt(encPassword);
    	logger.debug("decryptJasyptEncPassword decrypted={}", decrypted);
    	
    	return decrypted;
    }
}
