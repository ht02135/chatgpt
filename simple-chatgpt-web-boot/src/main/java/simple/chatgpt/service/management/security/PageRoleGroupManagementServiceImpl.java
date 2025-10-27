package simple.chatgpt.service.management.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import simple.chatgpt.config.management.loader.SecurityConfigLoader;
import simple.chatgpt.config.management.security.PageRoleGroupConfig;
import simple.chatgpt.mapper.management.security.PageRoleGroupManagementMapper;
import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.SafeConverter;

@Service
public class PageRoleGroupManagementServiceImpl implements PageRoleGroupManagementService {

    private static final Logger logger = LogManager.getLogger(PageRoleGroupManagementServiceImpl.class);

    private final PageRoleGroupManagementMapper pageMapper;
    private final UserManagementRoleGroupMappingService userRoleGroupMappingService;
    private final RoleGroupManagementService roleGroupService;
    private final SecurityConfigLoader securityConfigLoader;

    @Autowired
    public PageRoleGroupManagementServiceImpl(
            PageRoleGroupManagementMapper pageMapper,
            UserManagementRoleGroupMappingService userRoleGroupMappingService,
            RoleGroupManagementService roleGroupService,
            SecurityConfigLoader securityConfigLoader) {

        logger.debug("PageRoleGroupManagementServiceImpl START");
        logger.debug("PageRoleGroupManagementServiceImpl pageMapper={}", pageMapper);
        logger.debug("PageRoleGroupManagementServiceImpl userRoleGroupMappingService={}", userRoleGroupMappingService);
        logger.debug("PageRoleGroupManagementServiceImpl roleGroupService={}", roleGroupService);
        logger.debug("PageRoleGroupManagementServiceImpl securityConfigLoader={}", securityConfigLoader);

        this.pageMapper = pageMapper;
        this.userRoleGroupMappingService = userRoleGroupMappingService;
        this.roleGroupService = roleGroupService;
        this.securityConfigLoader = securityConfigLoader;

        logger.debug("PageRoleGroupManagementServiceImpl DONE");
    }

    @PostConstruct
    private void init() {
        logger.debug("init START");
        initializeDB();
        logger.debug("init DONE");
    }

    private void initializeDB() {
        logger.debug("initializeDB called");

        try {
            // ======================================================
            // STEP 1. Load page-role-groups from XML config
            // ======================================================
            List<PageRoleGroupConfig> pageRoleGroupConfigs = securityConfigLoader.getPageRoleGroups();
            logger.debug("initializeDB pageRoleGroupConfigs={}", pageRoleGroupConfigs);

            for (PageRoleGroupConfig prgConfig : pageRoleGroupConfigs) {
                logger.debug("initializeDB processing prgConfig={}", prgConfig);
                logger.debug("initializeDB prgConfig.urlPattern={}", prgConfig.getUrlPattern());
                logger.debug("initializeDB prgConfig.roleGroup={}", prgConfig.getRoleGroup());

                // ======================================================
                // STEP 2. Lookup RoleGroupManagementPojo for this role-group
                // ======================================================
                Map<String, Object> rgParams = new HashMap<>();
                rgParams.put("groupName", prgConfig.getRoleGroup());
                List<RoleGroupManagementPojo> matchedGroups = roleGroupService.getAll();
                RoleGroupManagementPojo roleGroupPojo = null;
                for (RoleGroupManagementPojo rg : matchedGroups) {
                    if (rg.getGroupName().equals(prgConfig.getRoleGroup())) {
                        roleGroupPojo = rg;
                        break;
                    }
                }

                if (roleGroupPojo == null) {
                    logger.warn("initializeDB skipping page-role-group because roleGroup not found: {}", prgConfig.getRoleGroup());
                    continue;
                }

                // ======================================================
                // STEP 3. Check if page-role-group already exists
                // ======================================================
                Map<String, Object> pageParams = new HashMap<>();
                pageParams.put("urlPattern", prgConfig.getUrlPattern());
                pageParams.put("roleGroupId", roleGroupPojo.getId());

                List<PageRoleGroupManagementPojo> existingPages = getMappingsByParams(pageParams);
                logger.debug("initializeDB existingPages.size={}", existingPages.size());

                PageRoleGroupManagementPojo pagePojo;
                if (existingPages.isEmpty()) {
                    // ======================================================
                    // STEP 4. Create new page-role-group
                    // ======================================================
                    pagePojo = new PageRoleGroupManagementPojo();
                    pagePojo.setUrlPattern(prgConfig.getUrlPattern());
                    pagePojo.setRoleGroup(roleGroupPojo);

                    create(pagePojo);
                    logger.debug("initializeDB created new pagePojo={}", pagePojo);
                } else {
                    pagePojo = existingPages.get(0);
                    logger.debug("initializeDB found existing pagePojo={}", pagePojo);
                }

                logger.debug("initializeDB finished page-role-group urlPattern={}", prgConfig.getUrlPattern());
            }

            logger.debug("initializeDB completed successfully");

        } catch (Exception e) {
            logger.error("initializeDB failed", e);
            throw new RuntimeException("Failed to initialize page-role-groups from XML", e);
        }

        logger.debug("initializeDB DONE");
    }

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @Override
    public PageRoleGroupManagementPojo create(PageRoleGroupManagementPojo pageRoleGroup) {
        logger.debug("create called");
        logger.debug("create pageRoleGroup={}", pageRoleGroup);
        pageMapper.create(pageRoleGroup);
        return pageRoleGroup;
    }

