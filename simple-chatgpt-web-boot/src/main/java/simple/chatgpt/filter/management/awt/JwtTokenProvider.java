package simple.chatgpt.filter.management.awt;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import simple.chatgpt.service.management.jwt.JwtUserDetailsServiceImpl;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LogManager.getLogger(JwtTokenProvider.class);

    // Use a strong secret key
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Inject JwtUserDetailsServiceImpl (implements UserDetailsService)
    private final JwtUserDetailsServiceImpl userDetailsService;

    public JwtTokenProvider(JwtUserDetailsServiceImpl userDetailsService) {
        logger.debug("JwtTokenProvider constructor called");
        logger.debug("JwtTokenProvider userDetailsService={}", userDetailsService);
        this.userDetailsService = userDetailsService;
    }

    public String createToken(String username, List<String> roles) {
        logger.debug("createToken called");
        logger.debug("createToken username={}", username);
        logger.debug("createToken roles={}", roles);

        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000); // 1 hour

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)
                .compact();
    }

    // get user from token, and load the user details
    public Authentication getAuthentication(String token) {
        logger.debug("getAuthentication called");
        logger.debug("getAuthentication token={}", token);

        String username = getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        logger.debug("getAuthentication userDetails={}", userDetails);

        return new UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities());
    }
    
    public String resolveToken(HttpServletRequest req) {
    	logger.debug("resolveToken called");
    	
    	String bearer = req.getHeader("Authorization");
        logger.debug("resolveToken bearer={}", bearer);
        
        if (bearer != null) {
            return bearer.replace("Bearer ", "");
        }
        if (req.getCookies() != null) {
        	logger.debug("resolveToken req.getCookies()={}", req.getCookies());
            for (Cookie cookie : req.getCookies()) {
            	logger.debug("resolveToken cookie={}", cookie);
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    // resolveToken() is responsible for extracting the JWT token from 
    // the incoming HTTP request — usually from the Authorization header.
    public boolean validateToken(String token) {
        logger.debug("validateToken called");
        logger.debug("validateToken token={}", token);

        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("JWT validation failed", e);
            return false;
        }
    }

    // get userName from token
    public String getUsername(String token) {
        logger.debug("getUsername called");
        logger.debug("getUsername token={}", token);

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
