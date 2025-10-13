package simple.chatgpt.filter.management.security.awt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import simple.chatgpt.service.management.security.jwt.JwtPageRoleGroupService;

/*
hung : dont remove
DynamicAccessFilter is triggered automatically by Spring Security because it 
extends OncePerRequestFilter and is registered as a Spring bean with @Component

1>Spring Security filter chain
When Spring Security is enabled, it builds a filter chain for every HTTP request. 
Each filter in the chain gets a chance to process the request before it reaches 
your controllers.
2>OncePerRequestFilter behavior
DynamicAccessFilter extends OncePerRequestFilter, which ensures that your 
doFilterInternal runs once per request. Spring Security automatically calls 
this method for each incoming request.
3>Triggering the filter
Any HTTP request that goes through Spring Security’s filter chain will trigger it.
That includes typing a URL directly in the browser, clicking a link, or making 
an AJAX call.
Inside doFilterInternal, your code checks the user’s roles and allowed roles for 
that URL and either lets the request continue or returns a 403 Forbidden.
/////////////
So yes, if you type /user/users.jsp directly, your DynamicAccessFilter will 
fire, check the logged-in user’s roles, and decide whether access is permitted.
*/

@Component
public class DynamicAccessFilter extends OncePerRequestFilter {
    private static final Logger logger = LogManager.getLogger(DynamicAccessFilter.class);
    private final JwtPageRoleGroupService pageRoleGroupService;

    public DynamicAccessFilter(JwtPageRoleGroupService pageRoleGroupService) {
        this.pageRoleGroupService = pageRoleGroupService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        logger.debug("doFilterInternal called");
        String url = req.getRequestURI();
        logger.debug("doFilterInternal url={}", url);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("doFilterInternal auth={}", auth);
        if (auth != null) {
        	// roles from JwtUserDetailsServiceImpl.loadUserByUsername
            var roles = auth.getAuthorities().stream().map(a -> a.getAuthority()).toList();
            logger.debug("doFilterInternal roles={}", roles);
            
            // allowed from pageRoleGroupService.getAllowedRoles
            var allowed = pageRoleGroupService.getAllowedRoles(url);
            logger.debug("doFilterInternal allowed={}", allowed);

            boolean permitted = roles.stream().anyMatch(allowed::contains);
            logger.debug("doFilterInternal permitted={}", allowed);
            if (!permitted) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        chain.doFilter(req, res);
    }
}