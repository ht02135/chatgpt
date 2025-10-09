package simple.chatgpt.service.management.security;

import java.util.HashMap;
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

        List<RoleGroupConfig> definedGroups = securityConfigLoader.getRoleGroups();
        logger.debug("initializeDB definedGroups={}", definedGroups.size());

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
        groupMapper.update(id, roleGroup);
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
        RoleGroupManagementPojo roleGroup = groupMapper.get(id);
        logger.debug("get return={}", roleGroup);
        return roleGroup;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);
        groupMapper.delete(id);
    }

    // ======= OTHER METHODS =======
    
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

        List<RoleGroupManagementPojo> items = groupMapper.findRoleGroups(params);
        long totalCount = groupMapper.countRoleGroups(params);

        PagedResult<RoleGroupManagementPojo> result = new PagedResult<>(items, totalCount, 1, (int) totalCount);
        logger.debug("findRoleGroups return={}", result);
        return result;
    }

    @Override
    public PagedResult<RoleGroupManagementPojo> searchRoleGroups(Map<String, Object> params) {
        logger.debug("searchRoleGroups START");
        logger.debug("searchRoleGroups params={}", params);

        List<RoleGroupManagementPojo> items = groupMapper.searchRoleGroups(params);
        long totalCount = groupMapper.countRoleGroups(params);

        PagedResult<RoleGroupManagementPojo> result = new PagedResult<>(items, totalCount, 1, (int) totalCount);
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

        Long roleGroupId = ParamWrapper.unwrap(params, "roleGroupId");
        if (roleGroupId == null) {
            logger.error("roleGroupId is null");
            return null;
        }

        RoleGroupManagementPojo result = roleGroupCache.get(roleGroupId, k ->
            groupMapper.findRoleGroupById(ParamWrapper.wrap("roleGroupId", k))
        );

        logger.debug("internalGetRoleGroup return={}", result);
        return result;
    }
}
