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
    private final Validator validator;
    private final SecurityConfigLoader securityConfigLoader;

    @Autowired
    public RoleManagementServiceImpl(RoleManagementMapper mapper,
                                     @Qualifier("roleCache") GenericCache<Long, RoleManagementPojo> roleCache,
                                     @Qualifier("idToNameCache") GenericCache<String, Long> idToNameCache,
                                     SecurityConfigLoader securityConfigLoader) {
        logger.debug("RoleManagementServiceImpl constructor called");

        this.mapper = mapper;
        this.roleCache = roleCache;
        this.idToNameCache = idToNameCache;
        this.securityConfigLoader = securityConfigLoader;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        logger.debug("RoleManagementServiceImpl validator initialized");
    }

    // ------------------ INITIALIZE DB ------------------
    @PostConstruct
    private void initializeDB() {
        logger.debug("initializeDB called");
        if (securityConfigLoader == null) {
            logger.warn("SecurityConfigLoader is null, skipping DB initialization");
            return;
        }

        securityConfigLoader.getRoles().forEach(roleConfig -> {
            RoleManagementPojo existing = mapper.findRoleByName(roleConfig.getName());
            if (existing == null) {
                RoleManagementPojo role = new RoleManagementPojo();
                role.setRoleName(roleConfig.getName());
                role.setDescription(roleConfig.getDescription());
                mapper.insertRole(role);
                logger.debug("Inserted role from SecurityConfigLoader: {}", role);

                // cache immediately
                roleCache.put(role.getId(), role);
                idToNameCache.put(role.getRoleName(), role.getId());
            } else {
                logger.debug("Role already exists in DB: {}", existing);
            }
        });
    }

    // ------------------ SEARCH / LIST ------------------
    @Override
    public PagedResult<RoleManagementPojo> searchRoles(Map<String, String> params) {
        logger.debug("searchRoles called with params={}", params);

        int page = 0;
        int size = 20;
        try {
            page = Integer.parseInt(params.getOrDefault("page", "0"));
        } catch (NumberFormatException e) {
            logger.warn("Invalid page parameter: {}, defaulting to 0", params.get("page"));
        }
        try {
            size = Integer.parseInt(params.getOrDefault("size", "20"));
        } catch (NumberFormatException e) {
            logger.warn("Invalid size parameter: {}, defaulting to 20", params.get("size"));
        }
        int offset = page * size;
        params.put("offset", String.valueOf(offset));
        params.put("limit", String.valueOf(size));

        List<RoleManagementPojo> items = mapper.searchRoles(params);
        int totalCount = mapper.countRoles(params);

        logger.debug("searchRoles fetched items={}, totalCount={}", items.size(), totalCount);

        return new PagedResult<>(items, totalCount, page, size);
    }

    // ------------------ GET ROLE ------------------
    @Override
    public RoleManagementPojo getRoleById(Long id) {
        logger.debug("getRoleById called id={}", id);

        RoleManagementPojo cached = roleCache.get(id);
        if (cached != null) {
            logger.debug("getRoleById found in cache id={}", id);
            return cached;
        }

        RoleManagementPojo dbRole = mapper.findRoleById(id);
        logger.debug("getRoleById fetched from DB role={}", dbRole);

        if (dbRole != null) {
            roleCache.put(id, dbRole);
            idToNameCache.put(dbRole.getRoleName(), id);
        }

        return dbRole;
    }

    @Override
    public RoleManagementPojo getByRoleName(String roleName) {
        logger.debug("getByRoleName called roleName={}", roleName);

        Long id = idToNameCache.get(roleName);
        if (id != null) {
            RoleManagementPojo cached = roleCache.get(id);
            if (cached != null) {
                logger.debug("getByRoleName found in cache id={}", id);
                return cached;
            }
        }

        RoleManagementPojo dbRole = mapper.findRoleByName(roleName);
        logger.debug("getByRoleName fetched from DB role={}", dbRole);

        if (dbRole != null) {
            roleCache.put(dbRole.getId(), dbRole);
            idToNameCache.put(roleName, dbRole.getId());
        }

        return dbRole;
    }

    // ------------------ CREATE / UPDATE / DELETE ------------------
    @Override
    public RoleManagementPojo createRole(RoleManagementPojo role) {
        logger.debug("createRole called role={}", role);

        mapper.insertRole(role);
        roleCache.put(role.getId(), role);
        idToNameCache.put(role.getRoleName(), role.getId());
        logger.debug("createRole cached role id={} roleName={}", role.getId(), role.getRoleName());

        return role;
    }

    @Override
    public RoleManagementPojo updateRoleById(Long id, RoleManagementPojo role) {
        logger.debug("updateRoleById called id={} role={}", id, role);

        RoleManagementPojo existing = getRoleById(id);
        if (existing == null) {
            logger.debug("updateRoleById no existing role id={}", id);
            return null;
        }

        role.setId(id);
        mapper.updateRole(role);
        roleCache.put(id, role);
        idToNameCache.put(role.getRoleName(), id);

        return role;
    }

    @Override
    public RoleManagementPojo updateRoleByRoleName(String roleName, RoleManagementPojo role) {
        logger.debug("updateRoleByRoleName called roleName={} role={}", roleName, role);

        RoleManagementPojo existing = getByRoleName(roleName);
        if (existing == null) {
            logger.debug("updateRoleByRoleName no existing role roleName={}", roleName);
            return null;
        }

        role.setId(existing.getId());
        mapper.updateRole(role);
        roleCache.put(existing.getId(), role);
        idToNameCache.put(role.getRoleName(), existing.getId());

        return role;
    }

    @Override
    public void deleteRoleById(Long id) {
        logger.debug("deleteRoleById called id={}", id);

        RoleManagementPojo existing = getRoleById(id);
        if (existing != null) {
            roleCache.remove(id);
            idToNameCache.remove(existing.getRoleName());
        }

        mapper.deleteRoleById(id);
        logger.debug("deleteRoleById completed id={}", id);
    }

    @Override
    public void deleteRoleByRoleName(String roleName) {
        logger.debug("deleteRoleByRoleName called roleName={}", roleName);

        RoleManagementPojo existing = getByRoleName(roleName);
        if (existing != null) {
            roleCache.remove(existing.getId());
            idToNameCache.remove(roleName);
            mapper.deleteRoleByName(roleName);
        }
    }
}
