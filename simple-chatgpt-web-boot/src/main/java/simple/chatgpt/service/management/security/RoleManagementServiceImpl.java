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

            RoleManagementPojo existing = getRole(Map.of("roleName", roleName));

            RoleManagementPojo rolePojo = new RoleManagementPojo();
            rolePojo.setRoleName(roleName);
            rolePojo.setDescription(description);

            if (existing == null) {
                mapper.insertRole(Map.of("params", Map.of("role", rolePojo)));
                logger.debug("initializeDB inserted role id={} roleName={}", rolePojo.getId(), roleName);
            } else {
                logger.debug("initializeDB role already exists, skipping id={} roleName={}", existing.getId(), roleName);
            }

            roleCache.put(rolePojo.getId(), rolePojo);
            idToNameCache.put(roleName, rolePojo.getId());
            logger.debug("initializeDB cached role id={} roleName={}", rolePojo.getId(), roleName);
        }

        logger.debug("initializeDB completed");
    }

    @Override
    public PagedResult<RoleManagementPojo> searchRoles(Map<String, Object> params) {
        logger.debug("searchRoles called");
        logger.debug("searchRoles params={}", params);

        int page = params.get("page") != null ? (int) params.get("page") : 1;
        int size = params.get("size") != null ? (int) params.get("size") : 20;
        int offset = (page - 1) * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<RoleManagementPojo> items = mapper.searchRoles(Map.of("params", params));
        long totalCount = mapper.countRoles(Map.of("params", params));

        logger.debug("searchRoles results size={}", items.size());
        logger.debug("searchRoles totalCount={}", totalCount);

        return new PagedResult<>(items, totalCount, page, size);
    }

    @Override
    public RoleManagementPojo getRole(Map<String, Object> params) {
        logger.debug("getRole called");
        logger.debug("getRole params={}", params);

        Long roleId = (Long) params.get("roleId");
        String roleName = (String) params.get("roleName");

        if (roleId != null) {
            return getRoleById(roleId);
        } else if (roleName != null) {
            return getRoleByName(Map.of("roleName", roleName));
        }
        return null;
    }

    private RoleManagementPojo getRoleById(Long id) {
        logger.debug("getRoleById called id={}", id);

        return roleCache.get(id, k -> {
            RoleManagementPojo dbRole = mapper.findRoleById(Map.of("params", Map.of("roleId", k)));
            if (dbRole != null) {
                idToNameCache.put(dbRole.getRoleName(), dbRole.getId());
                logger.debug("getRoleById loaded from DB and cached id={} roleName={}", dbRole.getId(), dbRole.getRoleName());
            }
            return dbRole;
        });
    }

    private RoleManagementPojo getRoleByName(Map<String, Object> params) {
        String roleName = (String) params.get("roleName");
        logger.debug("getRoleByName called roleName={}", roleName);

        Long id = idToNameCache.get(roleName, k -> {
            RoleManagementPojo dbRole = mapper.findRoleByName(Map.of("params", Map.of("roleName", k)));
            if (dbRole != null) {
                roleCache.put(dbRole.getId(), dbRole);
                logger.debug("getRoleByName loaded from DB and cached roleName={} id={}", k, dbRole.getId());
                return dbRole.getId();
            }
            return null;
        });

        return roleCache.get(id, k -> null);
    }

    @Override
    public RoleManagementPojo createRole(Map<String, Object> params) {
        logger.debug("createRole called params={}", params);

        RoleManagementPojo role = (RoleManagementPojo) params.get("role");

        mapper.insertRole(Map.of("params", Map.of("role", role)));
        logger.debug("createRole inserted role id={}", role.getId());

        roleCache.put(role.getId(), role);
        idToNameCache.put(role.getRoleName(), role.getId());
        logger.debug("createRole cached role id={} roleName={}", role.getId(), role.getRoleName());

        return role;
    }

    @Override
    public RoleManagementPojo updateRole(Map<String, Object> params) {
        logger.debug("updateRole called params={}", params);

        RoleManagementPojo role = (RoleManagementPojo) params.get("role");

        RoleManagementPojo existing = getRole(params);
        if (existing == null) {
            logger.debug("updateRole no existing record params={}", params);
            return null;
        }

        role.setId(existing.getId());
        mapper.updateRole(Map.of("params", Map.of("role", role)));
        logger.debug("updateRole updated DB id={}", existing.getId());

        roleCache.put(existing.getId(), role);
        idToNameCache.put(role.getRoleName(), existing.getId());
        logger.debug("updateRole updated cache id={} roleName={}", existing.getId(), role.getRoleName());

        return role;
    }

    @Override
    public void deleteRole(Map<String, Object> params) {
        logger.debug("deleteRole called params={}", params);

        RoleManagementPojo existing = getRole(params);
        if (existing != null) {
            idToNameCache.invalidate(existing.getRoleName());
            roleCache.invalidate(existing.getId());

            if (params.containsKey("roleId")) {
                mapper.deleteRoleById(Map.of("params", Map.of("roleId", existing.getId())));
            } else if (params.containsKey("roleName")) {
                mapper.deleteRoleByName(Map.of("params", Map.of("roleName", existing.getRoleName())));
            }

            logger.debug("deleteRole deleted role id={} roleName={}", existing.getId(), existing.getRoleName());
        }
    }
}
