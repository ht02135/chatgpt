package simple.chatgpt.service.management.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import simple.chatgpt.config.management.loader.SecurityConfigLoader;
import simple.chatgpt.config.management.security.RoleGroupConfig;
import simple.chatgpt.config.management.security.RoleRefConfig;
import simple.chatgpt.mapper.management.security.RoleGroupManagementMapper;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.util.GenericCache;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.SafeConverter;

@Service
public class RoleGroupManagementServiceImpl implements RoleGroupManagementService {

    private static final Logger logger = LogManager.getLogger(RoleGroupManagementServiceImpl.class);

    private final RoleGroupManagementMapper groupMapper;
    private final RoleGroupRoleMappingService roleGroupRoleMappingService;
    private final RoleManagementService roleManagementService;
    private final SecurityConfigLoader securityConfigLoader;
    private final GenericCache<Long, RoleGroupManagementPojo> roleGroupCache;

    @Autowired
    public RoleGroupManagementServiceImpl(
            RoleGroupManagementMapper groupMapper,
            RoleGroupRoleMappingService roleGroupRoleMappingService,
            RoleManagementService roleManagementService,
            SecurityConfigLoader securityConfigLoader,
            @Qualifier("roleGroupCache") GenericCache<Long, RoleGroupManagementPojo> roleGroupCache) {

        logger.debug("RoleGroupManagementServiceImpl START");
        logger.debug("RoleGroupManagementServiceImpl groupMapper={}", groupMapper);
        logger.debug("RoleGroupManagementServiceImpl roleGroupRoleMappingService={}", roleGroupRoleMappingService);
        logger.debug("RoleGroupManagementServiceImpl roleManagementService={}", roleManagementService);
        logger.debug("RoleGroupManagementServiceImpl securityConfigLoader={}", securityConfigLoader);
        logger.debug("RoleGroupManagementServiceImpl roleGroupCache={}", roleGroupCache);

        this.groupMapper = groupMapper;
        this.roleGroupRoleMappingService = roleGroupRoleMappingService;
        this.roleManagementService = roleManagementService;
        this.securityConfigLoader = securityConfigLoader;
        this.roleGroupCache = roleGroupCache;

        logger.debug("RoleGroupManagementServiceImpl DONE");
    }

    @PostConstruct
    public void postConstruct() {
        logger.debug("postConstruct START");
        initializeDB();
        logger.debug("postConstruct DONE");
    }

    public void initializeDB() {
        logger.debug("initializeDB called");

        List<RoleGroupConfig> roleGroupConfigs = securityConfigLoader.getRoleGroups();
        for (RoleGroupConfig rgConfig : roleGroupConfigs) {
            String groupName = rgConfig.getName();
            logger.debug("initializeDB processing roleGroupConfig={}", groupName);

            // ===============================
            // STEP 1: Check if role group exists
            // ===============================
            Map<String, String> params = new HashMap<>();
            params.put("groupName", groupName);
            PagedResult<RoleGroupManagementPojo> searchResult = this.search(params);
            RoleGroupManagementPojo groupPojo;

            if (searchResult.getTotalCount() > 0) {
                groupPojo = searchResult.getItems().get(0);
                logger.debug("initializeDB found existing role group id={}", groupPojo.getId());
            } else {
                // ===============================
                // STEP 2: Create new role group
                // ===============================
                groupPojo = new RoleGroupManagementPojo();
                groupPojo.setGroupName(groupName);
                groupPojo.setDescription(rgConfig.getDescription());
                groupPojo.setDelimitRoles(rgConfig.getDelimitRoles()); // <<< Added this line
                this.create(groupPojo);
                logger.debug("initializeDB created new role group id={}", groupPojo.getId());
            }

            Long roleGroupId = groupPojo.getId();

            // ======================================================
            // STEP 3. Remove existing mappings
            // ======================================================
            try {
                roleGroupRoleMappingService.deleteByRoleGroupId(roleGroupId);
                logger.debug("initializeDB cleared existing mappings for roleGroupId={}", roleGroupId);
            } catch (Exception e) {
                logger.warn("initializeDB: ####################");
                logger.warn("initializeDB failed to clear mappings for roleGroupId={}: {}", roleGroupId, e.getMessage());
                logger.warn("initializeDB: ####################");
            }

            // ======================================================
            // STEP 4. Insert fresh mappings based on XML config
            // ======================================================
            List<RoleRefConfig> roleRefs = rgConfig.getRoles();
            for (RoleRefConfig roleRef : roleRefs) {
                logger.debug("initializeDB processing roleRef={}", roleRef);
                logger.debug("initializeDB roleRef.name={}", roleRef.getName());

                Map<String, String> roleParams = new HashMap<>();
                roleParams.put("roleName", roleRef.getName());
                PagedResult<RoleManagementPojo> matchedRolesResult = roleManagementService.search(roleParams);
                List<RoleManagementPojo> matchedRoles = matchedRolesResult.getItems();
                logger.debug("initializeDB matchedRoles.size={}", matchedRoles.size());

                if (matchedRoles.isEmpty()) {
                    logger.debug("initializeDB skipping missing roleRef.name={}", roleRef.getName());
                    continue;
                }

                RoleManagementPojo role = matchedRoles.get(0);

                RoleGroupRoleMappingPojo mapping = new RoleGroupRoleMappingPojo();
                mapping.setRoleGroupId(roleGroupId);
                mapping.setRoleId(role.getId());
                mapping.setRoleGroupName(groupPojo.getGroupName());
                mapping.setRoleName(role.getRoleName());

                // ===============================
                // STEP 5. Try-catch create mapping
                // ===============================
                try {
                    roleGroupRoleMappingService.create(mapping);
                    logger.debug("initializeDB created mapping={}", mapping);
                } catch (Exception e) {
                    logger.warn("initializeDB: ####################");
                    logger.warn("initializeDB failed to create mapping for roleRef.name={} and roleGroupId={}: {}",
                                roleRef.getName(), roleGroupId, e.getMessage());
                    logger.warn("initializeDB: ####################");
                }
            }
        }

        logger.debug("initializeDB DONE");
    }

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @Override
    public RoleGroupManagementPojo create(RoleGroupManagementPojo roleGroup) {
        logger.debug("create called");
        logger.debug("create roleGroup={}", roleGroup);
        groupMapper.create(roleGroup);
        return roleGroup;
    }

