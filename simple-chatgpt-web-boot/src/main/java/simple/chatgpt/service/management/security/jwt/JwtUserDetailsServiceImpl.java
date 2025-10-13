package simple.chatgpt.service.management.security.jwt;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.service.management.UserManagementService;
import simple.chatgpt.service.management.security.RoleGroupManagementService;
import simple.chatgpt.service.management.security.RoleGroupRoleMappingService;
import simple.chatgpt.service.management.security.RoleManagementService;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(JwtUserDetailsServiceImpl.class);

    private final UserManagementService userManagementService;
    private final RoleGroupManagementService roleGroupManagementService;
    private final RoleGroupRoleMappingService roleGroupRoleMappingService;
    private final RoleManagementService roleManagementService;

    public JwtUserDetailsServiceImpl(
            UserManagementService userManagementService,
            RoleGroupManagementService roleGroupManagementService,
            RoleGroupRoleMappingService roleGroupRoleMappingService,
            RoleManagementService roleManagementService) 
    {
        logger.debug("JwtUserDetailsServiceImpl constructor called");
        logger.debug("userManagementService={}", userManagementService);
        logger.debug("roleGroupManagementService={}", roleGroupManagementService);
        logger.debug("roleGroupRoleMappingService={}", roleGroupRoleMappingService);
        logger.debug("roleManagementService={}", roleManagementService);

        this.userManagementService = userManagementService;
        this.roleGroupManagementService = roleGroupManagementService;
        this.roleGroupRoleMappingService = roleGroupRoleMappingService;
        this.roleManagementService = roleManagementService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("loadUserByUsername called username={}", username);

        UserManagementPojo user = userManagementService.getUserByUserName(username);
        if (user == null) {
            logger.debug("User not found for username={}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        logger.debug("loadUserByUsername user={}", user);

        List<String> roleNames =  getRolesFromRoleGroups(user.getRoleGroupRefs());
        logger.debug("loadUserByUsername roleNames={}", roleNames);
        
        // Map all role-groups to authorities (can also use roles later)
        
        List<SimpleGrantedAuthority> authorities = roleNames.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();    
        logger.debug("loadUserByUsername authorities={}", authorities);

        return User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .authorities(authorities)
                .accountLocked(user.getLocked())
                .disabled(!user.getActive())
                .build();
    }

    /**
     * Helper: Given a list of role-group names, return all roles contained in those role-groups.
     */
    public List<String> getRolesFromRoleGroups(List<String> roleGroupRefs) {
        logger.debug("getRolesFromRoleGroups called roleGroupRefs={}", roleGroupRefs);

        if (roleGroupRefs == null || roleGroupRefs.isEmpty()) {
            return List.of();
        }

        List<String> roles = roleGroupRefs.stream()
            .flatMap(rgName -> {
                RoleGroupManagementPojo rg = roleGroupManagementService.getRoleGroupByGroupName(rgName);
                if (rg == null) return Stream.empty();

                List<RoleGroupRoleMappingPojo> mappings = roleGroupRoleMappingService.getMappingsByRoleGroupId(rg.getId());
                if (mappings == null || mappings.isEmpty()) return Stream.empty();

                return mappings.stream()
                        .map(mapping -> {
                            RoleManagementPojo role = roleManagementService.get(mapping.getRoleId());
                            return role != null ? role.getRoleName() : null;
                        })
                        .filter(r -> r != null);
            })
            .distinct()
            .collect(Collectors.toList());

        logger.debug("getRolesFromRoleGroups result={}", roles);
        return roles;
    }
}
