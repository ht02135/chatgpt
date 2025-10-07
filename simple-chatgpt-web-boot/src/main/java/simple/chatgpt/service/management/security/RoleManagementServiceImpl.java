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
    public RoleManagementServiceImpl(RoleManagementMapper roleMapper,
                                     @Qualifier("roleCache") GenericCache<Long, RoleManagementPojo> roleCache,
                                     SecurityConfigLoader securityConfigLoader) {
        logger.debug("RoleManagementServiceImpl constructor called");
        logger.debug("roleMapper={}", roleMapper);
        logger.debug("roleCache={}", roleCache);
        logger.debug("securityConfigLoader={}", securityConfigLoader);

        this.roleMapper = roleMapper;
        this.roleCache = roleCache;
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

        if (securityConfigLoader == null || roleCache == null) {
            logger.debug("initializeDB called ##############");
            logger.error("Missing required beans: securityConfigLoader={}, roleCache={}", 
                securityConfigLoader, roleCache);
            logger.debug("initializeDB called ##############");
            return;
        }

        // ----------- LOAD DEFINED ROLES FROM CONFIG -----------
        List<RoleConfig> definedRoles = securityConfigLoader.getRoles();
        logger.debug("Loaded roles from config size={}", definedRoles.size());

        // ----------- FETCH ALL EXISTING ROLES ONCE -----------
        PagedResult<RoleManagementPojo> rolePagedResult = findAllRoles();
        logger.debug("Fetched all roles pagedResult={}", rolePagedResult);

        List<RoleManagementPojo> existingRoles = rolePagedResult.getItems();
        logger.debug("Fetched all existing roles size={}", existingRoles.size());

        Map<String, RoleManagementPojo> roleByName = existingRoles
                .stream()
                .collect(Collectors.toMap(RoleManagementPojo::getRoleName, r -> r));
        logger.debug("Mapped existing roles by roleName size={}", roleByName.size());

        // ----------- PROCESS EACH CONFIG ROLE -----------
        for (RoleConfig roleConfig : definedRoles) {
            String roleName = roleConfig.getName();
            String description = roleConfig.getDescription();

            logger.debug("Processing roleConfig roleName={}", roleName);
            logger.debug("Processing roleConfig description={}", description);

            RoleManagementPojo existing = roleByName.get(roleName);
            logger.debug("initializeDB existing={}", existing);

            if (existing == null) {
                logger.debug("initializeDB existing == null roleName={}", roleName);

                RoleManagementPojo rolePojo = new RoleManagementPojo();
                rolePojo.setRoleName(roleName);
                rolePojo.setDescription(description);

                logger.debug("initializeDB rolePojo={}", rolePojo);

                RoleManagementPojo inserted = insertRole(ParamWrapper.wrap("role", rolePojo));
                logger.debug("Inserted new role inserted={}", inserted);
                logger.debug("Inserted new role inserted.id={}", inserted.getId());
                logger.debug("Inserted new role inserted.roleName={}", inserted.getRoleName());

                // update map so next iterations can use it
                roleByName.put(roleName, inserted);
            } else {
                logger.debug("initializeDB existing already present id={}", existing.getId());
                logger.debug("initializeDB existing roleName={}", existing.getRoleName());
            }
        }

        logger.debug("initializeDB completed");
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
    public long countRoles(Map<String, Object> params) {
        logger.debug("countRoles called params={}", params);
        return roleMapper.countRoles(params);
    }

    @Override
    public RoleManagementPojo insertRole(Map<String, Object> params) {
        logger.debug("insertRole called params={}", params);

        // Extract role object from params
        RoleManagementPojo role = ParamWrapper.unwrap(params, "role");

        // Perform insert
        logger.debug("insertRole called ##############");
        logger.debug("insertRole role before insert={}", role);
        logger.debug("insertRole called ##############");
        roleMapper.insertRole(ParamWrapper.wrap("role", role));
        logger.debug("insertRole insert completed, role id={}", role.getId());

        // Re-fetch the role from DB to get all populated fields
        RoleManagementPojo fullRole = internalGetRole(ParamWrapper.wrap("roleName", role.getRoleName()));
        logger.debug("insertRole called ##############");
        logger.debug("insertRole re-fetched fullRole={}", fullRole);
        logger.debug("insertRole called ##############");

        logger.debug("insertRole called ##############");
        logger.debug("insertRole fullRole={}",fullRole);
        logger.debug("insertRole called ##############");
        return fullRole;
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
            logger.debug("deleteRoleById deleted role id={} roleName={}", existing.getId(), existing.getRoleName());
        }
    }

    // -------------------- Private Helpers --------------------
    private RoleManagementPojo internalFindRoleById(Long id) {
        logger.debug("internalFindRoleById called id={}", id);
        return roleCache.get(id, k -> {
        	logger.debug("internalFindRoleById roleCache.get called k={}", k);
            RoleManagementPojo dbRole = roleMapper.findRoleById(ParamWrapper.wrap("roleId", k));
            if (dbRole != null) {
            	logger.debug("internalFindRoleById roleCache.get ##############");
                logger.debug("internalFindRoleById roleCache.get id=k={} LOAD from DB", k);
                logger.debug("internalFindRoleById roleCache.get dbRole != null dbRole={}", dbRole);
                logger.debug("internalFindRoleById roleCache.get ##############");
            } else {
            	logger.debug("internalFindRoleById roleCache.get ##############");
            	logger.debug("internalFindRoleById roleCache.get id=k={} NOT LOAD from DB", k);
                logger.debug("internalFindRoleById roleCache.get dbRole is NULL !!!!");
                logger.debug("internalFindRoleById roleCache.get ##############");
            }
            return dbRole;
        });
    }

    private RoleManagementPojo internalGetRole(Map<String, Object> params) {
        logger.debug("internalGetRole called, params={}", params);

        Long roleId = ParamWrapper.unwrap(params, "roleId") != null ? ((Number) ParamWrapper.unwrap(params, "roleId")).longValue() : null;
        RoleManagementPojo role = null;
        if (roleId != null) {
            logger.debug("internalGetRole called, roleId={}", roleId);
            role = internalFindRoleById(roleId);
        }
        
        logger.debug("internalGetRole called ##############");
        logger.debug("internalGetRole role={}", role);
        logger.debug("internalGetRole called ##############");
        return role;
    }
}