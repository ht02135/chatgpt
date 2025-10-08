package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.config.management.loader.SecurityConfigLoader;
import simple.chatgpt.config.management.security.PageRoleGroupConfig;
import simple.chatgpt.mapper.management.security.PageRoleGroupManagementMapper;
import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
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

    @Autowired
    public PageRoleGroupManagementServiceImpl(
        PageRoleGroupManagementMapper pageMapper,
        UserManagementRoleGroupMappingService userRoleGroupMappingService,
        RoleGroupManagementService roleGroupService,
        SecurityConfigLoader securityConfigLoader) 
    {
        logger.debug("PageRoleGroupManagementServiceImpl constructor called");
        logger.debug("pageMapper={}", pageMapper);
        logger.debug("userRoleGroupMappingService={}", userRoleGroupMappingService);
        logger.debug("roleGroupService={}", roleGroupService);
        logger.debug("securityConfigLoader={}", securityConfigLoader);

        this.pageMapper = pageMapper;
        this.userRoleGroupMappingService = userRoleGroupMappingService;
        this.roleGroupService = roleGroupService;
        this.securityConfigLoader = securityConfigLoader;
    }

    @PostConstruct
    private void init() {
        logger.debug("init called");
        initializeDB();
    }

    private void initializeDB() {
        logger.debug("initializeDB called for PageRoleGroupManagementService");

        if (securityConfigLoader == null) {
            logger.error("Missing required bean: securityConfigLoader={}", securityConfigLoader);
            return;
        }

        List<PageRoleGroupConfig> pageConfigs = securityConfigLoader.getPageRoleGroups();
        logger.debug("Loaded page-role group configs from XML, size={}", pageConfigs.size());

        Map<String, RoleGroupManagementPojo> roleGroupByName = roleGroupService.getAllRoleGroups()
                .getItems()
                .stream()
                .collect(Collectors.toMap(RoleGroupManagementPojo::getGroupName, rg -> rg));
        logger.debug("Fetched all role groups size={}", roleGroupByName.size());

        for (PageRoleGroupConfig pageConfig : pageConfigs) {
            String urlPattern = pageConfig.getUrlPattern();
            String roleGroupName = pageConfig.getRoleGroup();

            PageRoleGroupManagementPojo existingPage = findByUrlPattern(ParamWrapper.wrap("urlPattern", urlPattern));

            PageRoleGroupManagementPojo pagePojo;
            if (existingPage == null) {
                pagePojo = new PageRoleGroupManagementPojo();
                pagePojo.setUrlPattern(urlPattern);

                RoleGroupManagementPojo roleGroup = roleGroupByName.get(roleGroupName);
                if (roleGroup == null) {
                    logger.warn("Role group '{}' not found, skipping page-role mapping for '{}'", roleGroupName, urlPattern);
                    continue;
                }
                pagePojo.setRoleGroup(roleGroup);

                pageMapper.insertPageRoleGroup(ParamWrapper.wrap("pageRoleGroup", pagePojo));
                logger.debug("Inserted new page-role group id={} urlPattern={} roleGroupId={}",
                        pagePojo.getId(), urlPattern, roleGroup.getId());
            } else {
                logger.debug("Page-role group already exists id={} urlPattern={} roleGroupId={}",
                        existingPage.getId(), urlPattern, existingPage.getRoleGroup().getId());
            }
        }

        logger.debug("initializeDB completed for PageRoleGroupManagementService");
    }

    @Override
    public PageRoleGroupManagementPojo insertPageRoleGroup(Map<String, Object> params) {
        logger.debug("insertPageRoleGroup called, params={}", params);

        PageRoleGroupManagementPojo pageRoleGroup = ParamWrapper.unwrap(params, "pageRoleGroup");
        logger.debug("insertPageRoleGroup before insert pageRoleGroup={}", pageRoleGroup);

        pageMapper.insertPageRoleGroup(ParamWrapper.wrap("pageRoleGroup", pageRoleGroup));
        logger.debug("insertPageRoleGroup after insert, pageRoleGroup.id={}", pageRoleGroup.getId());

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

        pageMapper.deletePageRoleGroupById(ParamWrapper.wrap("id", id));
        logger.debug("Deleted page-role group from DB, id={}", id);
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> findAllPageRoleGroups() {
        logger.debug("findAllPageRoleGroups called");

        List<PageRoleGroupManagementPojo> items = pageMapper.findAllPageRoleGroups();
        long totalCount = items.size();

        logger.debug("findAllPageRoleGroups results size={}", items.size());
        return new PagedResult<>(items, totalCount, 1, (int) totalCount);
    }

    @Override
    public PageRoleGroupManagementPojo findById(Map<String, Object> params) {
        Long id = ParamWrapper.unwrap(params, "id");
        logger.debug("findById called, id={}", id);

        return pageMapper.findById(ParamWrapper.wrap("id", id));
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
        return new PagedResult<>(items, totalCount, 1, (int) totalCount);
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> findPageRoleGroups(Map<String, Object> params) {
        logger.debug("findPageRoleGroups called, params={}", params);

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0); 
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<PageRoleGroupManagementPojo> items = pageMapper.findPageRoleGroups(params);
        long totalCount = pageMapper.countPageRoleGroups(params);

        logger.debug("findPageRoleGroups results size={}", items.size());
        return new PagedResult<>(items, totalCount, page, size);
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> searchPageRoleGroups(Map<String, Object> params) {
        logger.debug("searchPageRoleGroups called, params={}", params);

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0); 
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<PageRoleGroupManagementPojo> items = pageMapper.searchPageRoleGroups(params);
        long totalCount = pageMapper.countPageRoleGroups(params);

        logger.debug("searchPageRoleGroups results size={}", items.size());
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