    @Override
    public RoleGroupManagementPojo update(Long id, RoleGroupManagementPojo roleGroup) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update roleGroup={}", roleGroup);

        // DB update first
        groupMapper.update(id, roleGroup);
        // Invalidate cache after DB update
        roleGroupCache.invalidate(id);
        return roleGroup;
    }

    @Override
    public PagedResult<RoleGroupManagementPojo> search(Map<String, String> params) {
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
        
        List<RoleGroupManagementPojo> items = groupMapper.search(mapperParams);
        long totalCount = items.size();
        
        // === Populate cache by ID ===
        for (RoleGroupManagementPojo roleGroup : items) {
            roleGroupCache.get(roleGroup.getId(), k -> {
                logger.debug("search: caching roleGroup id={}", k);
                return roleGroup;
            });
        }
        
        PagedResult<RoleGroupManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("search return={}", result);
        return result;
    }

    @Override
    public RoleGroupManagementPojo get(Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);

        RoleGroupManagementPojo roleGroup = roleGroupCache.get(id, k -> {
            RoleGroupManagementPojo fromDb = groupMapper.get(k);
            return fromDb;
        });
        return roleGroup;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);

        // Invalidate cache first
        roleGroupCache.invalidate(id);
        // Then delete from DB
        groupMapper.delete(id);
    }
    
    // ======= OTHER METHODS =======

    @Override
    public List<RoleGroupManagementPojo> getRoleGroupByParams(Map<String, Object> params)
    {
        logger.debug("getRoleGroupByParams called");

        List<RoleGroupManagementPojo> roleGroups = groupMapper.search(params);
        
        // populate cache automatically using get()
        roleGroups.forEach(roleGroup -> roleGroupCache.get(roleGroup.getId(), k -> roleGroup));
        
        return roleGroups;
    }
	
    @Override
    public List<RoleGroupManagementPojo> getAll() {
        logger.debug("getAll called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        List<RoleGroupManagementPojo> roleGroups = getRoleGroupByParams(params);
        
        // populate cache automatically using get()
        roleGroups.forEach(roleGroup -> roleGroupCache.get(roleGroup.getId(), k -> roleGroup));
        
        return roleGroups;
    }
    
    // #{params.groupName}
    @Override
    public RoleGroupManagementPojo getRoleGroupByGroupName(String groupName) {
        logger.debug("getRoleGroupByGroupName called");
        logger.debug("getRoleGroupByGroupName groupName={}", groupName);

        // Query database by groupName
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", groupName);

        List<RoleGroupManagementPojo> roleGroups = getRoleGroupByParams(params);

        if (roleGroups == null || roleGroups.isEmpty()) {
            return null;
        }

        // Take the first matching role group
        RoleGroupManagementPojo roleGroup = roleGroups.get(0);

        /*
        hung : dont remove
        normally we wan to run cache first, but the cache is id->pojo
        and this method is groupName, so no cache. but since we get
        the pojo, we cache it by id anyway...
        */
        // Cache it using ID as key
        roleGroupCache.get(roleGroup.getId(), k -> {
            return roleGroup;
        });

        logger.debug("getRoleGroupByGroupName returning roleGroup={}", roleGroup);
        return roleGroup;
    }
    
    @Override
    public List<String> getRoleNamesByGroupName(String groupName) {
    	logger.debug("getRoleNamesByGroupName called");
    	logger.debug("getRoleNamesByGroupName groupName={}", groupName);
    	
        RoleGroupManagementPojo roleGroup = getRoleGroupByGroupName(groupName);
        logger.debug("getRoleNamesByGroupName roleGroup={}", roleGroup);
        if (roleGroup == null) {
            return List.of(); // empty list if group not found
        }

        String delimitRoleNames = roleGroup.getDelimitRoles();
        if (delimitRoleNames == null || delimitRoleNames.isBlank()) {
            return List.of(); // empty list if no roles
        }

        // Split by "|" and filter out empty strings
        String[] tokens = delimitRoleNames.split("\\|");
        java.util.Set<String> roleNameSet = new java.util.LinkedHashSet<>();
        for (String token : tokens) {
            if (!token.isBlank()) {
                roleNameSet.add(token.trim());
            }
        }

        List<String> roleNames = new ArrayList<>(roleNameSet);
        logger.debug("getRoleNamesByGroupName roles={}", roleNames);
        return roleNames;
    }
    
}
