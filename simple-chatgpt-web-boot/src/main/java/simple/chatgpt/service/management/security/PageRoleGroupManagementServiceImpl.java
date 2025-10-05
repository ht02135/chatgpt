package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import simple.chatgpt.config.management.PageRoleGroupConfig;
import simple.chatgpt.config.management.loader.SecurityConfigLoader;
import simple.chatgpt.mapper.management.security.PageRoleGroupManagementMapper;
import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.util.GenericCache;
import simple.chatgpt.util.PagedResult;

@Service
public class PageRoleGroupManagementServiceImpl implements PageRoleGroupManagementService {

    private static final Logger logger = LogManager.getLogger(PageRoleGroupManagementServiceImpl.class);

    private final PageRoleGroupManagementMapper pageMapper;
    private final UserManagementRoleGroupMappingService userRoleGroupMappingService;
    private final RoleGroupManagementService roleGroupService;
    private final SecurityConfigLoader securityConfigLoader;

    private final GenericCache<Long, PageRoleGroupManagementPojo> pageRoleGroupCache;
    private final GenericCache<Long, List<PageRoleGroupManagementPojo>> roleGroupCache;

    @Autowired
    public PageRoleGroupManagementServiceImpl(
            PageRoleGroupManagementMapper pageMapper,
            UserManagementRoleGroupMappingService userRoleGroupMappingService,
            RoleGroupManagementService roleGroupService,
            SecurityConfigLoader securityConfigLoader,
            @Qualifier("pageRoleGroupCache") GenericCache<Long, PageRoleGroupManagementPojo> pageRoleGroupCache,
            @Qualifier("roleGroupCache") GenericCache<Long, List<PageRoleGroupManagementPojo>> roleGroupCache) {

        logger.debug("PageRoleGroupManagementServiceImpl constructor called");
        logger.debug("pageMapper={}", pageMapper);
        logger.debug("userRoleGroupMappingService={}", userRoleGroupMappingService);
        logger.debug("roleGroupService={}", roleGroupService);
        logger.debug("securityConfigLoader={}", securityConfigLoader);
        logger.debug("pageRoleGroupCache={}", pageRoleGroupCache);
        logger.debug("roleGroupCache={}", roleGroupCache);

        this.pageMapper = pageMapper;
        this.userRoleGroupMappingService = userRoleGroupMappingService;
        this.roleGroupService = roleGroupService;
        this.securityConfigLoader = securityConfigLoader;
        this.pageRoleGroupCache = pageRoleGroupCache;
        this.roleGroupCache = roleGroupCache;
    }

    @PostConstruct
    public void postConstruct() {
        initializeDB();
    }

    @PostConstruct
    public void initializeDB() {
        logger.debug("initializeDB called for PageRoleGroupManagementService");

        List<PageRoleGroupConfig> pageConfigs = securityConfigLoader.getPageRoleGroups();
        logger.debug("Loaded page-role group configs from XML, size={}", pageConfigs.size());

        for (PageRoleGroupConfig pageConfig : pageConfigs) {
            String urlPattern = pageConfig.getUrlPattern();
            String roleGroupName = pageConfig.getRoleGroup();

            // 1️ Check if page-role group mapping already exists in DB
            PageRoleGroupManagementPojo existingPage = pageMapper.findByUrlPattern(urlPattern);

            PageRoleGroupManagementPojo pagePojo;
            if (existingPage == null) {
                pagePojo = new PageRoleGroupManagementPojo();
                pagePojo.setUrlPattern(urlPattern);

                // 2️ Find role group for this page
                RoleGroupManagementPojo roleGroup = roleGroupService.getByGroupName(roleGroupName);
                if (roleGroup == null) {
                    logger.warn("Role group '{}' not found, skipping page-role mapping for '{}'",
                            roleGroupName, urlPattern);
                    continue;
                }
                pagePojo.setRoleGroup(roleGroup);

                // 3️ Insert into DB
                pageMapper.insertPageRoleGroup(pagePojo);
                logger.debug("Inserted new page-role group id={} urlPattern={} roleGroupId={}",
                        pagePojo.getId(), urlPattern, roleGroup.getId());
            } else {
                pagePojo = existingPage;
                logger.debug("Page-role group already exists id={} urlPattern={} roleGroupId={}",
                        pagePojo.getId(), urlPattern, existingPage.getRoleGroup().getId());
            }

            // 4️ Cache the page-role group mapping
            pageRoleGroupCache.put(pagePojo.getId(), pagePojo);
            logger.debug("Cached page-role group id={} urlPattern={}", pagePojo.getId(), urlPattern);
        }

        logger.debug("initializeDB completed for PageRoleGroupManagementService");
    }


    @Override
    public PagedResult<PageRoleGroupManagementPojo> searchPageRoleGroups(Map<String, String> params) {
        logger.debug("searchPageRoleGroups called, params={}", params);
        List<PageRoleGroupManagementPojo> items = pageMapper.findAllPageRoleGroups();
        return new PagedResult<>(items, items.size(),
                params.containsKey("page") ? Integer.parseInt(params.get("page")) : 1,
                params.containsKey("size") ? Integer.parseInt(params.get("size")) : items.size());
    }

    @Override
    public List<PageRoleGroupManagementPojo> findAll() {
        logger.debug("findAll called");
        return pageMapper.findAllPageRoleGroups();
    }

    @Override
    public PageRoleGroupManagementPojo getById(Long id) {
        logger.debug("getById called, id={}", id);
        return pageRoleGroupCache.get(id, k -> pageMapper.findByUrlPattern(null)); // fallback if needed
    }

    @Override
    public PageRoleGroupManagementPojo getByUrlPattern(String urlPattern) {
        logger.debug("getByUrlPattern called, urlPattern={}", urlPattern);
        return pageMapper.findByUrlPattern(urlPattern);
    }

    @Override
    public List<PageRoleGroupManagementPojo> getByRoleGroupId(Long roleGroupId) {
        logger.debug("getByRoleGroupId called, roleGroupId={}", roleGroupId);
        return pageMapper.findByRoleGroupId(roleGroupId);
    }

    @Override
    public PageRoleGroupManagementPojo create(PageRoleGroupManagementPojo pageRoleGroup) {
        logger.debug("create called, pageRoleGroup={}", pageRoleGroup);
        pageMapper.insertPageRoleGroup(pageRoleGroup);
        pageRoleGroupCache.put(pageRoleGroup.getId(), pageRoleGroup);
        logger.debug("Created and cached page-role group id={} urlPattern={}", pageRoleGroup.getId(), pageRoleGroup.getUrlPattern());
        return pageRoleGroup;
    }

    @Override
    public PageRoleGroupManagementPojo updateById(Long id, PageRoleGroupManagementPojo pageRoleGroup) {
        logger.debug("updateById called, id={} pageRoleGroup={}", id, pageRoleGroup);
        pageRoleGroup.setId(id);
        pageMapper.updatePageRoleGroup(pageRoleGroup);
        pageRoleGroupCache.put(id, pageRoleGroup);
        return pageRoleGroup;
    }

    @Override
    public void deleteById(Long id) {
        logger.debug("deleteById called, id={}", id);
        pageRoleGroupCache.invalidate(id);
        pageMapper.deletePageRoleGroupById(id);
        logger.debug("Deleted page-role group from DB and cache id={}", id);
    }
}
