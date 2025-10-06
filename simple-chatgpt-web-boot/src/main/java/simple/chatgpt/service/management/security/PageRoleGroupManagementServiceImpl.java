package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import simple.chatgpt.config.management.loader.SecurityConfigLoader;
import simple.chatgpt.config.management.security.PageRoleGroupConfig;
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
    private void init() {
        logger.debug("init called");
        initializeDB();
    }

    private void initializeDB() {
        logger.debug("initializeDB called for PageRoleGroupManagementService");

        List<PageRoleGroupConfig> pageConfigs = securityConfigLoader.getPageRoleGroups();
        logger.debug("Loaded page-role group configs from XML, size={}", pageConfigs.size());

        for (PageRoleGroupConfig pageConfig : pageConfigs) {
            String urlPattern = pageConfig.getUrlPattern();
            String roleGroupName = pageConfig.getRoleGroup();

            PageRoleGroupManagementPojo existingPage = findByUrlPattern(Map.of("urlPattern", urlPattern));

            PageRoleGroupManagementPojo pagePojo;
            if (existingPage == null) {
                pagePojo = new PageRoleGroupManagementPojo();
                pagePojo.setUrlPattern(urlPattern);

                RoleGroupManagementPojo roleGroup = roleGroupService.findRoleGroupByName(Map.of("groupName", roleGroupName));
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
    public PageRoleGroupManagementPojo insertPageRoleGroup(Map<String, Object> params) {
        logger.debug("insertPageRoleGroup called, params={}", params);
        PageRoleGroupManagementPojo pageRoleGroup = (PageRoleGroupManagementPojo) params.get("pageRoleGroup");
        logger.debug("insertPageRoleGroup pageRoleGroup={}", pageRoleGroup);

        pageMapper.insertPageRoleGroup(Map.of("params", Map.of("pageRoleGroup", pageRoleGroup)));
        pageRoleGroupCache.put(pageRoleGroup.getId(), pageRoleGroup);
        logger.debug("Inserted and cached page-role group id={} urlPattern={}", pageRoleGroup.getId(), pageRoleGroup.getUrlPattern());

        return pageRoleGroup;
    }

    @Override
    public PageRoleGroupManagementPojo updatePageRoleGroup(Map<String, Object> params) {
        logger.debug("updatePageRoleGroup called, params={}", params);
        PageRoleGroupManagementPojo pageRoleGroup = (PageRoleGroupManagementPojo) params.get("pageRoleGroup");
        Long id = (Long) params.get("id");
        logger.debug("updatePageRoleGroup id={}", id);
        logger.debug("updatePageRoleGroup pageRoleGroup={}", pageRoleGroup);

        pageRoleGroup.setId(id);
        pageMapper.updatePageRoleGroup(Map.of("params", Map.of("pageRoleGroup", pageRoleGroup)));
        pageRoleGroupCache.put(id, pageRoleGroup);
        logger.debug("Updated and cached page-role group id={}", id);

        return pageRoleGroup;
    }

    @Override
    public void deletePageRoleGroupById(Map<String, Object> params) {
        Long id = (Long) params.get("id");
        logger.debug("deletePageRoleGroupById called, id={}", id);

        pageRoleGroupCache.invalidate(id);
        pageMapper.deletePageRoleGroupById(Map.of("params", Map.of("id", id)));
        logger.debug("Deleted page-role group from DB and cache id={}", id);
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> findAllPageRoleGroups() {
        logger.debug("findAllPageRoleGroups called");

        List<PageRoleGroupManagementPojo> items = pageMapper.findAllPageRoleGroups();
        long totalCount = items.size();

        logger.debug("findAllPageRoleGroups results size={}", items.size());
        logger.debug("findAllPageRoleGroups totalCount={}", totalCount);

        return new PagedResult<>(items, totalCount, 1, (int) totalCount);
    }

    @Override
    public PageRoleGroupManagementPojo findById(Map<String, Object> params) {
        Long id = (Long) params.get("id");
        logger.debug("findById called, id={}", id);

        return pageRoleGroupCache.get(id, k -> pageMapper.findById(Map.of("params", Map.of("id", k))));
    }

    @Override
    public PageRoleGroupManagementPojo findByUrlPattern(Map<String, Object> params) {
        String urlPattern = (String) params.get("urlPattern");
        logger.debug("findByUrlPattern called, urlPattern={}", urlPattern);

        return pageMapper.findByUrlPattern(Map.of("params", Map.of("urlPattern", urlPattern)));
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> findByRoleGroupId(Map<String, Object> params) {
        Long roleGroupId = params.get("roleGroupId") != null ? ((Number) params.get("roleGroupId")).longValue() : null;
        logger.debug("findByRoleGroupId called, roleGroupId={}", roleGroupId);

        if (roleGroupId == null) {
            logger.warn("findByRoleGroupId called with null roleGroupId");
            return new PagedResult<>(List.of(), 0, 1, 20);
        }

        List<PageRoleGroupManagementPojo> items = pageMapper.findByRoleGroupId(Map.of("params", Map.of("roleGroupId", roleGroupId)));
        long totalCount = items.size();

        logger.debug("findByRoleGroupId results size={}", items.size());
        logger.debug("findByRoleGroupId totalCount={}", totalCount);

        return new PagedResult<>(items, totalCount, 1, (int) totalCount);
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> findPageRoleGroups(Map<String, Object> params) {
        logger.debug("findPageRoleGroups called, params={}", params);

        int page = params.get("page") != null ? (int) params.get("page") : 1;
        int size = params.get("size") != null ? (int) params.get("size") : 20;
        int offset = (page - 1) * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<PageRoleGroupManagementPojo> items = pageMapper.findPageRoleGroups(Map.of("params", params));
        long totalCount = pageMapper.countPageRoleGroups(Map.of("params", params));

        logger.debug("findPageRoleGroups results size={}", items.size());
        logger.debug("findPageRoleGroups totalCount={}", totalCount);

        return new PagedResult<>(items, totalCount, page, size);
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> searchPageRoleGroups(Map<String, Object> params) {
        logger.debug("searchPageRoleGroups called, params={}", params);

        int page = params.get("page") != null ? (int) params.get("page") : 1;
        int size = params.get("size") != null ? (int) params.get("size") : 20;
        int offset = (page - 1) * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<PageRoleGroupManagementPojo> items = pageMapper.searchPageRoleGroups(Map.of("params", params));
        long totalCount = pageMapper.countPageRoleGroups(Map.of("params", params));

        logger.debug("searchPageRoleGroups results size={}", items.size());
        logger.debug("searchPageRoleGroups totalCount={}", totalCount);

        return new PagedResult<>(items, totalCount, page, size);
    }

    @Override
    public long countPageRoleGroups(Map<String, Object> params) {
        logger.debug("countPageRoleGroups called, params={}", params);
        long count = pageMapper.countPageRoleGroups(Map.of("params", params));
        logger.debug("countPageRoleGroups result={}", count);
        return count;
    }
}
