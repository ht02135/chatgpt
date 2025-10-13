package simple.chatgpt.controller.management.security.awt;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.config.management.jwt.JwtTokenProvider;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping("/management/auth")
public class AuthController {

    // ===== Constants =====
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ROLES = "roles";
    public static final String KEY_TOKEN = "token";

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<Response<Map<String, Object>>> login(@RequestBody Map<String, String> creds) {
        logger.debug("login called");
        logger.debug("login creds={}", creds);

        if (!creds.containsKey(KEY_USERNAME) || !creds.containsKey(KEY_PASSWORD)) {
            logger.debug("login missing username or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Username and password are required", null, HttpStatus.BAD_REQUEST.value()));
        }

        /*
        Spring Security uses AuthenticationManager (here in AuthController) 
        with DaoAuthenticationProvider.
		DaoAuthenticationProvider internally calls 
		PasswordEncoder.matches(rawPassword, encodedPasswordFromDB):
        */
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(creds.get(KEY_USERNAME), creds.get(KEY_PASSWORD))
            );
            logger.debug("login auth={}", auth);

            var user = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
            var roles = user.getAuthorities().stream().map(a -> a.getAuthority()).toList();
            var token = jwtTokenProvider.createToken(user.getUsername(), roles);

            Map<String, Object> data = Map.of(
            		KEY_USERNAME, user.getUsername(),
            		KEY_ROLES, roles,
            		KEY_TOKEN, token
            );

            return ResponseEntity.ok(Response.success("Login successful", data, HttpStatus.OK.value()));

        } catch (AuthenticationException ex) {
            logger.error("login failed", ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Response.error("Invalid username or password", null, HttpStatus.UNAUTHORIZED.value()));
        }
    }
}