package simple.chatgpt.service.management.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import simple.chatgpt.config.management.loader.SecurityConfigLoader;
import simple.chatgpt.config.management.security.PageConfig;
import simple.chatgpt.mapper.management.security.PageManagementMapper;
import simple.chatgpt.pojo.management.security.PageManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.SafeConverter;

@Service
public class PageManagementServiceImpl implements PageManagementService {

    private static final Logger logger = LogManager.getLogger(PageManagementServiceImpl.class);

    private final PageManagementMapper pageMapper;
    private final SecurityConfigLoader securityConfigLoader;
    private final RoleGroupManagementService roleGroupService; // <-- new service
    private final Validator validator;

    @Autowired
    public PageManagementServiceImpl(PageManagementMapper pageMapper,
                                     SecurityConfigLoader securityConfigLoader,
                                     RoleGroupManagementService roleGroupService) {
        logger.debug("PageManagementServiceImpl constructor START");
        logger.debug("pageMapper={}", pageMapper);
        logger.debug("securityConfigLoader={}", securityConfigLoader);
        logger.debug("roleGroupService={}", roleGroupService);

        this.pageMapper = pageMapper;
        this.securityConfigLoader = securityConfigLoader;
        this.roleGroupService = roleGroupService;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();

        logger.debug("PageManagementServiceImpl constructor DONE");
    }

    @PostConstruct
    public void postConstruct() {
        logger.debug("postConstruct START");
        initializeDB();
        logger.debug("postConstruct DONE");
    }

    public void initializeDB() {
        logger.debug("initializeDB called");

        List<PageConfig> pageConfigs = securityConfigLoader.getPages();
        for (PageConfig pageConfig : pageConfigs) {
            String urlPattern = pageConfig.getUrlPattern();
            String delimitRoleGroups = pageConfig.getDelimitRoleGroups();

            logger.debug("initializeDB processing pageConfig urlPattern={}", urlPattern);
            logger.debug("initializeDB pageConfig delimitRoleGroups={}", delimitRoleGroups);

            // ===============================
            // STEP 1: Check if page already exists
            // ===============================
            PageManagementPojo existingPage = this.getPageByUrlPattern(urlPattern);
            if (existingPage != null) {
                logger.debug("initializeDB found existing page id={}", existingPage.getId());
                continue; // skip creation
            }

            // ===============================
            // STEP 2: Create new page
            // ===============================
            PageManagementPojo pagePojo = new PageManagementPojo();
            pagePojo.setUrlPattern(urlPattern);
            pagePojo.setDelimitRoleGroups(delimitRoleGroups);

            this.create(pagePojo);
            logger.debug("initializeDB created new page id={}", pagePojo.getId());
        }

        logger.debug("initializeDB DONE");
    }

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @Override
    public PageManagementPojo create(PageManagementPojo page) {
        logger.debug("create called");
        logger.debug("create page={}", page);

        pageMapper.create(page);
        return page;
    }

    @Override
    public PageManagementPojo update(Long id, PageManagementPojo page) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update page={}", page);

