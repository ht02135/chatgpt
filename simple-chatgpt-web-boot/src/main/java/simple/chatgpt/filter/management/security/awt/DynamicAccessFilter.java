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
        if (auth != null) {
            var roles = auth.getAuthorities().stream().map(a -> a.getAuthority()).toList();
            var allowed = pageRoleGroupService.getAllowedRoleGroups(url);

            logger.debug("doFilterInternal roles={}", roles);
            logger.debug("doFilterInternal allowed={}", allowed);

            boolean permitted = roles.stream().anyMatch(allowed::contains);
            if (!permitted) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        chain.doFilter(req, res);
    }
}