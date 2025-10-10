package simple.chatgpt.service.management.security;

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

        try {
            // ======================================================
            // STEP 1. Load role groups from XML config
            // ======================================================
            List<RoleGroupConfig> roleGroupConfigs = securityConfigLoader.getRoleGroups();
            logger.debug("initializeDB roleGroupConfigs={}", roleGroupConfigs);

            for (RoleGroupConfig rgConfig : roleGroupConfigs) {
                logger.debug("initializeDB processing roleGroupConfig={}", rgConfig);
                logger.debug("initializeDB roleGroupConfig.name={}", rgConfig.getName());
                logger.debug("initializeDB roleGroupConfig.description={}", rgConfig.getDescription());
                logger.debug("initializeDB roleGroupConfig.roles={}", rgConfig.getRoles());

                // ======================================================
                // STEP 2. Check if group exists already
                // ======================================================
                Map<String, Object> params = new HashMap<>();
                params.put("groupName", rgConfig.getName());
                List<RoleGroupManagementPojo> existingGroups = groupMapper.search(params);
                logger.debug("initializeDB existingGroups.size={}", existingGroups.size());

                RoleGroupManagementPojo groupPojo;
                if (existingGroups.isEmpty()) {
                    // ======================================================
                    // STEP 3. Create new group
                    // ======================================================
                    groupPojo = new RoleGroupManagementPojo();
                    groupPojo.setGroupName(rgConfig.getName());
                    groupPojo.setDescription(rgConfig.getDescription());
                    groupMapper.create(groupPojo);
                    logger.debug("initializeDB created new groupPojo={}", groupPojo);
                } else {
                    groupPojo = existingGroups.get(0);
                    logger.debug("initializeDB found existing groupPojo={}", groupPojo);
                }

                // ======================================================
                // STEP 4. Link roles to group
                // ======================================================
                Long roleGroupId = groupPojo.getId();
                logger.debug("initializeDB roleGroupId={}", roleGroupId);

                // delete all existing mappings for clean re-sync
                List<RoleGroupRoleMappingPojo> existingMappings =
                        roleGroupRoleMappingService.getMappingsByRoleGroupId(roleGroupId);
                logger.debug("initializeDB existingMappings.size={}", existingMappings.size());
                for (RoleGroupRoleMappingPojo mappingPojo : existingMappings) {
                    roleGroupRoleMappingService.delete(mappingPojo.getId());
                    logger.debug("initializeDB deleted old mapping id={}", mappingPojo.getId());
                }

                // ======================================================
                // STEP 5. Insert fresh mappings based on XML config
                // ======================================================
                List<RoleRefConfig> roleRefs = rgConfig.getRoles();
                for (RoleRefConfig roleRef : roleRefs) {
                    logger.debug("initializeDB processing roleRef={}", roleRef);
                    logger.debug("initializeDB roleRef.name={}", roleRef.getName());

                    Map<String, String> roleParams = new HashMap<>();
                    roleParams.put("roleName", roleRef.getName());
                    PagedResult<RoleManagementPojo> searchResult = roleManagementService.search(roleParams);
                    List<RoleManagementPojo> matchedRoles = searchResult.getItems();
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

                    roleGroupRoleMappingService.create(mapping);
                    logger.debug("initializeDB created mapping={}", mapping);
                }

                logger.debug("initializeDB finished roleGroupConfig.name={}", rgConfig.getName());
            }

            logger.debug("initializeDB completed successfully");

        } catch (Exception e) {
            logger.error("initializeDB failed", e);
            throw new RuntimeException("Failed to initialize role groups from XML", e);
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
    public List<RoleGroupManagementPojo> getAll() {
        logger.debug("getAll called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        // No offset/limit => all rows
        List<RoleGroupManagementPojo> roleGroups = groupMapper.search(params);
        
        // populate cache automatically using get()
        roleGroups.forEach(roleGroup -> roleGroupCache.get(roleGroup.getId(), k -> roleGroup));
        
        return roleGroups;
    }
    
}
