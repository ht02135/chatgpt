package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

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
    private final GenericCache<String, Long> nameToIdCache;
    private final SecurityConfigLoader securityConfigLoader;
    private final Validator validator;

    @Autowired
    public RoleManagementServiceImpl(RoleManagementMapper roleMapper,
                                     @Qualifier("roleCache") GenericCache<Long, RoleManagementPojo> roleCache,
                                     @Qualifier("nameToIdCache") GenericCache<String, Long> nameToIdCache,
                                     SecurityConfigLoader securityConfigLoader) {
        logger.debug("RoleManagementServiceImpl constructor called");
        logger.debug("roleMapper={}", roleMapper);
        logger.debug("roleCache={}", roleCache);
        logger.debug("nameToIdCache={}", nameToIdCache);
        logger.debug("securityConfigLoader={}", securityConfigLoader);

        this.roleMapper = roleMapper;
        this.roleCache = roleCache;
        this.nameToIdCache = nameToIdCache;
        this.securityConfigLoader = securityConfigLoader;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        logger.debug("Validator initialized");
        
        logger.debug("constructor called ##############");
        logger.debug("constructor called");
        logger.debug("constructor called ##############");
    }

    @PostConstruct
    public void postConstruct() {
        logger.debug("postConstruct called ##############");
        logger.debug("postConstruct called");
        logger.debug("postConstruct called ##############");
        initializeDB();
    }

    public void initializeDB() {
        logger.debug("initializeDB called");
        
        if (securityConfigLoader == null || roleCache == null || nameToIdCache == null) {
            logger.debug("initializeDB called ##############");
            logger.error("Missing required beans: securityConfigLoader={}, roleCache={}, nameToIdCache={}", 
                securityConfigLoader, roleCache, nameToIdCache);
            logger.debug("initializeDB called ##############");
            return;
        }

        List<RoleConfig> definedRoles = securityConfigLoader.getRoles();
        logger.debug("Loaded roles from config, size={}", definedRoles.size());

        for (RoleConfig roleConfig : definedRoles) {
            String roleName = roleConfig.getName();
            String description = roleConfig.getDescription();
            logger.debug("Processing roleConfig name={} description={}", roleName, description);

            RoleManagementPojo existing = internalGetRole(ParamWrapper.wrap("roleName", roleName));
            
            if (existing == null) {
            	RoleManagementPojo rolePojo = new RoleManagementPojo();
                rolePojo.setRoleName(roleName);
                rolePojo.setDescription(description);
                
                logger.debug("initializeDB rolePojo={}", rolePojo);
                existing = insertRole(ParamWrapper.wrap("role", rolePojo));
                logger.debug("Inserted role existing.getId()={} roleName={}", existing.getId(), roleName);
            } else {
                logger.debug("Role already exists, skipping id={} roleName={}", existing.getId(), roleName);
            }

            logger.debug("Inserted before roleCache existing={}", existing);
            roleCache.put(existing.getId(), existing);
            
            logger.debug("Inserted before nameToIdCache roleName={}", roleName);
            logger.debug("Inserted before nameToIdCache existing.getId()={}", existing.getId());
            nameToIdCache.put(roleName, existing.getId());
            
            logger.debug("Cached role existing.getId()={} roleName={}", existing.getId(), roleName);
        }

        logger.debug("initializeDB called ##############");
        logger.debug("initializeDB completed");
        logger.debug("initializeDB called ##############");
    }

    // -------------------- PAGED METHODS --------------------
    @Override
    public PagedResult<RoleManagementPojo> findAllRoles() {
        logger.debug("findAllRoles called");

        List<RoleManagementPojo> items = roleMapper.findAllRoles();
        long totalCount = items != null ? items.size() : 0;

        logger.debug("findAllRoles results size={}", totalCount);

        return new PagedResult<>(items, totalCount, 1, (int) totalCount);
    }

    @Override
    public PagedResult<RoleManagementPojo> getAllRoles() {
        logger.debug("getAllRoles called");

        List<RoleManagementPojo> items = roleMapper.getAllRoles();
        long totalCount = items != null ? items.size() : 0;

        logger.debug("getAllRoles results size={}", totalCount);

        return new PagedResult<>(items, totalCount, 1, (int) totalCount);
    }

    @Override
    public PagedResult<RoleManagementPojo> findRoles(Map<String, Object> params) {
        return fetchPaged(params, roleMapper::findRoles, roleMapper::countRoles, "findRoles");
    }

    @Override
    public PagedResult<RoleManagementPojo> searchRoles(Map<String, Object> params) {
        return fetchPaged(params, roleMapper::searchRoles, roleMapper::countRoles, "searchRoles");
    }

    private PagedResult<RoleManagementPojo> fetchPaged(Map<String, Object> params,
                                                       java.util.function.Function<Map<String, Object>, List<RoleManagementPojo>> listFunc,
                                                       java.util.function.Function<Map<String, Object>, Long> countFunc,
                                                       String methodName) {
        logger.debug("{} called, params={}", methodName, params);

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

        List<RoleManagementPojo> items = listFunc.apply(params);
        long totalCount = countFunc.apply(params);

        logger.debug("{} results size={}", methodName, items.size());
        logger.debug("{} totalCount={}", methodName, totalCount);

        return new PagedResult<>(items, totalCount, page, size);
    }

    // -------------------- SINGLE ROLE METHODS --------------------
    @Override
    public RoleManagementPojo findRoleById(Map<String, Object> params) {
        logger.debug("findRoleById called params={}", params);
        Long roleId = ((Number) ParamWrapper.unwrap(params, "roleId")).longValue();
        logger.debug("roleId={}", roleId);
        return internalFindRoleById(roleId);
    }

    @Override
    public RoleManagementPojo findRoleByName(Map<String, Object> params) {
        logger.debug("findRoleByName called params={}", params);
        String roleName = ParamWrapper.unwrap(params, "roleName");
        logger.debug("roleName={}", roleName);
        return internalFindRoleByName(ParamWrapper.wrap("roleName", roleName));
    }

    @Override
    public long countRoles(Map<String, Object> params) {
        logger.debug("countRoles called params={}", params);
        return roleMapper.countRoles(params);
    }

    @Override
    public RoleManagementPojo insertRole(Map<String, Object> params) {
        logger.debug("insertRole called params={}", params);
        RoleManagementPojo role = ParamWrapper.unwrap(params, "role");
        logger.debug("insertRole role={}", role);
        roleMapper.insertRole(ParamWrapper.wrap("role", role));
        roleCache.put(role.getId(), role);
        nameToIdCache.put(role.getRoleName(), role.getId());
        logger.debug("insertRole cached role id={} roleName={}", role.getId(), role.getRoleName());
        return role;
    }

    @Override
    public RoleManagementPojo updateRole(Map<String, Object> params) {
        logger.debug("updateRole called params={}", params);
        RoleManagementPojo role = ParamWrapper.unwrap(params, "role");
        RoleManagementPojo existing = internalGetRole(params);

        if (existing == null) {
            logger.debug("No existing role found, update aborted params={}", params);
            return null;
        }

        role.setId(existing.getId());
        roleMapper.updateRole(ParamWrapper.wrap("role", role));
        roleCache.put(existing.getId(), role);
        nameToIdCache.put(role.getRoleName(), existing.getId());
        logger.debug("updateRole updated cache id={} roleName={}", existing.getId(), role.getRoleName());
        return role;
    }

    @Override
    public void deleteRoleById(Map<String, Object> params) {
        logger.debug("deleteRoleById called params={}", params);
        RoleManagementPojo existing = internalGetRole(params);
        if (existing != null) {
            roleMapper.deleteRoleById(ParamWrapper.wrap("roleId", existing.getId()));
            roleCache.invalidate(existing.getId());
            nameToIdCache.invalidate(existing.getRoleName());
            logger.debug("deleteRoleById deleted role id={} roleName={}", existing.getId(), existing.getRoleName());
        }
    }

    @Override
    public void deleteRoleByName(Map<String, Object> params) {
        logger.debug("deleteRoleByName called params={}", params);
        RoleManagementPojo existing = internalGetRole(params);
        if (existing != null) {
            roleMapper.deleteRoleByName(ParamWrapper.wrap("roleName", existing.getRoleName()));
            roleCache.invalidate(existing.getId());
            nameToIdCache.invalidate(existing.getRoleName());
            logger.debug("deleteRoleByName deleted role id={} roleName={}", existing.getId(), existing.getRoleName());
        }
    }

    // -------------------- Private Helpers --------------------
    private RoleManagementPojo internalFindRoleById(Long id) {
        logger.debug("internalFindRoleByName called id={}", id);
        return roleCache.get(id, k -> {
        	logger.debug("internalFindRoleByName called k={}", k);
            RoleManagementPojo dbRole = roleMapper.findRoleById(ParamWrapper.wrap("roleId", k));
            if (dbRole != null) {
                nameToIdCache.put(dbRole.getRoleName(), dbRole.getId());
                logger.debug("internalFindRoleByName Loaded from DB and cached id={} roleName={}", dbRole.getId(), dbRole.getRoleName());
            }
            return dbRole;
        });
    }

    private RoleManagementPojo internalFindRoleByName(Map<String, Object> params) {
        logger.debug("internalFindRoleByName called params={}", params);

        String roleName = ParamWrapper.unwrap(params, "roleName");
        logger.debug("internalFindRoleByName called roleName={}", roleName);

        Long id = nameToIdCache.get(roleName, k -> {
            logger.debug("internalFindRoleByName called k={}", k);
            RoleManagementPojo dbRole = roleMapper.findRoleByName(ParamWrapper.wrap("roleName", k));
            if (dbRole != null) {
                roleCache.put(dbRole.getId(), dbRole);
                logger.debug("internalFindRoleByName Loaded from DB and cached roleName={} id={}", k, dbRole.getId());
                return dbRole.getId();
            }
            return null;
        });

        return roleCache.get(id, k -> null);
    }

    private RoleManagementPojo internalGetRole(Map<String, Object> params) {
        logger.debug("internalGetRole called, params={}", params);

        Long roleId = ParamWrapper.unwrap(params, "roleId") != null ? ((Number) ParamWrapper.unwrap(params, "roleId")).longValue() : null;
        String roleName = ParamWrapper.unwrap(params, "roleName");

        if (roleId != null) {
            logger.debug("internalGetRole called, roleId={}", roleId);
            return internalFindRoleById(roleId);
        } else if (roleName != null) {
            logger.debug("internalGetRole called, roleName={}", roleName);
            return internalFindRoleByName(ParamWrapper.wrap("roleName", roleName));
        }
        return null;
    }
}
