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
        logger.debug("initializeDB START");

        if (securityConfigLoader == null || roleGroupCache == null) {
            logger.error("Missing required beans: securityConfigLoader={}, roleGroupCache={}", securityConfigLoader, roleGroupCache);
            logger.debug("initializeDB DONE");
            return;
        }

        // ----------- LOAD ROLE GROUPS FROM CONFIG -----------
        List<RoleGroupConfig> definedGroups = securityConfigLoader.getRoleGroups();
        logger.debug("initializeDB definedGroups={}", definedGroups.size());

        // ----------- FETCH ALL ROLE GROUPS ONCE -----------
        List<RoleGroupManagementPojo> allGroups = getAllRoleGroups().getItems();
        logger.debug("initializeDB allGroups={}", allGroups.size());

        Map<String, RoleGroupManagementPojo> groupByName = allGroups.stream()
                .collect(Collectors.toMap(RoleGroupManagementPojo::getGroupName, g -> g));
        logger.debug("initializeDB groupByName={}", groupByName.size());

        Map<String, RoleManagementPojo> roleByName = roleManagementService.getAllRoles()
                .getItems()
                .stream()
                .collect(Collectors.toMap(RoleManagementPojo::getRoleName, r -> r));
        logger.debug("initializeDB roleByName={}", roleByName.size());

        for (RoleGroupConfig rgConfig : definedGroups) {
            String groupName = rgConfig.getName();
            String description = rgConfig.getDescription();

            RoleGroupManagementPojo groupPojo = groupByName.get(groupName);
            if (groupPojo == null) {
                RoleGroupManagementPojo inserted = insertRoleGroup(ParamWrapper.wrap("group", new RoleGroupManagementPojo() {{
                    setGroupName(groupName);
                    setDescription(description);
                }}));
                groupByName.put(inserted.getGroupName(), inserted);
                logger.debug("initializeDB inserted group id={}", inserted.getId());
            }

            for (RoleRefConfig ref : rgConfig.getRoles()) {
                String roleName = ref.getName();
                RoleManagementPojo role = roleByName.get(roleName);
                if (role != null) {
                    roleGroupRoleMappingService.addRoleToGroupIfNotExists(
                            ParamWrapper.wrap("roleGroupId", groupByName.get(groupName).getId(), "roleId", role.getId())
                    );
                }
            }
        }

        logger.debug("initializeDB DONE");
    }

    // =================== CREATE ===================
    @Override
    public RoleGroupManagementPojo insertRoleGroup(Map<String, Object> params) {
        logger.debug("insertRoleGroup START");
        logger.debug("insertRoleGroup params={}", params);

        RoleGroupManagementPojo group = ParamWrapper.unwrap(params, "group");
        groupMapper.insertRoleGroup(ParamWrapper.wrap("group", group));
        RoleGroupManagementPojo fullGroup = groupMapper.findRoleGroupById(ParamWrapper.wrap("roleGroupId", group.getId()));

        logger.debug("insertRoleGroup return={}", fullGroup);
        return fullGroup;
    }

    // =================== UPDATE ===================
    @Override
    public RoleGroupManagementPojo updateRoleGroup(Map<String, Object> params) {
        logger.debug("updateRoleGroup START");
        logger.debug("updateRoleGroup params={}", params);

        RoleGroupManagementPojo group = ParamWrapper.unwrap(params, "group");
        groupMapper.updateRoleGroup(ParamWrapper.wrap("group", group));

        logger.debug("updateRoleGroup return={}", group);
        return group;
    }

    // =================== DELETE ===================
    @Override
    public void deleteRoleGroupById(Map<String, Object> params) {
        logger.debug("deleteRoleGroupById START");
        logger.debug("deleteRoleGroupById params={}", params);

        Long id = ParamWrapper.unwrap(params, "roleGroupId");
        roleGroupCache.invalidate(id);
        groupMapper.deleteRoleGroupById(ParamWrapper.wrap("roleGroupId", id));

        logger.debug("deleteRoleGroupById DONE");
    }

    // =================== READ ===================
    @Override
    public RoleGroupManagementPojo findRoleGroupById(Map<String, Object> params) {
        logger.debug("findRoleGroupById START");
        logger.debug("findRoleGroupById params={}", params);

        RoleGroupManagementPojo result = internalGetRoleGroup(params);
        logger.debug("findRoleGroupById return={}", result);
        return result;
    }

    @Override
    public PagedResult<RoleGroupManagementPojo> findAllRoleGroups() {
        logger.debug("findAllRoleGroups START");

        List<RoleGroupManagementPojo> items = groupMapper.findAllRoleGroups();
        PagedResult<RoleGroupManagementPojo> result = new PagedResult<>(items, items.size(), 1, items.size());

        logger.debug("findAllRoleGroups return={}", result);
        return result;
    }

    @Override
    public PagedResult<RoleGroupManagementPojo> getAllRoleGroups() {
        logger.debug("getAllRoleGroups START");

        List<RoleGroupManagementPojo> items = groupMapper.getAllRoleGroups();
        PagedResult<RoleGroupManagementPojo> result = new PagedResult<>(items, items.size(), 1, items.size());

        logger.debug("getAllRoleGroups return={}", result);
        return result;
    }

    // =================== SEARCH / PAGINATION ===================
    @Override
    public PagedResult<RoleGroupManagementPojo> findRoleGroups(Map<String, Object> params) {
        logger.debug("findRoleGroups START");
        logger.debug("findRoleGroups params={}", params);

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;
        params.put("offset", offset);
        params.put("limit", size);

        List<RoleGroupManagementPojo> items = groupMapper.findRoleGroups(params);
        long totalCount = groupMapper.countRoleGroups(params);
        PagedResult<RoleGroupManagementPojo> result = new PagedResult<>(items, totalCount, page, size);

        logger.debug("findRoleGroups return={}", result);
        return result;
    }

    @Override
    public PagedResult<RoleGroupManagementPojo> searchRoleGroups(Map<String, Object> params) {
        logger.debug("searchRoleGroups START");
        logger.debug("searchRoleGroups params={}", params);

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;
        params.put("offset", offset);
        params.put("limit", size);

        List<RoleGroupManagementPojo> items = groupMapper.searchRoleGroups(params);
        long totalCount = groupMapper.countRoleGroups(params);
        PagedResult<RoleGroupManagementPojo> result = new PagedResult<>(items, totalCount, page, size);

        logger.debug("searchRoleGroups return={}", result);
        return result;
    }

    // =================== COUNT ===================
    @Override
    public long countRoleGroups(Map<String, Object> params) {
        logger.debug("countRoleGroups START");
        logger.debug("countRoleGroups params={}", params);

        long count = groupMapper.countRoleGroups(params);

        logger.debug("countRoleGroups return={}", count);
        return count;
    }

    // =================== HELPER ===================
    public RoleGroupManagementPojo internalGetRoleGroup(Map<String, Object> params) {
        logger.debug("internalGetRoleGroup START");
        logger.debug("internalGetRoleGroup params={}", params);

        Long roleGroupId = ParamWrapper.unwrap(params, "roleGroupId"); // <-- fixed
        if (roleGroupId == null) {
            logger.error("roleGroupId is null");
            return null;
        }

        RoleGroupManagementPojo result = roleGroupCache.get(roleGroupId, k ->
            groupMapper.findRoleGroupById(ParamWrapper.wrap("roleGroupId", k)) // <-- fixed
        );
        
        logger.debug("internalGetRoleGroup return={}", result);
        return result;
    }
}
