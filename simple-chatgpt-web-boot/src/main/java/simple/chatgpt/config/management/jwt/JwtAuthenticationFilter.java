package simple.chatgpt.config.management.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
        if (token != null && jwtTokenProvider.validateToken(token)) {
            var auth = jwtTokenProvider.getAuthentication(token);
            logger.debug("doFilterInternal auth={}", auth);

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
        
        logger.debug("doFilterInternal DONE");
    }
}