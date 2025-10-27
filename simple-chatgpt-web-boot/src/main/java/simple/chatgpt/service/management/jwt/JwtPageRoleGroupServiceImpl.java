package simple.chatgpt.service.management.jwt;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.service.management.security.PageManagementService;
import simple.chatgpt.service.management.security.PageRoleGroupManagementService;
import simple.chatgpt.service.management.security.RoleGroupManagementService;
import simple.chatgpt.service.management.security.RoleGroupRoleMappingService;
import simple.chatgpt.service.management.security.RoleManagementService;

@Service
public class JwtPageRoleGroupServiceImpl implements JwtPageRoleGroupService {

    private static final Logger logger = LogManager.getLogger(JwtPageRoleGroupServiceImpl.class);

    private final PageRoleGroupManagementService pageRoleGroupManagementService;
    private final RoleManagementService roleManagementService;
    private final RoleGroupRoleMappingService roleGroupRoleMappingService;
    private final RoleGroupManagementService roleGroupManagementService;
    private final PageManagementService pageManagementService; // <-- new field

    public JwtPageRoleGroupServiceImpl(
            PageRoleGroupManagementService pageRoleGroupManagementService,
            RoleManagementService roleManagementService,
            RoleGroupRoleMappingService roleGroupRoleMappingService,
            RoleGroupManagementService roleGroupManagementService,
            PageManagementService pageManagementService) // <-- inject here
    {
        logger.debug("JwtPageRoleGroupServiceImpl constructor called");
        logger.debug("pageRoleGroupManagementService={}", pageRoleGroupManagementService);
        logger.debug("roleManagementService={}", roleManagementService);
        logger.debug("roleGroupRoleMappingService={}", roleGroupRoleMappingService);
        logger.debug("roleGroupManagementService={}", roleGroupManagementService);
        logger.debug("pageManagementService={}", pageManagementService);

        this.pageRoleGroupManagementService = pageRoleGroupManagementService;
        this.roleManagementService = roleManagementService;
        this.roleGroupRoleMappingService = roleGroupRoleMappingService;
        this.roleGroupManagementService = roleGroupManagementService;
        this.pageManagementService = pageManagementService;
    }

    @Override
    public List<String> getAllowedRoleGroups(String url) {
        logger.debug("getAllowedRoleGroups called with url={}", url);

        List<PageRoleGroupManagementPojo> mappings = pageRoleGroupManagementService.getMappingsByUrlPattern(url);
        logger.debug("getAllowedRoleGroups mappings={}", mappings);

        List<String> allowedRoleGroups = mappings.stream()
                .map(PageRoleGroupManagementPojo::getRoleGroupRef)
                .collect(Collectors.toList());

        logger.debug("getAllowedRoleGroups ##########");
        logger.debug("getAllowedRoleGroups allowedRoleGroups={}", allowedRoleGroups);
        logger.debug("getAllowedRoleGroups ##########");
        return allowedRoleGroups;
    }
    
    @Override
    public List<String> getAllowedRoles(String url) {
        logger.debug("getAllowedRoles called with url={}", url);

        // Step 1: Get all role-groups mapped to this URL
        List<String> roleGroupRefs = getAllowedRoleGroups(url);
        logger.debug("getAllowedRoles roleGroupRefs={}", roleGroupRefs);

        // Step 2: Flatten all roles inside those role-groups
        List<String> allowedRoles = roleGroupRefs.stream()
                .flatMap(rgName -> {
                    RoleGroupManagementPojo rg = roleGroupManagementService.getRoleGroupByGroupName(rgName);
                    if (rg == null) {
                        logger.warn("Role group not found: {}", rgName);
                        return Stream.empty();
                    }

                    List<RoleGroupRoleMappingPojo> mappings = roleGroupRoleMappingService.getMappingsByRoleGroupId(rg.getId());
                    if (mappings == null || mappings.isEmpty()) return Stream.empty();

                    return mappings.stream()
                            .map(mapping -> {
                                RoleManagementPojo role = roleManagementService.get(mapping.getRoleId());
                                return role != null ? role.getRoleName() : null;
                            })
                            .filter(r -> r != null);
                })
                .distinct() // optional: remove duplicates
                .collect(Collectors.toList());

        logger.debug("getAllowedRoles allowedRoles={}", allowedRoles);
        return allowedRoles;
    }
    
    @Override
    public List<String> getAllowedRoleGroupNames(String url) {
    	logger.debug("getAllowedRoleGroupNames url={}", url);
    	
    	List<String> roleGroupNames = pageManagementService.getRoleGroupNamesByUrlPattern(url);
    	logger.debug("getAllowedRoleGroupNames roleGroupNames={}", roleGroupNames);
    	
    	return roleGroupNames;
	}

    @Override
    public List<String> getAllowedRoleNames(String url) {
    	logger.debug("getAllowedRoleNames url={}", url);
    	
    	List<String> roleNames = pageManagementService.getRoleNamesByUrlPattern(url);
    	logger.debug("getAllowedRoleNames roleNames={}", roleNames);
    	
    	return roleNames;
    }
    
}
