package simple.chatgpt.service.management.security.jwt;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;
import simple.chatgpt.service.management.security.PageRoleGroupManagementService;

@Service
public class JwtPageRoleGroupServiceImpl implements JwtPageRoleGroupService {

    private static final Logger logger = LogManager.getLogger(JwtPageRoleGroupServiceImpl.class);

    private final PageRoleGroupManagementService pageRoleGroupManagementService;

    public JwtPageRoleGroupServiceImpl(PageRoleGroupManagementService pageRoleGroupManagementService) {
        logger.debug("JwtPageRoleGroupServiceImpl constructor called");
        logger.debug("JwtPageRoleGroupServiceImpl pageRoleGroupManagementService={}", pageRoleGroupManagementService);
        this.pageRoleGroupManagementService = pageRoleGroupManagementService;
    }

    @Override
    public List<String> getAllowedRoleGroups(String url) {
        logger.debug("getAllowedRoleGroups called with url={}", url);

        List<PageRoleGroupManagementPojo> mappings = pageRoleGroupManagementService.getMappingsByUrlPattern(url);
        logger.debug("getAllowedRoleGroups mappings={}", mappings);

        List<String> allowedRoleGroups = mappings.stream()
                .map(PageRoleGroupManagementPojo::getRoleGroupRef)
                .collect(Collectors.toList());

        logger.debug("getAllowedRoleGroups result={}", allowedRoleGroups);
        return allowedRoleGroups;
    }
}
