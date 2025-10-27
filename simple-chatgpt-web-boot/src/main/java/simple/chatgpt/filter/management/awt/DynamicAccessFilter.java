package simple.chatgpt.filter.management.awt;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import simple.chatgpt.service.management.jwt.JwtPageRoleGroupService;

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
        String rawUrl = req.getRequestURI();
        logger.debug("doFilterInternal rawUrl={}", rawUrl);

        // Strip context prefix for page-role-group matching
        String url = normalizeUrl(rawUrl, req.getContextPath());
        logger.debug("doFilterInternal normalized url={}", url);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("doFilterInternal auth={}", auth);

        if (auth != null) {
            // roles from JwtUserDetailsServiceImpl.loadUserByUsername
            List<String> roles = auth.getAuthorities().stream()
                    .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                    .toList();
            logger.debug("doFilterInternal ##########");
            logger.debug("doFilterInternal roles={}", roles);
            logger.debug("doFilterInternal ##########");

            /*
            ///////////////////////
            hung : dont remove it
            obsolete it though...
            ///////////////////////
            ///allowed from pageRoleGroupService.getAllowedRoles
            List<String> allowed = pageRoleGroupService.getAllowedRoles(url);
            */
            // allowed from pageRoleGroupService.getAllowedRoleNames
            List<String> allowed = pageRoleGroupService.getAllowedRoleNames(url);
            logger.debug("doFilterInternal ##########");
            logger.debug("doFilterInternal url={}", url);
            logger.debug("doFilterInternal allowed={}", allowed);
            logger.debug("doFilterInternal ##########");

            /*
            hung : dont remove it
            common practice is if no role restriction, then PASS
            */
            // If no allowed roles, pass by default
            if (allowed.isEmpty()) {
                logger.debug("doFilterInternal passing by default url={}", url);
                chain.doFilter(req, res);
                return;
            } else {
                logger.debug("doFilterInternal need to check permitted for allowed={}", allowed);
            }

            boolean permitted = roles.stream().anyMatch(allowed::contains);
            logger.debug("doFilterInternal ##########");
            logger.debug("doFilterInternal roles={}", roles);
            logger.debug("doFilterInternal url={}", url);
            logger.debug("doFilterInternal permitted={}", permitted);
            logger.debug("doFilterInternal ##########");

            if (!permitted) {
                logger.debug("doFilterInternal access denied for url={}", url);
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        chain.doFilter(req, res);
    }

    /**
     * hung : dont remove
     * Helper to normalize URL for page-role-group matching
     *  - Strips the context path (e.g., /chatgpt-production)
     *  - Ensures leading "/" exists
     */
    private String normalizeUrl(String url, String contextPath) {
        logger.debug("normalizeUrl called with url={} contextPath={}", url, contextPath);

        if (contextPath != null && !contextPath.isEmpty() && url.startsWith(contextPath)) {
            url = url.substring(contextPath.length());
            logger.debug("normalizeUrl stripped contextPath, url={}", url);
        }

        if (!url.startsWith("/")) {
            url = "/" + url;
            logger.debug("normalizeUrl added leading /, url={}", url);
        }

        logger.debug("normalizeUrl returning url={}", url);
        return url;
    }
}