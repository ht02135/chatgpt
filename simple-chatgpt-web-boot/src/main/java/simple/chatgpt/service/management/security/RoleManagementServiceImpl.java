package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import simple.chatgpt.config.management.loader.SecurityConfigLoader;
import simple.chatgpt.config.management.security.RoleConfig;
import simple.chatgpt.mapper.management.security.RoleManagementMapper;
import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.util.GenericCache;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.SafeConverter;

@Service
public class RoleManagementServiceImpl implements RoleManagementService {

    private static final Logger logger = LogManager.getLogger(RoleManagementServiceImpl.class);

    private final RoleManagementMapper roleMapper;
    private final GenericCache<Long, RoleManagementPojo> roleCache;
    private final SecurityConfigLoader securityConfigLoader;
    private final Validator validator;

    @Autowired
    public RoleManagementServiceImpl(
            RoleManagementMapper roleMapper,
            @Qualifier("roleCache") GenericCache<Long, RoleManagementPojo> roleCache,
            SecurityConfigLoader securityConfigLoader) {

        logger.debug("RoleManagementServiceImpl constructor START");
        logger.debug("roleMapper={}", roleMapper);
        logger.debug("roleCache={}", roleCache);
        logger.debug("securityConfigLoader={}", securityConfigLoader);

        this.roleMapper = roleMapper;
        this.roleCache = roleCache;
        this.securityConfigLoader = securityConfigLoader;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();

        logger.debug("RoleManagementServiceImpl constructor DONE");
    }

    @PostConstruct
    public void postConstruct() {
        logger.debug("postConstruct START");
        initializeDB();
        logger.debug("postConstruct DONE");
    }

    public void initializeDB() {
        logger.debug("initializeDB START");

        if (securityConfigLoader == null || roleCache == null) {
            logger.debug("initializeDB DONE");
            return;
        }

        List<RoleConfig> definedRoles = securityConfigLoader.getRoles();
        List<RoleManagementPojo> existingRoles = findAllRoles().getItems();

        Map<String, RoleManagementPojo> roleByName = existingRoles
                .stream()
                .collect(Collectors.toMap(RoleManagementPojo::getRoleName, r -> r));

        for (RoleConfig roleConfig : definedRoles) {
            String roleName = roleConfig.getName();
            String description = roleConfig.getDescription();

            RoleManagementPojo existing = roleByName.get(roleName);

            if (existing == null) {
                RoleManagementPojo rolePojo = new RoleManagementPojo();
                rolePojo.setRoleName(roleName);
                rolePojo.setDescription(description);

                RoleManagementPojo inserted = insertRole(Map.of("role", rolePojo));
                roleByName.put(roleName, inserted);
            }
        }

        logger.debug("initializeDB DONE");
    }

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @Override
    public RoleManagementPojo create(RoleManagementPojo role) {
        logger.debug("create called");
        logger.debug("create role={}", role);
        roleMapper.create(role);
        return role;
    }

