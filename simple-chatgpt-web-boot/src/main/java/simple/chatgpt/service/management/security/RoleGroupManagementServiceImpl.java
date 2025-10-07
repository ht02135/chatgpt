package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.util.GenericCache;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;
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
        @Qualifier("roleGroupCache") GenericCache<Long, RoleGroupManagementPojo> roleGroupCache) 
   {
        logger.debug("RoleGroupManagementServiceImpl constructor called");
        logger.debug("groupMapper={}", groupMapper);
        logger.debug("roleGroupRoleMappingService={}", roleGroupRoleMappingService);
        logger.debug("roleManagementService={}", roleManagementService);
        logger.debug("securityConfigLoader={}", securityConfigLoader);
        logger.debug("roleGroupCache={}", roleGroupCache);

        this.groupMapper = groupMapper;
        this.roleGroupRoleMappingService = roleGroupRoleMappingService;
        this.roleManagementService = roleManagementService;
        this.securityConfigLoader = securityConfigLoader;
        this.roleGroupCache = roleGroupCache;
    }

    @PostConstruct
    public void postConstruct() {
        initializeDB();
    }

    public void initializeDB() {
        logger.debug("initializeDB called");

        if (securityConfigLoader == null || roleGroupCache == null) {
            logger.error("Missing required beans: securityConfigLoader={}, roleGroupCache={}",
                    securityConfigLoader, roleGroupCache);
            return;
        }

        // ----------- LOAD ROLE GROUPS FROM CONFIG -----------
        List<RoleGroupConfig> definedGroups = securityConfigLoader.getRoleGroups();
        logger.debug("Loaded role groups from config, size={}", definedGroups.size());

        // ----------- FETCH ALL ROLE GROUPS ONCE (BY ID) -----------
        List<RoleGroupManagementPojo> allGroups = getAllRoleGroups().getItems();
        logger.debug("Fetched all existing role groups size={}", allGroups.size());

        // Map by name so we can check existence quickly
        Map<String, RoleGroupManagementPojo> groupByName = allGroups.stream()
                .collect(Collectors.toMap(RoleGroupManagementPojo::getGroupName, g -> g));
        logger.debug("Mapped existing role groups by name, size={}", groupByName.size());

        // ----------- FETCH ALL ROLES ONCE -----------
        Map<String, RoleManagementPojo> roleByName = roleManagementService
                .getAllRoles()
                .getItems()
                .stream()
                .collect(Collectors.toMap(RoleManagementPojo::getRoleName, r -> r));
        logger.debug("Fetched all roles size={}", roleByName.size());

        // ----------- MAIN LOOP -----------
        for (RoleGroupConfig rgConfig : definedGroups) {
            String groupName = rgConfig.getName();
            String description = rgConfig.getDescription();

            logger.debug("Processing groupName={} description={}", groupName, description);

            RoleGroupManagementPojo groupPojo = groupByName.get(groupName);
            if (groupPojo == null) {
                logger.debug("Group '{}' not found, inserting new group", groupName);

                groupPojo = new RoleGroupManagementPojo();
                groupPojo.setGroupName(groupName);
                groupPojo.setDescription(description);
                RoleGroupManagementPojo inserted = insertRoleGroup(ParamWrapper.wrap("group", groupPojo));

                logger.debug("Inserted new role group id={} groupName={}", inserted.getId(), inserted.getGroupName());
                // Also put in local map for subsequent lookups in this same init
                groupByName.put(inserted.getGroupName(), inserted);
            } else {
                logger.debug("Group already exists id={} groupName={}", groupPojo.getId(), groupName);
            }

            // ----------- MAP ROLES TO GROUP -----------
            for (RoleRefConfig ref : rgConfig.getRoles()) {
                String roleName = ref.getName();
                RoleManagementPojo role = roleByName.get(roleName);

                logger.debug("Mapping roleName={} to groupName={}", roleName, groupName);

                if (role != null) {
                    roleGroupRoleMappingService.addRoleToGroupIfNotExists(
                            ParamWrapper.wrap("roleGroupId", groupPojo.getId(), "roleId", role.getId())
                    );
                    logger.debug("Mapped role→group: groupName={} roleName={} roleId={}", 
                            groupName, roleName, role.getId());
                } else {
                    logger.warn("Role '{}' not found in pre-fetched roles, skipping mapping to group '{}'",
                            roleName, groupName);
                }
            }
        }

        logger.debug("initializeDB completed");
    }

    // =================== CREATE ===================
    @Override
    public RoleGroupManagementPojo insertRoleGroup(Map<String, Object> params) {
        logger.debug("insertRoleGroup called, params={}", params);

        RoleGroupManagementPojo group = ParamWrapper.unwrap(params, "group");
        logger.debug("insertRoleGroup before insert group={}", group);

        // Insert into DB
        groupMapper.insertRoleGroup(ParamWrapper.wrap("group", group));
        logger.debug("insertRoleGroup after insert, group.id={}", group.getId());

        // Re-fetch from DB to get all populated fields (timestamps, etc.)
        RoleGroupManagementPojo fullGroup = groupMapper.findRoleGroupById(
            ParamWrapper.wrap("roleGroupId", group.getId())
        );
        logger.debug("insertRoleGroup fetched fullGroup={}", fullGroup);

        return fullGroup;
    }

    // =================== UPDATE ===================
    @Override
    public RoleGroupManagementPojo updateRoleGroup(Map<String, Object> params) {
        logger.debug("updateRoleGroup called, params={}", params);
        RoleGroupManagementPojo group = ParamWrapper.unwrap(params, "group");
        groupMapper.updateRoleGroup(ParamWrapper.wrap("group", group));
        return group;
    }

    // =================== DELETE ===================
    @Override
    public void deleteRoleGroupById(Map<String, Object> params) {
        logger.debug("deleteRoleGroupById called, params={}", params);
        Long id = ParamWrapper.unwrap(params, "roleGroupId");
        roleGroupCache.invalidate(id);
        groupMapper.deleteRoleGroupById(ParamWrapper.wrap("roleGroupId", id));
    }

    // =================== READ ===================
    @Override
    public RoleGroupManagementPojo findRoleGroupById(Map<String, Object> params) {
        logger.debug("findRoleGroupById called, params={}", params);
        return groupMapper.findRoleGroupById(params);
    }

    @Override
    public PagedResult<RoleGroupManagementPojo> findAllRoleGroups() {
        logger.debug("findAllRoleGroups called");
        List<RoleGroupManagementPojo> items = groupMapper.findAllRoleGroups();
        long totalCount = items != null ? items.size() : 0;
        return new PagedResult<>(items, totalCount, 1, (int) totalCount);
    }

    @Override
    public PagedResult<RoleGroupManagementPojo> getAllRoleGroups() {
        logger.debug("getAllRoleGroups called");
        List<RoleGroupManagementPojo> items = groupMapper.getAllRoleGroups();
        long totalCount = items != null ? items.size() : 0;
        return new PagedResult<>(items, totalCount, 1, (int) totalCount);
    }

    // =================== SEARCH / PAGINATION ===================
    @Override
    public PagedResult<RoleGroupManagementPojo> findRoleGroups(Map<String, Object> params) {
        logger.debug("findRoleGroups called, params={}", params);

        /*
        hung: DONT REMOVE THIS CODE
        */
        int page = 0;
        int size = 20;
        try {
            page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0); 
        } catch (NumberFormatException e) {
            logger.warn("Invalid page param {}, defaulting to 0", ParamWrapper.unwrap(params, "page", 0), e);
        }
        try {
            size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        } catch (NumberFormatException e) {
            logger.warn("Invalid size param {}, defaulting to 20", ParamWrapper.unwrap(params, "size", 20), e);
        }
        int offset = (page - 1) * size;
        params.put("offset", offset);
        params.put("limit", size);

        List<RoleGroupManagementPojo> items = groupMapper.findRoleGroups(params);
        long totalCount = groupMapper.countRoleGroups(params);

        return new PagedResult<>(items, totalCount, page, size);
    }

    @Override
    public PagedResult<RoleGroupManagementPojo> searchRoleGroups(Map<String, Object> params) {
        logger.debug("searchRoleGroups called, params={}", params);

        /*
        hung: DONT REMOVE THIS CODE
        */
        int page = 0;
        int size = 20;
        try {
            page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0); 
        } catch (NumberFormatException e) {
            logger.warn("Invalid page param {}, defaulting to 0", ParamWrapper.unwrap(params, "page", 0), e);
        }
        try {
            size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        } catch (NumberFormatException e) {
            logger.warn("Invalid size param {}, defaulting to 20", ParamWrapper.unwrap(params, "size", 20), e);
        }
        int offset = (page - 1) * size;
        params.put("offset", offset);
        params.put("limit", size);

        List<RoleGroupManagementPojo> items = groupMapper.searchRoleGroups(params);
        long totalCount = groupMapper.countRoleGroups(params);

        return new PagedResult<>(items, totalCount, page, size);
    }

    // =================== COUNT ===================
    @Override
    public long countRoleGroups(Map<String, Object> params) {
        logger.debug("countRoleGroups called, params={}", params);
        return groupMapper.countRoleGroups(params);
    }

    // =================== HELPER ===================
    public RoleGroupManagementPojo getRoleGroup(Map<String, Object> params) {
        logger.debug("getRoleGroup called, params={}", params);
        Long id = ParamWrapper.unwrap(params, "roleGroupId");
        if (id != null) {
            return roleGroupCache.get(id, k -> findRoleGroupById(ParamWrapper.wrap("roleGroupId", k)));
        }
        return null;
    }
}
