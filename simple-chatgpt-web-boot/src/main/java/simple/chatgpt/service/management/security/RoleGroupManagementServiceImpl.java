package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import simple.chatgpt.config.management.RoleGroupConfig;
import simple.chatgpt.config.management.RoleRefConfig;
import simple.chatgpt.config.management.loader.SecurityConfigLoader;
import simple.chatgpt.mapper.management.security.RoleGroupManagementMapper;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.util.GenericCache;
import simple.chatgpt.util.PagedResult;

@Service
public class RoleGroupManagementServiceImpl implements RoleGroupManagementService {

    private static final Logger logger = LogManager.getLogger(RoleGroupManagementServiceImpl.class);

    private final RoleGroupManagementMapper groupMapper;
    private final RoleGroupRoleMappingService roleGroupRoleMappingService;
    private final RoleManagementService roleManagementService;
    private final SecurityConfigLoader securityConfigLoader;

    private final GenericCache<Long, RoleGroupManagementPojo> groupCache;
    private final GenericCache<String, Long> idToNameCache;

    @Autowired
    public RoleGroupManagementServiceImpl(
            RoleGroupManagementMapper groupMapper,
            RoleGroupRoleMappingService roleGroupRoleMappingService,
            RoleManagementService roleManagementService,
            SecurityConfigLoader securityConfigLoader,
            @Qualifier("groupCache") GenericCache<Long, RoleGroupManagementPojo> groupCache,
            @Qualifier("idToNameCache") GenericCache<String, Long> idToNameCache) {

        logger.debug("RoleGroupManagementServiceImpl constructor called");
        logger.debug("groupMapper={}", groupMapper);
        logger.debug("roleGroupRoleMappingService={}", roleGroupRoleMappingService);
        logger.debug("roleManagementService={}", roleManagementService);
        logger.debug("securityConfigLoader={}", securityConfigLoader);
        logger.debug("groupCache={}", groupCache);
        logger.debug("idToNameCache={}", idToNameCache);

        this.groupMapper = groupMapper;
        this.roleGroupRoleMappingService = roleGroupRoleMappingService;
        this.roleManagementService = roleManagementService;
        this.securityConfigLoader = securityConfigLoader;
        this.groupCache = groupCache;
        this.idToNameCache = idToNameCache;
    }

    @PostConstruct
    public void postConstruct() {
        initializeDB();
    }

    public void initializeDB() {
        logger.debug("initializeDB called");

        List<RoleGroupConfig> definedGroups = securityConfigLoader.getRoleGroups();
        logger.debug("Loaded role groups from config, size={}", definedGroups.size());

        for (RoleGroupConfig rgConfig : definedGroups) {
            String groupName = rgConfig.getName();

            // Fetch existing group via Map-based service
            RoleGroupManagementPojo existingGroup = getRoleGroup(Map.of("groupName", groupName));

            RoleGroupManagementPojo groupPojo = new RoleGroupManagementPojo();
            groupPojo.setGroupName(groupName);

            if (existingGroup == null) {
                groupMapper.insertRoleGroup(Map.of("params", Map.of("group", groupPojo)));
                logger.debug("Inserted new role group id={} groupName={}", groupPojo.getId(), groupName);
            } else {
                groupPojo = existingGroup;
                logger.debug("Role group already exists id={} groupName={}", existingGroup.getId(), groupName);
            }

            // Cache the group
            groupCache.put(groupPojo.getId(), groupPojo);
            idToNameCache.put(groupName, groupPojo.getId());
            logger.debug("Cached role group id={} groupName={}", groupPojo.getId(), groupName);

            // Map roles to group using Map-based service
            for (RoleRefConfig ref : rgConfig.getRoles()) {
                RoleManagementPojo role = roleManagementService.getRole(Map.of("roleName", ref.getName()));
                if (role != null) {
                    roleGroupRoleMappingService.addRoleToGroupIfNotExists(
                        Map.of(
                            "roleGroupId", groupPojo.getId(),
                            "roleId", role.getId()
                        )
                    );
                    logger.debug(
                        "Ensured role → group mapping: groupName={} roleName={} roleId={}",
                        groupName, ref.getName(), role.getId()
                    );
                } else {
                    logger.warn("Role '{}' not found, skipping mapping", ref.getName());
                }
            }
        }

        logger.debug("initializeDB completed");
    }


    // ---------------- CRUD ----------------

    @Override
    public PagedResult<RoleGroupManagementPojo> searchRoleGroups(Map<String, Object> params) {
        logger.debug("searchRoleGroups called, params={}", params);

        int page = params.get("page") != null ? (int) params.get("page") : 1;
        int size = params.get("size") != null ? (int) params.get("size") : 20;
        int offset = (page - 1) * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<RoleGroupManagementPojo> items = groupMapper.findRoleGroups(Map.of("params", params));
        return new PagedResult<>(items, items.size(), page, size);
    }

    @Override
    public RoleGroupManagementPojo getRoleGroup(Map<String, Object> params) {
        logger.debug("getRoleGroup called, params={}", params);

        Long id = (Long) params.get("roleGroupId");
        String groupName = (String) params.get("groupName");

        if (id != null) {
            return groupCache.get(id, k -> groupMapper.findRoleGroupById(Map.of("params", Map.of("roleGroupId", k))));
        } else if (groupName != null) {
            Long cachedId = idToNameCache.get(groupName, k -> {
                RoleGroupManagementPojo group = groupMapper.findRoleGroupByName(Map.of("params", Map.of("groupName", k)));
                return group != null ? group.getId() : null;
            });
            return groupCache.get(cachedId, k -> null);
        }
        return null;
    }

    @Override
    public RoleGroupManagementPojo createRoleGroup(Map<String, Object> params) {
        logger.debug("createRoleGroup called, params={}", params);
        RoleGroupManagementPojo group = (RoleGroupManagementPojo) params.get("group");

        groupMapper.insertRoleGroup(Map.of("params", Map.of("group", group)));
        groupCache.put(group.getId(), group);
        idToNameCache.put(group.getGroupName(), group.getId());
        logger.debug("Created and cached role group id={} groupName={}", group.getId(), group.getGroupName());
        return group;
    }

    @Override
    public RoleGroupManagementPojo updateRoleGroup(Map<String, Object> params) {
        logger.debug("updateRoleGroup called, params={}", params);

        RoleGroupManagementPojo group = (RoleGroupManagementPojo) params.get("group");
        RoleGroupManagementPojo existing = getRoleGroup(params);
        if (existing == null) {
            logger.debug("No existing role group for update, params={}", params);
            return null;
        }

        group.setId(existing.getId());
        groupMapper.updateRoleGroup(Map.of("params", Map.of("group", group)));
        groupCache.put(existing.getId(), group);
        idToNameCache.put(group.getGroupName(), existing.getId());
        return group;
    }

    @Override
    public void deleteRoleGroup(Map<String, Object> params) {
        logger.debug("deleteRoleGroup called, params={}", params);

        RoleGroupManagementPojo existing = getRoleGroup(params);
        if (existing != null) {
            idToNameCache.invalidate(existing.getGroupName());
            groupCache.invalidate(existing.getId());
            groupMapper.deleteRoleGroupById(Map.of("params", Map.of("roleGroupId", existing.getId())));
            logger.debug("Deleted role group from DB and cache id={}", existing.getId());
        }
    }
}
