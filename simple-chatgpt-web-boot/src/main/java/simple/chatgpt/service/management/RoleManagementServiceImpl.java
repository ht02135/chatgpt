package simple.chatgpt.service.management;

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

import simple.chatgpt.config.management.RoleConfig;
import simple.chatgpt.config.management.loader.SecurityConfigLoader;
import simple.chatgpt.mapper.management.security.RoleManagementMapper;
import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.util.GenericCache;
import simple.chatgpt.util.PagedResult;

@Service
public class RoleManagementServiceImpl implements RoleManagementService {

    private static final Logger logger = LogManager.getLogger(RoleManagementServiceImpl.class);

    private final RoleManagementMapper mapper;
    private final GenericCache<Long, RoleManagementPojo> roleCache;
    private final GenericCache<String, Long> idToNameCache;
    private final SecurityConfigLoader securityConfigLoader;
    private final Validator validator;

    @Autowired
    public RoleManagementServiceImpl(RoleManagementMapper mapper,
                                     @Qualifier("roleCache") GenericCache<Long, RoleManagementPojo> roleCache,
                                     @Qualifier("idToNameCache") GenericCache<String, Long> idToNameCache,
                                     SecurityConfigLoader securityConfigLoader) {
        logger.debug("RoleManagementServiceImpl constructor called");
        logger.debug("RoleManagementServiceImpl mapper={}", mapper);
        logger.debug("RoleManagementServiceImpl roleCache={}", roleCache);
        logger.debug("RoleManagementServiceImpl idToNameCache={}", idToNameCache);
        logger.debug("RoleManagementServiceImpl securityConfigLoader={}", securityConfigLoader);

        this.mapper = mapper;
        this.roleCache = roleCache;
        this.idToNameCache = idToNameCache;
        this.securityConfigLoader = securityConfigLoader;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        logger.debug("RoleManagementServiceImpl validator initialized");
    }

    @PostConstruct
    public void postConstruct() {
        initializeDB();
    }

    public void initializeDB() {
        logger.debug("initializeDB called");

        List<RoleConfig> definedRoles = securityConfigLoader.getRoles();
        logger.debug("initializeDB loaded roles size={}", definedRoles.size());

        for (RoleConfig roleConfig : definedRoles) {
            String roleName = roleConfig.getName();
            String description = roleConfig.getDescription();
            logger.debug("Processing roleConfig name={} description={}", roleName, description);

            RoleManagementPojo existing = getRoleByName(roleName);

            RoleManagementPojo rolePojo = new RoleManagementPojo();
            rolePojo.setRoleName(roleName);
            rolePojo.setDescription(description);

            if (existing == null) {
                mapper.insertRole(rolePojo);
                logger.debug("initializeDB inserted role id={} roleName={}", rolePojo.getId(), roleName);
            } else {
            	logger.debug("initializeDB role already exists, skipping id={} roleName={}", existing.getId(), roleName);
            }

            // Cache the role
            roleCache.put(rolePojo.getId(), rolePojo);
            idToNameCache.put(roleName, rolePojo.getId());
            logger.debug("initializeDB cached role id={} roleName={}", rolePojo.getId(), roleName);
        }

        logger.debug("initializeDB completed");
    }

    @Override
    public PagedResult<RoleManagementPojo> searchRoles(Map<String, String> params) {
        logger.debug("searchRoles called");
        logger.debug("searchRoles params={}", params);

        List<RoleManagementPojo> items = mapper.searchRoles(params);
        int totalCount = mapper.countRoles(params);

        logger.debug("searchRoles results size={}", items.size());
        return new PagedResult<>(items, totalCount, 
                                 params.containsKey("page") ? Integer.parseInt(params.get("page")) : 1,
                                 params.containsKey("size") ? Integer.parseInt(params.get("size")) : items.size());
    }

