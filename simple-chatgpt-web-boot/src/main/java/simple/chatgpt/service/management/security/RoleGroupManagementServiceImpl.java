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

@Service
public class RoleGroupManagementServiceImpl implements RoleGroupManagementService {

    private static final Logger logger = LogManager.getLogger(RoleGroupManagementServiceImpl.class);

    private final RoleGroupManagementMapper groupMapper;
    private final RoleGroupRoleMappingService roleGroupRoleMappingService;
    private final RoleManagementService roleManagementService;
    private final SecurityConfigLoader securityConfigLoader;

    private final GenericCache<Long, RoleGroupManagementPojo> groupCache;
    private final GenericCache<String, Long> nameToIdCache;

    @Autowired
    public RoleGroupManagementServiceImpl(
            RoleGroupManagementMapper groupMapper,
            RoleGroupRoleMappingService roleGroupRoleMappingService,
            RoleManagementService roleManagementService,
            SecurityConfigLoader securityConfigLoader,
            @Qualifier("groupCache") GenericCache<Long, RoleGroupManagementPojo> groupCache,
            @Qualifier("nameToIdCache") GenericCache<String, Long> nameToIdCache) {

        logger.debug("RoleGroupManagementServiceImpl constructor called");
        logger.debug("groupMapper={}", groupMapper);
        logger.debug("roleGroupRoleMappingService={}", roleGroupRoleMappingService);
        logger.debug("roleManagementService={}", roleManagementService);
        logger.debug("securityConfigLoader={}", securityConfigLoader);
        logger.debug("groupCache={}", groupCache);
        logger.debug("nameToIdCache={}", nameToIdCache);

        this.groupMapper = groupMapper;
        this.roleGroupRoleMappingService = roleGroupRoleMappingService;
        this.roleManagementService = roleManagementService;
        this.securityConfigLoader = securityConfigLoader;
        this.groupCache = groupCache;
        this.nameToIdCache = nameToIdCache;
    }

    @PostConstruct
    public void postConstruct() {
        initializeDB();
    }

    public void initializeDB() {
        logger.debug("initializeDB called");

        if (securityConfigLoader == null || groupCache == null || nameToIdCache == null) {
            logger.error("Missing required beans: securityConfigLoader={}, groupCache={}, nameToIdCache={}", 
                securityConfigLoader, groupCache, nameToIdCache);
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

            groupCache.put(groupPojo.getId(), groupPojo);
            nameToIdCache.put(groupName, groupPojo.getId());

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
        RoleGroupManagementPojo group = (RoleGroupManagementPojo) params.get("group");
        groupMapper.insertRoleGroup(ParamWrapper.wrap("params", ParamWrapper.wrap("group", group)));
        groupCache.put(group.getId(), group);
        nameToIdCache.put(group.getGroupName(), group.getId());
        return group;
    }

    // =================== UPDATE ===================
    @Override
    public RoleGroupManagementPojo updateRoleGroup(Map<String, Object> params) {
        logger.debug("updateRoleGroup called, params={}", params);
        RoleGroupManagementPojo group = (RoleGroupManagementPojo) params.get("group");
        groupMapper.updateRoleGroup(ParamWrapper.wrap("params", ParamWrapper.wrap("group", group)));
        groupCache.put(group.getId(), group);
        return group;
    }

    // =================== DELETE ===================
    @Override
    public void deleteRoleGroupById(Map<String, Object> params) {
        logger.debug("deleteRoleGroupById called, params={}", params);
        Long id = (Long) params.get("roleGroupId");
        groupCache.invalidate(id);
        groupMapper.deleteRoleGroupById(ParamWrapper.wrap("params", ParamWrapper.wrap("roleGroupId", id)));
    }

    @Override
    public void deleteRoleGroupByName(Map<String, Object> params) {
        logger.debug("deleteRoleGroupByName called, params={}", params);

        String groupName = (String) params.get("groupName");

        // Use the mappingFunction version of get
        Long id = nameToIdCache.get(groupName, k -> null);

        if (id != null) {
            groupCache.invalidate(id);
        }

        groupMapper.deleteRoleGroupByName(ParamWrapper.wrap("params", ParamWrapper.wrap("groupName", groupName)));
    }


    // =================== READ ===================
    @Override
    public RoleGroupManagementPojo findRoleGroupById(Map<String, Object> params) {
        logger.debug("findRoleGroupById called, params={}", params);
        return groupMapper.findRoleGroupById(params);
    }

    @Override
    public RoleGroupManagementPojo findRoleGroupByName(Map<String, Object> params) {
        logger.debug("findRoleGroupByName called, params={}", params);
        return groupMapper.findRoleGroupByName(params);
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
        int page = params.get("page") != null ? (int) params.get("page") : 1;
        int size = params.get("size") != null ? (int) params.get("size") : 20;
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
        int page = params.get("page") != null ? (int) params.get("page") : 1;
        int size = params.get("size") != null ? (int) params.get("size") : 20;
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
        Long id = (Long) params.get("roleGroupId");
        String groupName = (String) params.get("groupName");

        if (id != null) {
            return groupCache.get(id, k -> findRoleGroupById(ParamWrapper.wrap("params", ParamWrapper.wrap("roleGroupId", k))));
        } else if (groupName != null) {
            Long cachedId = nameToIdCache.get(groupName, k -> {
                RoleGroupManagementPojo group = findRoleGroupByName(ParamWrapper.wrap("params", ParamWrapper.wrap("groupName", k)));
                return group != null ? group.getId() : null;
            });
            return groupCache.get(cachedId, k -> null);
        }
        return null;
    }
}
