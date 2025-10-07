package simple.chatgpt.service.management.security;

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

    private final GenericCache<Long, RoleGroupManagementPojo> groupCache;

    @Autowired
    public RoleGroupManagementServiceImpl(
            RoleGroupManagementMapper groupMapper,
            RoleGroupRoleMappingService roleGroupRoleMappingService,
            RoleManagementService roleManagementService,
            SecurityConfigLoader securityConfigLoader,
            @Qualifier("groupCache") GenericCache<Long, RoleGroupManagementPojo> groupCache) {

        logger.debug("RoleGroupManagementServiceImpl constructor called");
        logger.debug("groupMapper={}", groupMapper);
        logger.debug("roleGroupRoleMappingService={}", roleGroupRoleMappingService);
        logger.debug("roleManagementService={}", roleManagementService);
        logger.debug("securityConfigLoader={}", securityConfigLoader);
        logger.debug("groupCache={}", groupCache);

        this.groupMapper = groupMapper;
        this.roleGroupRoleMappingService = roleGroupRoleMappingService;
        this.roleManagementService = roleManagementService;
        this.securityConfigLoader = securityConfigLoader;
        this.groupCache = groupCache;
    }

    @PostConstruct
    public void postConstruct() {
        initializeDB();
    }

    public void initializeDB() {
        logger.debug("initializeDB called");

        if (securityConfigLoader == null || groupCache == null) {
            logger.error("Missing required beans: securityConfigLoader={}, groupCache={}", 
                securityConfigLoader, groupCache);
            return;
        }
        
        List<RoleGroupConfig> definedGroups = securityConfigLoader.getRoleGroups();
        logger.debug("Loaded role groups from config, size={}", definedGroups.size());

        for (RoleGroupConfig rgConfig : definedGroups) {
            String groupName = rgConfig.getName();
            RoleGroupManagementPojo existingGroup = getRoleGroup(ParamWrapper.wrap("groupName", groupName));

            RoleGroupManagementPojo groupPojo;
            if (existingGroup == null) {
                groupPojo = new RoleGroupManagementPojo();
                groupPojo.setGroupName(groupName);
                insertRoleGroup(ParamWrapper.wrap("group", groupPojo));
                logger.debug("Inserted new role group id={} groupName={}", groupPojo.getId(), groupName);
            } else {
                groupPojo = existingGroup;
                logger.debug("Role group already exists id={} groupName={}", existingGroup.getId(), groupName);
            }

            for (RoleRefConfig ref : rgConfig.getRoles()) {
                RoleManagementPojo role = roleManagementService.findRoleByName(ParamWrapper.wrap("roleName", ref.getName()));
                if (role != null) {
                    roleGroupRoleMappingService.addRoleToGroupIfNotExists(
                        ParamWrapper.wrap("roleGroupId", groupPojo.getId(), "roleId", role.getId())
                    );
                    logger.debug(
                        "Mapped role → group: groupName={} roleName={} roleId={}",
                        groupName, ref.getName(), role.getId()
                    );
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
        groupCache.invalidate(id);
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
            return groupCache.get(id, k -> findRoleGroupById(ParamWrapper.wrap("roleGroupId", k)));
        }
        return null;
    }
}