        pageMapper.update(id, page);
        return page;
    }

    @Override
    public PagedResult<PageManagementPojo> search(Map<String, String> params) {
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

        Map<String, Object> mapperParams = new HashMap<>(params);
        mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
        mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));

        List<PageManagementPojo> items = pageMapper.search(mapperParams);
        long totalCount = items.size();

        PagedResult<PageManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("search return={}", result);
        return result;
    }

    @Override
    public PageManagementPojo get(Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);

        PageManagementPojo page = pageMapper.get(id);
        logger.debug("get return page={}", page);
        return page;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);

        pageMapper.delete(id);
    }

    // ==============================================================
    // ==================== OTHER METHODS ===========================
    // ==============================================================

    @Override
    public List<PageManagementPojo> getPageByParams(Map<String, Object> params) {
        logger.debug("getPageByParams called");
        logger.debug("getPageByParams params={}", params);

        List<PageManagementPojo> pages = pageMapper.search(params);
        logger.debug("getPageByParams pages={}", pages);
        return pages;
    }

    @Override
    public List<PageManagementPojo> getAll() {
        logger.debug("getAll called");

        Map<String, Object> params = new HashMap<>();
        List<PageManagementPojo> pages = getPageByParams(params);
        logger.debug("getAll pages={}", pages);
        return pages;
    }

    @Override
    public PageManagementPojo getPageByUrlPattern(String urlPattern) {
        logger.debug("getPageByUrlPattern called");
        logger.debug("getPageByUrlPattern urlPattern={}", urlPattern);

        Map<String, Object> params = new HashMap<>();
        params.put("urlPattern", urlPattern);

        List<PageManagementPojo> pages = getPageByParams(params);

        if (pages == null || pages.isEmpty()) {
            logger.debug("getPageByUrlPattern found nothing for urlPattern={}", urlPattern);
            return null;
        }

        PageManagementPojo page = pages.get(0);
        logger.debug("getPageByUrlPattern returning page={}", page);
        return page;
    }
    
    @Override
    public List<String> getRoleGroupNamesByUrlPattern(String urlPattern) {
        logger.debug("getRoleGroupNamesByUrlPattern called");
        logger.debug("getRoleGroupNamesByUrlPattern urlPattern={}", urlPattern);

        AntPathMatcher matcher = new AntPathMatcher();
        logger.debug("getRoleGroupNamesByUrlPattern matcher initialized");

        List<PageManagementPojo> allPages = getAll();
        logger.debug("getRoleGroupNamesByUrlPattern allPages={}", allPages);

        List<String> roleGroupNames = new ArrayList<>();
        for (PageManagementPojo page : allPages) {
            logger.debug("getRoleGroupNamesByUrlPattern checking page={}", page);

            String pattern = page.getUrlPattern();
            logger.debug("getRoleGroupNamesByUrlPattern pattern={}", pattern);

            if (matcher.match(pattern, urlPattern)) {
                logger.debug("getRoleGroupNamesByUrlPattern pattern matched for urlPattern={}", urlPattern);

                String delimitRoleGroupNames = page.getDelimitRoleGroups();
                logger.debug("getRoleGroupNamesByUrlPattern delimitRoleGroupNames={}", delimitRoleGroupNames);

                if (delimitRoleGroupNames != null && !delimitRoleGroupNames.isBlank()) {
                    String[] tokens = delimitRoleGroupNames.split("\\|");
                    for (String token : tokens) {
                        logger.debug("getRoleGroupNamesByUrlPattern processing token={}", token);
                        if (!token.isBlank()) {
                            roleGroupNames.add(token.trim());
                            logger.debug("getRoleGroupNamesByUrlPattern added roleGroupName={}", token.trim());
                        }
                    }
                } else {
                    logger.debug("getRoleGroupNamesByUrlPattern no role groups found for pattern={}", pattern);
                }
            } else {
                logger.debug("getRoleGroupNamesByUrlPattern pattern did NOT match urlPattern={}", urlPattern);
            }
        }

        logger.debug("getRoleGroupNamesByUrlPattern ##########");
        logger.debug("getRoleGroupNamesByUrlPattern matched roleGroupNames={}", roleGroupNames);
        logger.debug("getRoleGroupNamesByUrlPattern ##########");

        return roleGroupNames;
    }
	
    @Override
	public List<String> getRoleNamesByUrlPattern(String urlPattern) {
	    logger.debug("getRoleNamesByUrlPattern called");
	    logger.debug("getRoleNamesByUrlPattern urlPattern={}", urlPattern);

	    List<String> roleGroupNames = getRoleGroupNamesByUrlPattern(urlPattern);
	    logger.debug("getRoleNamesByUrlPattern roleGroupNames={}", roleGroupNames);
	    if (roleGroupNames.isEmpty()) {
	        return List.of(); // empty list if no role groups
	    }

	    List<String> roleNames = new ArrayList<>();
	    java.util.Set<String> roleNameSet = new java.util.LinkedHashSet<>();
	    for (String groupName : roleGroupNames) {
	        logger.debug("getRoleNamesByUrlPattern groupName={}", groupName);

	        RoleGroupManagementPojo roleGroup = roleGroupService.getRoleGroupByGroupName(groupName);
	        logger.debug("getRoleNamesByUrlPattern roleGroup={}", roleGroup);
	        if (roleGroup == null) continue;

	        String delimitRoles = roleGroup.getDelimitRoles();
	        logger.debug("getRoleNamesByUrlPattern delimitRoles={}", delimitRoles);
	        if (delimitRoles == null || delimitRoles.isBlank()) continue;

	        String[] tokens = delimitRoles.split("\\|");
	        for (String token : tokens) {
	            if (!token.isBlank()) {
	                roleNameSet.add(token.trim());
	            }
	        }
	    }

	    roleNames = new ArrayList<>(roleNameSet);
	    logger.debug("getRoleNamesByUrlPattern roles={}", roleNames);
	    return roleNames;
	}

}
