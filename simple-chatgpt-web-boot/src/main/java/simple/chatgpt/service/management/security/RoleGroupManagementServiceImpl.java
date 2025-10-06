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
            RoleGroupManagementPojo existingGroup = getRoleGroup(Map.of("groupName", groupName));

            RoleGroupManagementPojo groupPojo = new RoleGroupManagementPojo();
            groupPojo.setGroupName(groupName);

            if (existingGroup == null) {
                insertRoleGroup(Map.of("group", groupPojo));
                logger.debug("Inserted new role group id={} groupName={}", groupPojo.getId(), groupName);
            } else {
                groupPojo = existingGroup;
                logger.debug("Role group already exists id={} groupName={}", existingGroup.getId(), groupName);
            }

            groupCache.put(groupPojo.getId(), groupPojo);
            idToNameCache.put(groupName, groupPojo.getId());

            for (RoleRefConfig ref : rgConfig.getRoles()) {
                RoleManagementPojo role = roleManagementService.findRoleByName(Map.of("roleName", ref.getName()));
                if (role != null) {
                    roleGroupRoleMappingService.addRoleToGroupIfNotExists(
                        Map.of("roleGroupId", groupPojo.getId(), "roleId", role.getId())
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
    public int insertRoleGroup(Map<String, Object> params) {
        logger.debug("insertRoleGroup called, params={}", params);
        return groupMapper.insertRoleGroup(params);
    }

    // =================== UPDATE ===================
    @Override
    public int updateRoleGroup(Map<String, Object> params) {
        logger.debug("updateRoleGroup called, params={}", params);
        return groupMapper.updateRoleGroup(params);
    }

    // =================== DELETE ===================
    @Override
    public int deleteRoleGroupById(Map<String, Object> params) {
        logger.debug("deleteRoleGroupById called, params={}", params);
        return groupMapper.deleteRoleGroupById(params);
    }

    @Override
    public int deleteRoleGroupByName(Map<String, Object> params) {
        logger.debug("deleteRoleGroupByName called, params={}", params);
        return groupMapper.deleteRoleGroupByName(params);
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

    // ---------------- LIST ALL / PAGINATION ----------------

    @Override
    public PagedResult<RoleGroupManagementPojo> findAllRoleGroups() {
        logger.debug("findAllRoleGroups called");

        List<RoleGroupManagementPojo> items = groupMapper.findAllRoleGroups();
        logger.debug("findAllRoleGroups items={}", items);

        long totalCount = items != null ? items.size() : 0;
        logger.debug("findAllRoleGroups totalCount={}", totalCount);

        int page = 1;
        int size = (int) totalCount;

        return new PagedResult<>(items, totalCount, page, size);
    }

    @Override
    public PagedResult<RoleGroupManagementPojo> getAllRoleGroups() {
        logger.debug("getAllRoleGroups called");

        List<RoleGroupManagementPojo> items = groupMapper.getAllRoleGroups();
        logger.debug("getAllRoleGroups items={}", items);

        long totalCount = items != null ? items.size() : 0;
        logger.debug("getAllRoleGroups totalCount={}", totalCount);

        int page = 1;
        int size = (int) totalCount;

        return new PagedResult<>(items, totalCount, page, size);
    }

    // ---------------- SEARCH / PAGINATION ----------------
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

        logger.debug("findRoleGroups results size={}", items.size());
        logger.debug("findRoleGroups totalCount={}", totalCount);

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

        logger.debug("searchRoleGroups results size={}", items.size());
        logger.debug("searchRoleGroups totalCount={}", totalCount);

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
            return groupCache.get(id, k -> findRoleGroupById(Map.of("params", Map.of("roleGroupId", k))));
        } else if (groupName != null) {
            Long cachedId = idToNameCache.get(groupName, k -> {
                RoleGroupManagementPojo group = findRoleGroupByName(Map.of("params", Map.of("groupName", k)));
                return group != null ? group.getId() : null;
            });
            return groupCache.get(cachedId, k -> null);
        }
        return null;
    }
}
