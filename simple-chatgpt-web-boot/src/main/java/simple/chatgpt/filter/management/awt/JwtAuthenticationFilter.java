package simple.chatgpt.filter.management.awt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        logger.debug("doFilterInternal START");
        
        // ===== Step 1: Try to get JWT from Authorization header =====
        String token = jwtTokenProvider.resolveToken(request); // your existing logic
        logger.debug("doFilterInternal token={}", token);

        // ===== Step 2: Fallback to cookie if header is missing =====
        if (token == null) {
            Cookie[] cookies = request.getCookies();
            logger.debug("doFilterInternal cookies={}", cookies);
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwtToken".equals(cookie.getName())) {
                        token = cookie.getValue();
                        logger.debug("doFilterInternal token={}", token);
                        break;
                    }
                }
            }
        }
        
        logger.debug("doFilterInternal before validateToken token={}", token);
        if (token != null && !token.isEmpty()) {
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    logger.debug("doFilterInternal auth={}", auth);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException | IllegalArgumentException e) {
                logger.error("JWT validation failed", e);
                SecurityContextHolder.clearContext();
            }
        } else {
        	/*
        	hung : dont remove it
        	In a JWT-based stateless authentication flow, if the request comes 
        	without a token, you typically don’t throw an exception immediately. 
        	Instead, you:
			1>Log it for debugging/monitoring
			2>Leave the SecurityContext empty (unauthenticated)
        	*/
            logger.debug("No JWT token found, skipping authentication for this request");
            SecurityContextHolder.clearContext(); // explicit safety
        }

        filterChain.doFilter(request, response);
        
        logger.debug("doFilterInternal DONE");
    }
}