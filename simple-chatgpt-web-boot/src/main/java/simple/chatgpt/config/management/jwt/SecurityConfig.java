package simple.chatgpt.config.management.jwt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import simple.chatgpt.service.management.PropertyManagementService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
    // Define the auth URL prefix as a constant
    public static final String AUTH_URL = "/auth/**";

    private static final Logger logger = LogManager.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PropertyManagementService propertyService;

    public SecurityConfig(
        JwtAuthenticationFilter jwtAuthenticationFilter,
        PropertyManagementService propertyService
    ) {
        logger.debug("SecurityConfig constructor called");
        logger.debug("SecurityConfig jwtAuthenticationFilter={}", jwtAuthenticationFilter);
        logger.debug("SecurityConfig propertyService={}", propertyService);

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.propertyService = propertyService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.debug("securityFilterChain called");

        http
            // Disable CSRF since we are using JWT
            .csrf(csrf -> csrf.disable())

            // Stateless session (no HttpSession)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Authorize requests
            .authorizeHttpRequests(auth -> auth
                // Permit all requests to /auth/**
                .requestMatchers(new AntPathRequestMatcher(AUTH_URL)).permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            );

        // Add JWT filter before the standard authentication filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        logger.debug("authenticationManager called");
        return configuration.getAuthenticationManager();
    }
}
