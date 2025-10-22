package simple.chatgpt.controller.management.awt;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.JwtException;
import simple.chatgpt.filter.management.awt.JwtTokenProvider;
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
    public ResponseEntity<Response<Map<String, Object>>> login(
            @RequestBody Map<String, String> creds,
            HttpServletRequest request,
            HttpServletResponse response) {
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
            logger.debug("login user={}", user);

            var roles = user.getAuthorities().stream().map(a -> a.getAuthority()).toList();
            logger.debug("login roles={}", roles);

            var token = jwtTokenProvider.createToken(user.getUsername(), roles);
            logger.debug("login token={}", token);

            // ===== Add cookie for JWT with dynamic path =====
            javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("jwtToken", token);
            cookie.setHttpOnly(false);   // true if you want to hide from JS
            cookie.setSecure(false);     // true if using HTTPS
            String contextPath = request.getContextPath();
            cookie.setPath(contextPath.isEmpty() ? "/" : contextPath);
            cookie.setMaxAge(24 * 60 * 60); // 1 day
            response.addCookie(cookie);
            logger.debug("login cookie set for jwtToken, value={}, path={}", token, cookie.getPath());

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

    @PostMapping("/logout")
    public ResponseEntity<Response<String>> logout(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("logout called");

        // Clear JWT cookie with dynamic path
        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("jwtToken", "");
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        String contextPath = request.getContextPath();
        cookie.setPath(contextPath.isEmpty() ? "/" : contextPath);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        logger.debug("logout cookie cleared for jwtToken, path={}", cookie.getPath());

        return ResponseEntity.ok(Response.success("Logout successful", null, HttpStatus.OK.value()));
    }
    
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("validateToken called");
        logger.debug("validateToken request={}", request);
        logger.debug("validateToken response={}", response);

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        try {
            String token = jwtTokenProvider.resolveToken(request);
            logger.debug("validateToken token={}", token);

            if (token != null && !token.isEmpty() && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);
                logger.debug("validateToken username={}", username);

                data.put("valid", true);
                data.put("username", username);
                data.put("message", "Token is valid");

                result.put("success", true);
                result.put("data", data);

                return ResponseEntity.ok(result);
            } else {
                logger.debug("validateToken invalid or empty token");
                data.put("valid", false);
                data.put("message", "Token invalid or missing");

                result.put("success", false);
                result.put("data", data);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }
        } catch (JwtException e) {
            logger.error("validateToken JwtException", e);
            data.put("valid", false);
            data.put("message", "Invalid or expired JWT: " + e.getMessage());

            result.put("success", false);
            result.put("data", data);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        } catch (Exception e) {
            logger.error("validateToken unexpected error", e);
            data.put("valid", false);
            data.put("message", "Unexpected error: " + e.getMessage());

            result.put("success", false);
            result.put("data", data);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
