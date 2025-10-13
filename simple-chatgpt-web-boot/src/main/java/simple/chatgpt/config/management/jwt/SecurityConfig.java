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
    public static final String PUBLIC_URL = "/public/**";
    
    public static final String ALL_JS_FILE = "/**/*.js";
    public static final String ALL_CSS_FILE = "/**/*.css";
    
    public static final String INDEX_FILE = "/index.html";
    public static final String TEST_FILE = "/this_is_test.html";

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

        // Disable CSRF and set stateless session
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
        	.requestMatchers(new AntPathRequestMatcher(ALL_JS_FILE)).permitAll()
        	.requestMatchers(new AntPathRequestMatcher(ALL_CSS_FILE)).permitAll()
        	.requestMatchers(new AntPathRequestMatcher(PUBLIC_URL)).permitAll()
        	.requestMatchers(new AntPathRequestMatcher(INDEX_FILE)).permitAll()
        	.requestMatchers(new AntPathRequestMatcher(TEST_FILE)).permitAll()
        	.requestMatchers(new AntPathRequestMatcher(AUTH_URL)).permitAll()
        	.anyRequest().authenticated()
        );

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
        http.exceptionHandling(ex -> ex
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

        /*
        jwtAuthenticationFilter is a custom filter you wrote that:
    	Reads the Authorization: Bearer <token> header from the request
    	Validates the JWT
    	Creates an Authentication object and sets it in SecurityContextHolder
    	addFilterBefore(..., UsernamePasswordAuthenticationFilter.class) means:
    	“Place this JWT filter before Spring’s default username/password login filter in the security filter chain.” 
        */
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
