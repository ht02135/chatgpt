package simple.chatgpt.config.management.jwt;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/*
Unauthenticated access
User types /user/users.jsp directly.
DynamicAccessFilter triggers → Spring sees no authenticated user → 
AuthenticationEntryPoint.commence() is called → redirects to /login.jsp
*/

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LogManager.getLogger(CustomAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        logger.debug("commence called");
        logger.debug("commence requestURI={}", request.getRequestURI());
        logger.debug("commence authException={}", authException.getMessage());

        // Redirect to login page
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}