    @Override
    public RoleManagementPojo update(Long id, RoleManagementPojo role) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update role={}", role);
        roleMapper.update(id, role);
        return role;
    }

    @Override
    public PagedResult<RoleManagementPojo> search(Map<String, String> params) {
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

        List<RoleManagementPojo> items = roleMapper.search((Map) params);
        long totalCount = items.size();
        PagedResult<RoleManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("search return={}", result);
        return result;
    }

    @Override
    public RoleManagementPojo get(Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);
        RoleManagementPojo role = roleMapper.get(id);
        logger.debug("get return={}", role);
        return role;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);
        roleMapper.delete(id);
    }

    // ======= OTHER METHODS =======
    
    @Override
    public PagedResult<RoleManagementPojo> findAllRoles() {
        logger.debug("findAllRoles START");

        List<RoleManagementPojo> items = roleMapper.findAllRoles();
        long totalCount = items != null ? items.size() : 0;

        PagedResult<RoleManagementPojo> result = new PagedResult<>(items, totalCount, 1, (int) totalCount);
        logger.debug("findAllRoles DONE return={}", result);
        return result;
    }

    @Override
    public PagedResult<RoleManagementPojo> getAllRoles() {
        logger.debug("getAllRoles START");

        List<RoleManagementPojo> items = roleMapper.getAllRoles();
        long totalCount = items != null ? items.size() : 0;

        PagedResult<RoleManagementPojo> result = new PagedResult<>(items, totalCount, 1, (int) totalCount);
        logger.debug("getAllRoles DONE return={}", result);
        return result;
    }

    @Override
    public PagedResult<RoleManagementPojo> findRoles(Map<String, Object> params) {
        logger.debug("findRoles START");
        logger.debug("findRoles params={}", params);

        PagedResult<RoleManagementPojo> result = fetchPaged(params, roleMapper::findRoles, roleMapper::countRoles, "findRoles");
        logger.debug("findRoles DONE return={}", result);
        return result;
    }

    @Override
    public PagedResult<RoleManagementPojo> searchRoles(Map<String, Object> params) {
        logger.debug("searchRoles START");
        logger.debug("searchRoles params={}", params);

        PagedResult<RoleManagementPojo> result = fetchPaged(params, roleMapper::searchRoles, roleMapper::countRoles, "searchRoles");
        logger.debug("searchRoles DONE return={}", result);
        return result;
    }

    private PagedResult<RoleManagementPojo> fetchPaged(Map<String, Object> params,
                                                       java.util.function.Function<Map<String, Object>, List<RoleManagementPojo>> listFunc,
                                                       java.util.function.Function<Map<String, Object>, Long> countFunc,
                                                       String methodName) {
        logger.debug(methodName + " START");
        logger.debug(methodName + " params={}", params);

        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<RoleManagementPojo> items = listFunc.apply(params);
        long totalCount = countFunc.apply(params);

        PagedResult<RoleManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug(methodName + " DONE return={}", result);
        return result;
    }

    @Override
    public RoleManagementPojo findRoleById(Map<String, Object> params) {
        logger.debug("findRoleById START");
        logger.debug("findRoleById params={}", params);

        Long roleId = ((Number) ParamWrapper.unwrap(params, "roleId")).longValue();
        RoleManagementPojo result = internalFindRoleById(roleId);

        logger.debug("findRoleById DONE return={}", result);
        return result;
    }

    @Override
    public long countRoles(Map<String, Object> params) {
        logger.debug("countRoles START");
        logger.debug("countRoles params={}", params);

        long count = roleMapper.countRoles(params);
        logger.debug("countRoles DONE return={}", count);
        return count;
    }

    @Override
    public RoleManagementPojo insertRole(Map<String, Object> params) {
        logger.debug("insertRole START");
        logger.debug("insertRole params={}", params);

        RoleManagementPojo role = ParamWrapper.unwrap(params, "role");

        roleMapper.insertRole(params);  // pass raw params directly

        RoleManagementPojo fullRole = internalGetRole(Map.of("roleName", role.getRoleName()));
        logger.debug("insertRole DONE return={}", fullRole);
        return fullRole;
    }

    @Override
    public RoleManagementPojo updateRole(Map<String, Object> params) {
        logger.debug("updateRole START");
        logger.debug("updateRole params={}", params);

        RoleManagementPojo role = ParamWrapper.unwrap(params, "role");
        RoleManagementPojo existing = internalGetRole(params);

        if (existing == null) {
            logger.debug("updateRole DONE return=null");
            return null;
        }

        role.setId(existing.getId());
        roleMapper.updateRole(params);  // raw params

        logger.debug("updateRole DONE return={}", role);
        return role;
    }

    @Override
    public void deleteRoleById(Map<String, Object> params) {
        logger.debug("deleteRoleById START");
        logger.debug("deleteRoleById params={}", params);

        RoleManagementPojo existing = internalGetRole(params);
        if (existing != null) {
            roleMapper.deleteRoleById(params);  // raw params
            roleCache.invalidate(existing.getId());
        }

        logger.debug("deleteRoleById DONE");
    }

    private RoleManagementPojo internalFindRoleById(Long id) {
        logger.debug("internalFindRoleById START");
        logger.debug("internalFindRoleById id={}", id);

        RoleManagementPojo result = roleCache.get(id, k -> roleMapper.findRoleById(Map.of("roleId", k)));

        logger.debug("internalFindRoleById DONE return={}", result);
        return result;
    }

    private RoleManagementPojo internalGetRole(Map<String, Object> params) {
        logger.debug("internalGetRole START");
        logger.debug("internalGetRole params={}", params);

        Long roleId = ((Number) ParamWrapper.unwrap(params, "roleId")).longValue();

        RoleManagementPojo role = null;
        if (roleId != null) {
            role = internalFindRoleById(roleId);
        }

        logger.debug("internalGetRole DONE return={}", role);
        return role;
    }
}
