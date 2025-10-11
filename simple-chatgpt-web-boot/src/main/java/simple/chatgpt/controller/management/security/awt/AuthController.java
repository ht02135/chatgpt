package simple.chatgpt.controller.management.security.awt;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.config.management.jwt.JwtTokenProvider;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> creds) {
        logger.debug("login called");
        logger.debug("login creds={}", creds);

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(creds.get("username"), creds.get("password")));
        logger.debug("login auth={}", auth);

        var user = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        var roles = user.getAuthorities().stream().map(a -> a.getAuthority()).toList();
        var token = jwtTokenProvider.createToken(user.getUsername(), roles);
        return Map.of("token", token);
    }
}