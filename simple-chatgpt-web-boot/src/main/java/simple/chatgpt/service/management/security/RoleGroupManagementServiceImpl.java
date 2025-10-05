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
            RoleGroupManagementPojo existingGroup = groupMapper.findRoleGroupByName(groupName);

            RoleGroupManagementPojo groupPojo = new RoleGroupManagementPojo();
            groupPojo.setGroupName(groupName);

            if (existingGroup == null) {
                groupMapper.insertRoleGroup(groupPojo);
                logger.debug("Inserted new role group id={} groupName={}", groupPojo.getId(), groupName);
            } else {
                groupPojo = existingGroup;
                logger.debug("Role group already exists id={} groupName={}", existingGroup.getId(), groupName);
            }

            // Cache the group
            groupCache.put(groupPojo.getId(), groupPojo);
            idToNameCache.put(groupName, groupPojo.getId());
            logger.debug("Cached role group id={} groupName={}", groupPojo.getId(), groupName);

            // Ensure role → group mappings exist via mapping service
            for (RoleRefConfig ref : rgConfig.getRoles()) {
                String roleName = ref.getName();
                RoleManagementPojo role = roleManagementService.getByRoleName(roleName);
                if (role == null) {
                    logger.warn("Role '{}' not found in DB, skipping mapping", roleName);
                    continue;
                }

                List<RoleGroupManagementPojo> dummy = null; // just to satisfy any interface; mappings handled in mapping service
                // Add mapping if missing
                roleGroupRoleMappingService.addRoleToGroupIfNotExists(groupPojo.getId(), role.getId());
                logger.debug("Ensured role → group mapping: groupName={} roleName={} roleId={}",
                        groupName, roleName, role.getId());
            }
        }

        logger.debug("initializeDB completed");
    }

    // ---------------- CRUD ----------------

    @Override
    public PagedResult<RoleGroupManagementPojo> searchRoleGroups(Map<String, String> params) {
        logger.debug("searchRoleGroups called, params={}", params);
        List<RoleGroupManagementPojo> items = groupMapper.findAllRoleGroups();
        return new PagedResult<>(items, items.size(),
                params.containsKey("page") ? Integer.parseInt(params.get("page")) : 1,
                params.containsKey("size") ? Integer.parseInt(params.get("size")) : items.size());
    }

    @Override
    public RoleGroupManagementPojo getRoleGroupById(Long id) {
        logger.debug("getRoleGroupById called, id={}", id);
        return groupCache.get(id, k -> groupMapper.findRoleGroupById(k));
    }

    @Override
    public RoleGroupManagementPojo getByGroupName(String groupName) {
        logger.debug("getByGroupName called, groupName={}", groupName);
        Long id = idToNameCache.get(groupName, k -> {
            RoleGroupManagementPojo group = groupMapper.findRoleGroupByName(k);
            return group != null ? group.getId() : null;
        });
        return groupCache.get(id, k -> null);
    }

    @Override
    public RoleGroupManagementPojo createRoleGroup(RoleGroupManagementPojo group) {
        logger.debug("createRoleGroup called, group={}", group);
        groupMapper.insertRoleGroup(group);
        groupCache.put(group.getId(), group);
        idToNameCache.put(group.getGroupName(), group.getId());
        logger.debug("Created and cached role group id={} groupName={}", group.getId(), group.getGroupName());
        return group;
    }

    @Override
    public RoleGroupManagementPojo updateRoleGroupById(Long id, RoleGroupManagementPojo group) {
        logger.debug("updateRoleGroupById called, id={} group={}", id, group);
        group.setId(id);
        groupMapper.updateRoleGroup(group);
        groupCache.put(id, group);
        idToNameCache.put(group.getGroupName(), id);
        return group;
    }

    @Override
    public RoleGroupManagementPojo updateRoleGroupByName(String groupName, RoleGroupManagementPojo group) {
        logger.debug("updateRoleGroupByName called, groupName={} group={}", groupName, group);
        RoleGroupManagementPojo existing = getByGroupName(groupName);
        if (existing == null) {
            logger.debug("No existing role group found for update, groupName={}", groupName);
            return null;
        }
        return updateRoleGroupById(existing.getId(), group);
    }

    @Override
    public void deleteRoleGroupById(Long id) {
        logger.debug("deleteRoleGroupById called, id={}", id);
        RoleGroupManagementPojo existing = getRoleGroupById(id);
        if (existing != null) {
            idToNameCache.invalidate(existing.getGroupName());
            groupCache.invalidate(id);
            // role → group mapping cache handled inside RoleGroupRoleMappingService
            groupMapper.deleteRoleGroupById(id);
            logger.debug("Deleted role group from DB and cache id={}", id);
        }
    }

    @Override
    public void deleteRoleGroupByName(String groupName) {
        logger.debug("deleteRoleGroupByName called, groupName={}", groupName);
        RoleGroupManagementPojo existing = getByGroupName(groupName);
        if (existing != null) {
            deleteRoleGroupById(existing.getId());
        }
    }
}
