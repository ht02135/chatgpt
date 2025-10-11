package simple.chatgpt.service.management.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Service for loading user details for JWT authentication.
 */
public interface JwtUserDetailsService {

    /**
     * Loads a user by username.
     *
     * @param username the username
     * @return UserDetails object with authorities
     * @throws UsernameNotFoundException if user not found
     */
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
