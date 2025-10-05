package simple.chatgpt.service.management;

import java.util.List;
import java.util.Map;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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

    @Autowired
    public RoleManagementServiceImpl(RoleManagementMapper mapper,
                                     @Qualifier("roleCache") GenericCache<Long, RoleManagementPojo> roleCache,
                                     @Qualifier("idToNameCache") GenericCache<String, Long> idToNameCache) {
        logger.debug("RoleManagementServiceImpl constructor called");
        logger.debug("RoleManagementServiceImpl mapper={}", mapper);
        logger.debug("RoleManagementServiceImpl roleCache={}", roleCache);
        logger.debug("RoleManagementServiceImpl idToNameCache={}", idToNameCache);

        this.mapper = mapper;
        this.roleCache = roleCache;
        this.idToNameCache = idToNameCache;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();

        logger.debug("RoleManagementServiceImpl validator initialized");
    }

    @Override
    public PagedResult<RoleManagementPojo> searchRoles(Map<String, String> params) {
        logger.debug("searchRoles called");
        logger.debug("searchRoles params={}", params);

        List<RoleManagementPojo> results = mapper.searchRoles(params);
        int total = mapper.countRoles(params);

        logger.debug("searchRoles results size={}", results.size());
        return new PagedResult<>(results, total);
    }

    @Override
    public RoleManagementPojo getRoleById(Long id) {
        logger.debug("getRoleById called");
        logger.debug("getRoleById id={}", id);

        // 1️⃣ Check cache
        RoleManagementPojo cached = roleCache.get(id);
        if (cached != null) {
            logger.debug("getRoleById found in cache id={}", id);
            return cached;
        }

        // 2️⃣ Fetch from DB
        RoleManagementPojo dbRole = mapper.getRoleById(id);
        logger.debug("getRoleById fetched from DB role={}", dbRole);

        if (dbRole != null) {
            roleCache.put(id, dbRole);
            idToNameCache.put(dbRole.getRoleName(), id);
            logger.debug("getRoleById cached id={} roleName={}", id, dbRole.getRoleName());
        }

        return dbRole;
    }

    public RoleManagementPojo getRoleByName(String roleName) {
        logger.debug("getRoleByName called");
        logger.debug("getRoleByName roleName={}", roleName);

        Long id = idToNameCache.get(roleName);
        if (id != null) {
            logger.debug("getRoleByName found id in idToNameCache roleName={} id={}", roleName, id);
            RoleManagementPojo cached = roleCache.get(id);
            if (cached != null) {
                logger.debug("getRoleByName found role in roleCache id={}", id);
                return cached;
            }
        }

        RoleManagementPojo dbRole = mapper.getRoleByName(roleName);
        logger.debug("getRoleByName fetched from DB role={}", dbRole);

        if (dbRole != null) {
            roleCache.put(dbRole.getId(), dbRole);
            idToNameCache.put(roleName, dbRole.getId());
            logger.debug("getRoleByName cached roleName={} id={}", roleName, dbRole.getId());
        }

        return dbRole;
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
            idToNameCache.remove(existing.getRoleName());
            roleCache.remove(id);
            logger.debug("deleteRoleById removed from cache id={} roleName={}", id, existing.getRoleName());
        }

        mapper.deleteRoleById(id);
        logger.debug("deleteRoleById deleted from DB id={}", id);
    }

	@Override
	public RoleManagementPojo getByRoleName(String roleName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RoleManagementPojo updateRoleByRoleName(String roleName, RoleManagementPojo role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteRoleByRoleName(String roleName) {
		// TODO Auto-generated method stub
		
	}
}
