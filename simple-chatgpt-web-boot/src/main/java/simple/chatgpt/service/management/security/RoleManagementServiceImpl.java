package simple.chatgpt.service.management.security;

import java.util.HashMap;
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
        logger.debug("initializeDB called");

        try {
            // load roles from XML config
            List<RoleConfig> xmlRoles = securityConfigLoader.getRoles();
            logger.debug("initializeDB xmlRoles={}", xmlRoles);

            if (xmlRoles == null || xmlRoles.isEmpty()) {
                logger.debug("initializeDB no roles found in XML, skipping");
                return;
            }

            // get existing roles from DB
            List<RoleManagementPojo> existingRoles = getAll();
            logger.debug("initializeDB existingRoles={}", existingRoles);

            // map existing role names for quick lookup
            Map<String, RoleManagementPojo> existingRoleMap = existingRoles.stream()
                    .collect(Collectors.toMap(RoleManagementPojo::getRoleName, r -> r, (a, b) -> a));
            logger.debug("initializeDB existingRoleMap keys={}", existingRoleMap.keySet());

            // iterate xmlRoles and insert if missing
            for (RoleConfig rc : xmlRoles) {
                logger.debug("initializeDB processing rc={}", rc);

                String name = rc.getName();
                String desc = rc.getDescription();

                if (!existingRoleMap.containsKey(name)) {
                    logger.debug("initializeDB inserting new role name={}", name);
                    logger.debug("initializeDB inserting new role desc={}", desc);

                    RoleManagementPojo newRole = new RoleManagementPojo();
                    newRole.setRoleName(name);
                    newRole.setDescription(desc);
                    create(newRole);

                    // cache new role
                    roleCache.get(newRole.getId(), k -> newRole);
                } else {
                    logger.debug("initializeDB role already exists name={}", name);
                }
            }

            logger.debug("initializeDB DONE");
        } catch (Exception e) {
            logger.error("initializeDB failed", e);
        }
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
        // Hung: invalidate cache after update
        roleCache.invalidate(id);
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

        // Hung : mapper expect Map<String, Object> for offset and limit
    	Map<String, Object> mapperParams = new HashMap<>(params);
        mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
        mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));
        
        List<RoleManagementPojo> items = roleMapper.search(mapperParams);
        long totalCount = items.size();
        
        // === Populate cache by ID ===
        for (RoleManagementPojo role : items) {
            roleCache.get(role.getId(), k -> {
                logger.debug("search: caching role id={}", k);
                return role;
            });
        }
        
        PagedResult<RoleManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("search return={}", result);
        return result;
    }

    @Override
    public RoleManagementPojo get(Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);

        // Hung: use roleCache to fetch by id; Caffeine will load from mapper if absent
        RoleManagementPojo role = roleCache.get(id, k -> {
            RoleManagementPojo loaded = roleMapper.get(k);
            return loaded;
        });
        return role;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);

        // Hung: invalidate cache first to ensure consistency
        roleCache.invalidate(id);
        roleMapper.delete(id);
    }

    // ======= OTHER METHODS =======
    
    @Override
	public List<RoleManagementPojo> getRoleByParams(Map<String, Object> params)
	{
        logger.debug("getRoleByParams called");

        List<RoleManagementPojo> roles = roleMapper.search(params);
        
        // populate cache automatically using get()
        roles.forEach(role -> roleCache.get(role.getId(), k -> role));
        
        return roles;
	}
	
	@Override
	public List<RoleManagementPojo> getAll() {
        logger.debug("getAll called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        List<RoleManagementPojo> roles = getRoleByParams(params);
        
        // populate cache automatically using get()
        roles.forEach(role -> roleCache.get(role.getId(), k -> role));
        
        return roles;
	}
	
	// #{params.roleName}
	@Override
	public RoleManagementPojo getRoleByRoleName(String roleName) {
        logger.debug("getRoleByRoleName called");
        logger.debug("getRoleByRoleName groupName={}", roleName);

        // Query database by groupName
        Map<String, Object> params = new HashMap<>();
        params.put("roleName", roleName);

        List<RoleManagementPojo> roles = getRoleByParams(params);

        if (roles == null || roles.isEmpty()) {
            return null;
        }

        // Take the first matching role group
        RoleManagementPojo role = roles.get(0);

        /*
        hung : dont remove
        normally we wan to run cache first, but the cache is id->pojo
        and this method is groupName, so no cache. but since we get
        the pojo, we cache it by id anyway...
        */
        // Cache it using ID as key
        roleCache.get(role.getId(), k -> {
            return role;
        });

        logger.debug("getRoleGroupByGroupName returning role={}", role);
        return role;
	}

}