    @Override
    public RoleManagementPojo getRoleById(Long id) {
        logger.debug("getRoleById called");
        logger.debug("getRoleById id={}", id);

        RoleManagementPojo cached = roleCache.get(id, k -> {
            RoleManagementPojo dbRole = mapper.findRoleById(k);
            if (dbRole != null) {
                idToNameCache.put(dbRole.getRoleName(), dbRole.getId());
                logger.debug("getRoleById loaded from DB and cached id={} roleName={}", dbRole.getId(), dbRole.getRoleName());
            }
            return dbRole;
        });

        if (cached != null) {
            logger.debug("getRoleById returning role from cache id={}", id);
        }
        return cached;
    }

    public RoleManagementPojo getRoleByName(String roleName) {
        logger.debug("getRoleByName called");
        logger.debug("getRoleByName roleName={}", roleName);

        Long id = idToNameCache.get(roleName, k -> {
            RoleManagementPojo dbRole = mapper.findRoleByName(k);
            if (dbRole != null) {
                roleCache.put(dbRole.getId(), dbRole);
                logger.debug("getRoleByName loaded from DB and cached roleName={} id={}", k, dbRole.getId());
                return dbRole.getId();
            }
            return null;
        });

        RoleManagementPojo cached = roleCache.get(id, k -> null);
        if (cached != null) {
            logger.debug("getRoleByName returning role from roleCache id={}", id);
            return cached;
        }

        return null;
    }

    @Override
    public RoleManagementPojo createRole(RoleManagementPojo role) {
        logger.debug("createRole called");
        logger.debug("createRole role={}", role);

        mapper.insertRole(role);
        logger.debug("createRole inserted role id={}", role.getId());

        roleCache.put(role.getId(), role);
        idToNameCache.put(role.getRoleName(), role.getId());
        logger.debug("createRole cached role id={} roleName={}", role.getId(), role.getRoleName());

        return role;
    }

    @Override
    public RoleManagementPojo updateRoleById(Long id, RoleManagementPojo role) {
        logger.debug("updateRoleById called");
        logger.debug("updateRoleById id={}", id);
        logger.debug("updateRoleById role={}", role);

        RoleManagementPojo existing = getRoleById(id);
        if (existing == null) {
            logger.debug("updateRoleById no existing record id={}", id);
            return null;
        }

        role.setId(id);
        mapper.updateRole(role);
        logger.debug("updateRoleById updated DB id={}", id);

        roleCache.put(id, role);
        idToNameCache.put(role.getRoleName(), id);
        logger.debug("updateRoleById updated cache id={} roleName={}", id, role.getRoleName());

        return role;
    }

    @Override
    public void deleteRoleById(Long id) {
        logger.debug("deleteRoleById called");
        logger.debug("deleteRoleById id={}", id);

        RoleManagementPojo existing = getRoleById(id);
        if (existing != null) {
            idToNameCache.invalidate(existing.getRoleName());
            roleCache.invalidate(id);
            logger.debug("deleteRoleById removed from cache id={} roleName={}", id, existing.getRoleName());
        }

        mapper.deleteRoleById(id);
        logger.debug("deleteRoleById deleted from DB id={}", id);
    }

    @Override
    public RoleManagementPojo getByRoleName(String roleName) {
        return getRoleByName(roleName);
    }

    @Override
    public RoleManagementPojo updateRoleByRoleName(String roleName, RoleManagementPojo role) {
        RoleManagementPojo existing = getRoleByName(roleName);
        if (existing == null) {
            logger.debug("updateRoleByRoleName no existing role roleName={}", roleName);
            return null;
        }
        return updateRoleById(existing.getId(), role);
    }

    @Override
    public void deleteRoleByRoleName(String roleName) {
        RoleManagementPojo existing = getRoleByName(roleName);
        if (existing != null) {
            idToNameCache.invalidate(roleName);
            roleCache.invalidate(existing.getId());
            mapper.deleteRoleByName(roleName);
            logger.debug("deleteRoleByRoleName deleted roleName={} id={}", roleName, existing.getId());
        }
    }
}
