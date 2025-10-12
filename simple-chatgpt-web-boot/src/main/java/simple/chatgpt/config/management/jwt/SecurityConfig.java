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

import simple.chatgpt.filter.management.security.awt.DynamicAccessFilter;

/*
Goals:
- Permit all /auth/** requests (login, registration, etc.)
- Use CustomAuthenticationEntryPoint to handle unauthenticated requests
- Apply DynamicAccessFilter for runtime authorization on all other endpoints
- Add JwtAuthenticationFilter for JWT validation
- Keep JWT stateless setup (SessionCreationPolicy.STATELESS)
- Support formLogin and logout
*/

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LogManager.getLogger(SecurityConfig.class);
    public static final String AUTH_URL = "/auth/**";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final DynamicAccessFilter dynamicAccessFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            DynamicAccessFilter dynamicAccessFilter
    ) {
        logger.debug("SecurityConfig constructor called");
        logger.debug("SecurityConfig jwtAuthenticationFilter={}", jwtAuthenticationFilter);
        logger.debug("SecurityConfig authenticationEntryPoint={}", authenticationEntryPoint);
        logger.debug("SecurityConfig dynamicAccessFilter={}", dynamicAccessFilter);

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.dynamicAccessFilter = dynamicAccessFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.debug("securityFilterChain called");

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Permit /auth/** endpoints without authentication
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher(AUTH_URL)).permitAll()
                .anyRequest().authenticated() // all other requests require authentication
            )

            /*
            When an unauthenticated user tries to access a protected URL 
            (.anyRequest().authenticated()), Spring Security needs to decide what to do.
			authenticationEntryPoint(authenticationEntryPoint) tells Spring:
			“If the user is not logged in, call this CustomAuthenticationEntryPoint.”
			Your CustomAuthenticationEntryPoint might:
			Redirect the user to /login.jsp, or
			Return a JSON error for APIs
			Essentially, it’s the “unauthorized handler” for requests without valid credentials.
            */
            // Use custom entry point for unauthenticated requests
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
            );

            /*
            Enables form-based login (classic web login).
			loginPage("/login.jsp") → Spring will use your JSP page as the login form.
			permitAll() → anyone can access /login.jsp without being authenticated.
			Behind the scenes:
			Spring registers a UsernamePasswordAuthenticationFilter to handle 
			POST requests from /login.jsp.
			On successful login, Spring creates a session and stores the user 
			authentication in SecurityContextHolder.
            */
            // Form login page not needed for jwt
        	/*
            .formLogin(form -> form
                .loginPage("/login.jsp")
                .permitAll()
            )
            */

            /*
            Configures logout behavior:
			logoutUrl("/logout") → This URL triggers logout.
			logoutSuccessUrl("/logout.jsp") → After logging out, redirect the user here.
			invalidateHttpSession(true) → Destroy the HTTP session (remove all session attributes).
			deleteCookies("JSESSIONID") → Remove the session cookie so the user is fully logged out.
			permitAll() → anyone can access /logout without being logged in.
            */
            // Logout handling not needed for jwt
        	/*
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logout.jsp")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
            */

        // Add JWT authentication filter first
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Add DynamicAccessFilter after JWT is validated
        http.addFilterAfter(dynamicAccessFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        logger.debug("authenticationManager called");
        return configuration.getAuthenticationManager();
    }
}
