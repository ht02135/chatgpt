package simple.chatgpt.service.management.jwt;

import java.util.List;
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
import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.service.management.UserManagementService;
import simple.chatgpt.service.management.security.RoleGroupManagementService;
import simple.chatgpt.service.management.security.RoleGroupRoleMappingService;
import simple.chatgpt.service.management.security.RoleManagementService;
import simple.chatgpt.service.management.security.UserManagementRoleGroupMappingService;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(JwtUserDetailsServiceImpl.class);

    private final UserManagementService userManagementService;
    private final UserManagementRoleGroupMappingService mappingService;
    private final RoleGroupManagementService roleGroupManagementService;
    private final RoleGroupRoleMappingService roleGroupRoleMappingService;
    private final RoleManagementService roleManagementService;

    public JwtUserDetailsServiceImpl(
            UserManagementService userManagementService,
            UserManagementRoleGroupMappingService mappingService,
            RoleGroupManagementService roleGroupManagementService,
            RoleGroupRoleMappingService roleGroupRoleMappingService,
            RoleManagementService roleManagementService) 
    {
        logger.debug("JwtUserDetailsServiceImpl constructor called");
        this.userManagementService = userManagementService;
        this.mappingService = mappingService;
        this.roleGroupManagementService = roleGroupManagementService;
        this.roleGroupRoleMappingService = roleGroupRoleMappingService;
        this.roleManagementService = roleManagementService;
    }

    /*
    ///////////////////////
    hung : dont remove it
    obsolete it though...
    ///////////////////////
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("loadUserByUsername called username={}", username);

        UserManagementPojo user = userManagementService.getUserByUserName(username);
        if (user == null) {
            logger.debug("User not found for username={}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        logger.debug("loadUserByUsername populated user={}", user);

        // Explicitly populate roleGroups from mapping service
        List<RoleGroupManagementPojo> roleGroups = mappingService.getMappingsByUserId(user.getId())
            .stream()
            .map(m -> roleGroupManagementService.get(m.getRoleGroupId()))
            .filter(rg -> rg != null)
            .toList();
        user.setRoleGroups(roleGroups);
        logger.debug("loadUserByUsername populated roleGroups={}", roleGroups);

        List<String> roleNames = getRoleNamesFromRoleGroups(user.getRoleGroupRefs());
        logger.debug("loadUserByUsername roleNames={}", roleNames);

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
    */
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("loadUserByUsername called username={}", username);

        UserManagementPojo user = userManagementService.getUserByUserName(username);
        if (user == null) {
            logger.debug("User not found for username={}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        logger.debug("loadUserByUsername populated user={}", user);

        // Use existing method to get role names
        List<String> roleNames = getRoleNamesByUserName(username);
        logger.debug("loadUserByUsername roleNames={}", roleNames);

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

    public List<String> getRoleNamesFromRoleGroups(List<String> roleGroupRefs) {
        logger.debug("getRoleNamesFromRoleGroups called roleGroupRefs={}", roleGroupRefs);
        if (roleGroupRefs == null || roleGroupRefs.isEmpty()) {
            return List.of();
        }

        List<String> roles = roleGroupRefs.stream()
            .flatMap(rgName -> {
                RoleGroupManagementPojo rg = roleGroupManagementService.getRoleGroupByGroupName(rgName);
                if (rg == null) return Stream.empty();

                return roleGroupRoleMappingService.getMappingsByRoleGroupId(rg.getId()).stream()
                        .map(mapping -> {
                            RoleManagementPojo role = roleManagementService.get(mapping.getRoleId());
                            return role != null ? role.getRoleName() : null;
                        })
                        .filter(r -> r != null);
            })
            .distinct()
            .toList();

        logger.debug("getRoleNamesFromRoleGroups result={}", roles);
        return roles;
    }
    
    public List<String> getRoleNamesByUserName(String userName) {
    	logger.debug("getRoleNamesByUserName userName={}", userName);
    	
    	List<String> roleNames = userManagementService.getRoleNamesByUserName(userName);
    	logger.debug("getRoleNamesByUserName roleNames={}", roleNames);
    	
    	return roleNames;
    }
}
