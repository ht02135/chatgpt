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

    public void initializeDB() {
        logger.debug("initializeDB called for PageRoleGroupManagementService");

        List<PageRoleGroupConfig> pageConfigs = securityConfigLoader.getPageRoleGroups();
        logger.debug("Loaded page-role group configs from XML, size={}", pageConfigs.size());

        for (PageRoleGroupConfig pageConfig : pageConfigs) {
            String urlPattern = pageConfig.getUrlPattern();
            String roleGroupName = pageConfig.getRoleGroup();

            PageRoleGroupManagementPojo existingPage = getByUrlPattern(Map.of("urlPattern", urlPattern));

            PageRoleGroupManagementPojo pagePojo;
            if (existingPage == null) {
                pagePojo = new PageRoleGroupManagementPojo();
                pagePojo.setUrlPattern(urlPattern);

                RoleGroupManagementPojo roleGroup = roleGroupService.getRoleGroup(Map.of("groupName", roleGroupName));
                if (roleGroup == null) {
                    logger.warn("Role group '{}' not found, skipping page-role mapping for '{}'", roleGroupName, urlPattern);
                    continue;
                }
                pagePojo.setRoleGroup(roleGroup);

                pageMapper.insertPageRoleGroup(Map.of("params", Map.of("pageRoleGroup", pagePojo)));
                logger.debug("Inserted new page-role group id={} urlPattern={} roleGroupId={}",
                        pagePojo.getId(), urlPattern, roleGroup.getId());
            } else {
                pagePojo = existingPage;
                logger.debug("Page-role group already exists id={} urlPattern={} roleGroupId={}",
                        pagePojo.getId(), urlPattern, existingPage.getRoleGroup().getId());
            }

            pageRoleGroupCache.put(pagePojo.getId(), pagePojo);
            logger.debug("Cached page-role group id={} urlPattern={}", pagePojo.getId(), urlPattern);
        }

        logger.debug("initializeDB completed for PageRoleGroupManagementService");
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> searchPageRoleGroups(Map<String, Object> params) {
        logger.debug("searchPageRoleGroups called, params={}", params);

        int page = params.get("page") != null ? (int) params.get("page") : 1;
        int size = params.get("size") != null ? (int) params.get("size") : 20;
        int offset = (page - 1) * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<PageRoleGroupManagementPojo> items = pageMapper.findPageRoleGroups(Map.of("params", params));
        long totalCount = pageMapper.countPageRoleGroups(Map.of("params", params));

        logger.debug("searchPageRoleGroups results size={}", items.size());
        logger.debug("searchPageRoleGroups totalCount={}", totalCount);

        return new PagedResult<>(items, totalCount, page, size);
    }

    @Override
    public List<PageRoleGroupManagementPojo> findAll(Map<String, Object> params) {
        logger.debug("findAll called, params={}", params);
        return pageMapper.findPageRoleGroups(Map.of("params", params));
    }

    @Override
    public PageRoleGroupManagementPojo getById(Map<String, Object> params) {
        Long id = (Long) params.get("id");
        logger.debug("getById called, id={}", id);

        return pageRoleGroupCache.get(id, k -> pageMapper.findById(Map.of("params", Map.of("id", k))));
    }

    @Override
    public PageRoleGroupManagementPojo getByUrlPattern(Map<String, Object> params) {
        String urlPattern = (String) params.get("urlPattern");
        logger.debug("getByUrlPattern called, urlPattern={}", urlPattern);
        return pageMapper.findByUrlPattern(Map.of("params", Map.of("urlPattern", urlPattern)));
    }

    @Override
    public List<PageRoleGroupManagementPojo> getByRoleGroupId(Map<String, Object> params) {
        Long roleGroupId = (Long) params.get("roleGroupId");
        logger.debug("getByRoleGroupId called, roleGroupId={}", roleGroupId);
        return pageMapper.findByRoleGroupId(Map.of("params", Map.of("roleGroupId", roleGroupId)));
    }

    @Override
    public PageRoleGroupManagementPojo create(Map<String, Object> params) {
        PageRoleGroupManagementPojo pageRoleGroup = (PageRoleGroupManagementPojo) params.get("pageRoleGroup");
        logger.debug("create called, pageRoleGroup={}", pageRoleGroup);

        pageMapper.insertPageRoleGroup(Map.of("params", Map.of("pageRoleGroup", pageRoleGroup)));
        pageRoleGroupCache.put(pageRoleGroup.getId(), pageRoleGroup);
        logger.debug("Created and cached page-role group id={} urlPattern={}", pageRoleGroup.getId(), pageRoleGroup.getUrlPattern());

        return pageRoleGroup;
    }

    @Override
    public PageRoleGroupManagementPojo update(Map<String, Object> params) {
        PageRoleGroupManagementPojo pageRoleGroup = (PageRoleGroupManagementPojo) params.get("pageRoleGroup");
        Long id = (Long) params.get("id");
        logger.debug("update called, id={} pageRoleGroup={}", id, pageRoleGroup);

        pageRoleGroup.setId(id);
        pageMapper.updatePageRoleGroup(Map.of("params", Map.of("pageRoleGroup", pageRoleGroup)));
        pageRoleGroupCache.put(id, pageRoleGroup);

        return pageRoleGroup;
    }

    @Override
    public void delete(Map<String, Object> params) {
        Long id = (Long) params.get("id");
        logger.debug("delete called, id={}", id);

        pageRoleGroupCache.invalidate(id);
        pageMapper.deletePageRoleGroupById(Map.of("params", Map.of("id", id)));
        logger.debug("Deleted page-role group from DB and cache id={}", id);
    }
}
