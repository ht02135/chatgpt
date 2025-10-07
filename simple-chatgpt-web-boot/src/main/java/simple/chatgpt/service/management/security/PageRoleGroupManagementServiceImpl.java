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
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.SafeConverter;

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

        if (securityConfigLoader == null || pageRoleGroupCache == null || roleGroupCache == null) {
            logger.error("Missing required beans: securityConfigLoader={}, pageRoleGroupCache={}, roleGroupCache={}", 
                securityConfigLoader, pageRoleGroupCache, roleGroupCache);
            return;
        }

        List<PageRoleGroupConfig> pageConfigs = securityConfigLoader.getPageRoleGroups();
        logger.debug("Loaded page-role group configs from XML, size={}", pageConfigs.size());

        for (PageRoleGroupConfig pageConfig : pageConfigs) {
            String urlPattern = pageConfig.getUrlPattern();
            String roleGroupName = pageConfig.getRoleGroup();

            PageRoleGroupManagementPojo existingPage = findByUrlPattern(ParamWrapper.wrap("urlPattern", urlPattern));

            PageRoleGroupManagementPojo pagePojo;
            if (existingPage == null) {
                pagePojo = new PageRoleGroupManagementPojo();
                pagePojo.setUrlPattern(urlPattern);

                RoleGroupManagementPojo roleGroup = roleGroupService.findRoleGroupByName(ParamWrapper.wrap("groupName", roleGroupName));
                if (roleGroup == null) {
                    logger.warn("Role group '{}' not found, skipping page-role mapping for '{}'", roleGroupName, urlPattern);
                    continue;
                }
                pagePojo.setRoleGroup(roleGroup);

                pageMapper.insertPageRoleGroup(ParamWrapper.wrap("pageRoleGroup", pagePojo));
                logger.debug("Inserted new page-role group id={} urlPattern={} roleGroupId={}",
                        pagePojo.getId(), urlPattern, roleGroup.getId());
            } else {
                pagePojo = existingPage;
                logger.debug("Page-role group already exists id={} urlPattern={} roleGroupId={}",
                        pagePojo.getId(), urlPattern, existingPage.getRoleGroup().getId());
            }
        }

        logger.debug("initializeDB completed for PageRoleGroupManagementService");
    }

    @Override
    public PageRoleGroupManagementPojo insertPageRoleGroup(Map<String, Object> params) {
        logger.debug("insertPageRoleGroup called, params={}", params);

        PageRoleGroupManagementPojo pageRoleGroup = ParamWrapper.unwrap(params, "pageRoleGroup");
        logger.debug("insertPageRoleGroup before insert pageRoleGroup={}", pageRoleGroup);

        // Insert into DB
        pageMapper.insertPageRoleGroup(ParamWrapper.wrap("pageRoleGroup", pageRoleGroup));
        logger.debug("insertPageRoleGroup after insert, pageRoleGroup.id={}", pageRoleGroup.getId());

        // Re-fetch from DB to get all populated fields (timestamps, role group join, etc.)
        PageRoleGroupManagementPojo fullPageRoleGroup = pageMapper.findById(
            ParamWrapper.wrap("id", pageRoleGroup.getId())
        );
        logger.debug("insertPageRoleGroup fetched fullPageRoleGroup={}", fullPageRoleGroup);

        return fullPageRoleGroup;
    }

    @Override
    public PageRoleGroupManagementPojo updatePageRoleGroup(Map<String, Object> params) {
        logger.debug("updatePageRoleGroup called, params={}", params);
        PageRoleGroupManagementPojo pageRoleGroup = ParamWrapper.unwrap(params, "pageRoleGroup");
        Long id = ParamWrapper.unwrap(params, "id");
        logger.debug("updatePageRoleGroup id={}", id);
        logger.debug("updatePageRoleGroup pageRoleGroup={}", pageRoleGroup);

        pageRoleGroup.setId(id);
        pageMapper.updatePageRoleGroup(ParamWrapper.wrap("pageRoleGroup", pageRoleGroup));

        return pageRoleGroup;
    }

    @Override
    public void deletePageRoleGroupById(Map<String, Object> params) {
        Long id = ParamWrapper.unwrap(params, "id");
        logger.debug("deletePageRoleGroupById called, id={}", id);

        pageRoleGroupCache.invalidate(id);
        pageMapper.deletePageRoleGroupById(ParamWrapper.wrap("id", id));
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
        Long id = ParamWrapper.unwrap(params, "id");
        logger.debug("findById called, id={}", id);

        return pageRoleGroupCache.get(id, k -> pageMapper.findById(ParamWrapper.wrap("id", k)));
    }

    @Override
    public PageRoleGroupManagementPojo findByUrlPattern(Map<String, Object> params) {
        String urlPattern = ParamWrapper.unwrap(params, "urlPattern");
        logger.debug("findByUrlPattern called, urlPattern={}", urlPattern);

        return pageMapper.findByUrlPattern(ParamWrapper.wrap("urlPattern", urlPattern));
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> findByRoleGroupId(Map<String, Object> params) {
        Long roleGroupId = ParamWrapper.unwrap(params, "roleGroupId");
        logger.debug("findByRoleGroupId called, roleGroupId={}", roleGroupId);

        if (roleGroupId == null) {
            logger.warn("findByRoleGroupId called with null roleGroupId");
            return new PagedResult<>(List.of(), 0, 1, 20);
        }

        List<PageRoleGroupManagementPojo> items = pageMapper.findByRoleGroupId(ParamWrapper.wrap("roleGroupId", roleGroupId));
        long totalCount = items.size();

        logger.debug("findByRoleGroupId results size={}", items.size());
        logger.debug("findByRoleGroupId totalCount={}", totalCount);

        return new PagedResult<>(items, totalCount, 1, (int) totalCount);
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> findPageRoleGroups(Map<String, Object> params) {
        logger.debug("findPageRoleGroups called, params={}", params);

        /*
        hung: DONT REMOVE THIS CODE
        */
        int page = 0;
        int size = 20;
        try {
            page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0); 
        } catch (NumberFormatException e) {
            logger.warn("Invalid page param {}, defaulting to 0", ParamWrapper.unwrap(params, "page", 0), e);
        }
        try {
            size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        } catch (NumberFormatException e) {
            logger.warn("Invalid size param {}, defaulting to 20", ParamWrapper.unwrap(params, "size", 20), e);
        }
        int offset = (page - 1) * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<PageRoleGroupManagementPojo> items = pageMapper.findPageRoleGroups(params);
        long totalCount = pageMapper.countPageRoleGroups(params);

        logger.debug("findPageRoleGroups results size={}", items.size());
        logger.debug("findPageRoleGroups totalCount={}", totalCount);

        return new PagedResult<>(items, totalCount, page, size);
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> searchPageRoleGroups(Map<String, Object> params) {
        logger.debug("searchPageRoleGroups called, params={}", params);

        /*
        hung: DONT REMOVE THIS CODE
        */
        int page = 0;
        int size = 20;
        try {
            page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        } catch (NumberFormatException e) {
            logger.warn("Invalid page param {}, defaulting to 0", ParamWrapper.unwrap(params, "page", 0), e);
        }
        try {
            size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        } catch (NumberFormatException e) {
            logger.warn("Invalid size param {}, defaulting to 20", ParamWrapper.unwrap(params, "size", 20), e);
        }
        int offset = (page - 1) * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<PageRoleGroupManagementPojo> items = pageMapper.searchPageRoleGroups(params);
        long totalCount = pageMapper.countPageRoleGroups(params);

        logger.debug("searchPageRoleGroups results size={}", items.size());
        logger.debug("searchPageRoleGroups totalCount={}", totalCount);

        return new PagedResult<>(items, totalCount, page, size);
    }

    @Override
    public long countPageRoleGroups(Map<String, Object> params) {
        logger.debug("countPageRoleGroups called, params={}", params);
        long count = pageMapper.countPageRoleGroups(params);
        logger.debug("countPageRoleGroups result={}", count);
        return count;
    }
}
