package simple.chatgpt.filter.management.awt;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import simple.chatgpt.service.management.PropertyManagementService;
import simple.chatgpt.util.PropertyKey;

/*
Unauthenticated access
User types /user/users.jsp directly.
DynamicAccessFilter triggers → Spring sees no authenticated user → 
AuthenticationEntryPoint.commence() is called → redirects to /login.jsp
*/

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LogManager.getLogger(CustomAuthenticationEntryPoint.class);
    
    public static final String LOGIN_REDIRECT_URL_RELATIVE_PATH = "/management/jsp/auth/login.jsp";
    
    private final PropertyManagementService propertyService;

    public CustomAuthenticationEntryPoint(PropertyManagementService propertyService) {
        logger.debug("CustomAuthenticationEntryPoint constructor called");
        logger.debug("CustomAuthenticationEntryPoint propertyService={}", propertyService);
        this.propertyService = propertyService;
    }
    
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        logger.debug("commence START");
        
        logger.debug("commence requestURI={}", request.getRequestURI());
        logger.debug("commence contextPath={}", request.getContextPath());
        logger.debug("commence authException={}", authException.getMessage());

        
        String loginRedirectURLRelativePath = LOGIN_REDIRECT_URL_RELATIVE_PATH;
        try {
            loginRedirectURLRelativePath = propertyService.getString(PropertyKey.LOGIN_REDIRECT_URL_RELATIVE_PATH);
            logger.debug("commence: Loaded property LOGIN_REDIRECT_URL_RELATIVE_PATH={}", loginRedirectURLRelativePath);
        } catch (Exception e) {
            logger.error("commence: Failed to fetch LOGIN_REDIRECT_URL_RELATIVE_PATH, using default {}", loginRedirectURLRelativePath, e);
        }
        logger.debug("commence: loginRedirectURLRelativePath={}", loginRedirectURLRelativePath);
        
        // Redirect to login page
        response.sendRedirect(request.getContextPath() + loginRedirectURLRelativePath);
        
        logger.debug("commence DONE");
    }
}