    @Override
    public PageRoleGroupManagementPojo update(Long id, PageRoleGroupManagementPojo pageRoleGroup) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update pageRoleGroup={}", pageRoleGroup);
        pageMapper.update(id, pageRoleGroup);
        return pageRoleGroup;
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> search(Map<String, String> params) {
        logger.debug("search called");
        logger.debug("search params={}", params);

        if (!params.containsKey("page")) params.put("page", "0");
        if (!params.containsKey("size")) params.put("size", "20");
        int page = SafeConverter.toIntOrDefault(params.get("page"), 0);
        int size = SafeConverter.toIntOrDefault(params.get("size"), 20);
        int offset = page * size;

        if (!params.containsKey("offset")) params.put("offset", String.valueOf(offset));
        if (!params.containsKey("limit")) params.put("limit", String.valueOf(size));
        if (!params.containsKey("sortField")) params.put("sortField", "id");
        if (!params.containsKey("sortDirection")) params.put("sortDirection", "ASC");
        params.put("sortDirection", params.get("sortDirection").toUpperCase());

        // Hung : mapper expect Map<String, Object> for offset and limit
    	Map<String, Object> mapperParams = new HashMap<>(params);
        mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
        mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));
        
        List<PageRoleGroupManagementPojo> items = pageMapper.search(mapperParams);
        long totalCount = items.size();
        PagedResult<PageRoleGroupManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("search return={}", result);
        return result;
    }

    @Override
    public PageRoleGroupManagementPojo get(Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);
        PageRoleGroupManagementPojo pageRoleGroup = pageMapper.get(id);
        logger.debug("get return={}", pageRoleGroup);
        return pageRoleGroup;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);
        pageMapper.delete(id);
    }

    // ======= OTHER METHODS =======

    @Override
	public List<PageRoleGroupManagementPojo> getMappingsByParams(Map<String, Object> params)
	{
        logger.debug("getMappingsByParams called");

        List<PageRoleGroupManagementPojo> mappings = pageMapper.search(params);
        return mappings;
	}
	
	// mapper uses #{params.urlPattern}
    @Override
    public List<PageRoleGroupManagementPojo> getMappingsByUrlPattern(String urlPattern) {
        logger.debug("getMappingsByUrlPattern called with urlPattern={}", urlPattern);

        List<PageRoleGroupManagementPojo> allMappings = getAll(); // fetch all
        List<PageRoleGroupManagementPojo> matched = new ArrayList<>();
        AntPathMatcher matcher = new AntPathMatcher();

        for (PageRoleGroupManagementPojo mapping : allMappings) {
            String pattern = mapping.getUrlPattern(); // e.g., "/property/**"
            if (matcher.match(pattern, urlPattern)) {
                matched.add(mapping);
            }
        }

        logger.debug("getMappingsByUrlPattern ##########");
        logger.debug("getMappingsByUrlPattern matched={}", matched);
        logger.debug("getMappingsByUrlPattern ##########");
        return matched;
    }

    @Override
    public List<PageRoleGroupManagementPojo> getAll()
	{
        logger.debug("getAll called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        // No offset/limit => all rows
        List<PageRoleGroupManagementPojo> mappings = getMappingsByParams(params);
        
        return mappings;	
	}
	
}
