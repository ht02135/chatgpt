package simple.chatgpt.service.management.security.jwt;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.service.management.UserManagementService;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(JwtUserDetailsServiceImpl.class);

    private final UserManagementService userManagementService;

    public JwtUserDetailsServiceImpl(UserManagementService userManagementService) {
        logger.debug("JwtUserDetailsServiceImpl constructor called");
        logger.debug("JwtUserDetailsServiceImpl userManagementService={}", userManagementService);
        this.userManagementService = userManagementService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("loadUserByUsername called");
        logger.debug("loadUserByUsername username={}", username);

        List<UserManagementPojo> users = userManagementService.getUserdByUserName(username);
        logger.debug("loadUserByUsername users={}", users);

        if (users == null || users.isEmpty()) {
            logger.debug("User not found for username={}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        UserManagementPojo user = users.get(0);
        logger.debug("loadUserByUsername user={}", user);

        // Map all role-groups to authorities
        List<SimpleGrantedAuthority> authorities = user.getRoleGroupRefs().stream()
                .map(SimpleGrantedAuthority::new)
                .toList(); // or Collectors.toList() if Java <16

        logger.debug("loadUserByUsername authorities={}", authorities);

        return User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .authorities(authorities)
                .accountLocked(user.getLocked())
                .disabled(!user.getActive())
                .build();
    }

}
