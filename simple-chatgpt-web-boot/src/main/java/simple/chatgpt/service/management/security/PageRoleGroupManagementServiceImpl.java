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
        logger.debug("initializeDB START");

        if (securityConfigLoader == null) {
            logger.error("Missing required bean: securityConfigLoader={}", securityConfigLoader);
            logger.debug("initializeDB DONE");
            return;
        }

        List<PageRoleGroupConfig> pageConfigs = securityConfigLoader.getPageRoleGroups();
        logger.debug("initializeDB pageConfigs={}", pageConfigs.size());

        Map<String, RoleGroupManagementPojo> roleGroupByName = roleGroupService.getAllRoleGroups()
                .getItems()
                .stream()
                .collect(Collectors.toMap(RoleGroupManagementPojo::getGroupName, rg -> rg));
        logger.debug("initializeDB roleGroupByName={}", roleGroupByName.size());

        for (PageRoleGroupConfig pageConfig : pageConfigs) {
            String urlPattern = pageConfig.getUrlPattern();
            String roleGroupName = pageConfig.getRoleGroup();

            PageRoleGroupManagementPojo existingPage = findByUrlPattern(ParamWrapper.wrap("urlPattern", urlPattern));

            if (existingPage == null) {
                PageRoleGroupManagementPojo pagePojo = new PageRoleGroupManagementPojo();
                pagePojo.setUrlPattern(urlPattern);

                RoleGroupManagementPojo roleGroup = roleGroupByName.get(roleGroupName);
                if (roleGroup == null) {
                    logger.warn("Role group '{}' not found, skipping page-role mapping for '{}'", roleGroupName, urlPattern);
                    continue;
                }
                pagePojo.setRoleGroup(roleGroup);

                pageMapper.insertPageRoleGroup(ParamWrapper.wrap("pageRoleGroup", pagePojo));
            }
        }

        logger.debug("initializeDB DONE");
    }

    @Override
    public PageRoleGroupManagementPojo insertPageRoleGroup(Map<String, Object> params) {
        logger.debug("insertPageRoleGroup START");
        logger.debug("insertPageRoleGroup params={}", params);

        PageRoleGroupManagementPojo pageRoleGroup = ParamWrapper.unwrap(params, "pageRoleGroup");
        pageMapper.insertPageRoleGroup(ParamWrapper.wrap("pageRoleGroup", pageRoleGroup));
        PageRoleGroupManagementPojo fullPageRoleGroup = pageMapper.findById(
                ParamWrapper.wrap("id", pageRoleGroup.getId())
        );

        logger.debug("insertPageRoleGroup return={}", fullPageRoleGroup);
        return fullPageRoleGroup;
    }

    @Override
    public PageRoleGroupManagementPojo updatePageRoleGroup(Map<String, Object> params) {
        logger.debug("updatePageRoleGroup START");
        logger.debug("updatePageRoleGroup params={}", params);

        PageRoleGroupManagementPojo pageRoleGroup = ParamWrapper.unwrap(params, "pageRoleGroup");
        Long id = ParamWrapper.unwrap(params, "id");
        pageRoleGroup.setId(id);
        pageMapper.updatePageRoleGroup(ParamWrapper.wrap("pageRoleGroup", pageRoleGroup));

        logger.debug("updatePageRoleGroup return={}", pageRoleGroup);
        return pageRoleGroup;
    }

    @Override
    public void deletePageRoleGroupById(Map<String, Object> params) {
        logger.debug("deletePageRoleGroupById START");
        Long id = ParamWrapper.unwrap(params, "id");
        logger.debug("deletePageRoleGroupById id={}", id);

        pageMapper.deletePageRoleGroupById(ParamWrapper.wrap("id", id));

        logger.debug("deletePageRoleGroupById DONE");
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> findAllPageRoleGroups() {
        logger.debug("findAllPageRoleGroups START");

        List<PageRoleGroupManagementPojo> items = pageMapper.findAllPageRoleGroups();
        PagedResult<PageRoleGroupManagementPojo> result = new PagedResult<>(items, items.size(), 1, items.size());

        logger.debug("findAllPageRoleGroups return={}", result);
        return result;
    }

    @Override
    public PageRoleGroupManagementPojo findById(Map<String, Object> params) {
        logger.debug("findById START");
        logger.debug("findById params={}", params);

        PageRoleGroupManagementPojo result = pageMapper.findById(ParamWrapper.wrap("id", ParamWrapper.unwrap(params, "id")));

        logger.debug("findById return={}", result);
        return result;
    }

    @Override
    public PageRoleGroupManagementPojo findByUrlPattern(Map<String, Object> params) {
        logger.debug("findByUrlPattern START");
        logger.debug("findByUrlPattern params={}", params);

        PageRoleGroupManagementPojo result = pageMapper.findByUrlPattern(ParamWrapper.wrap("urlPattern", ParamWrapper.unwrap(params, "urlPattern")));

        logger.debug("findByUrlPattern return={}", result);
        return result;
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> findByRoleGroupId(Map<String, Object> params) {
        logger.debug("findByRoleGroupId START");
        logger.debug("findByRoleGroupId params={}", params);

        Long roleGroupId = ParamWrapper.unwrap(params, "roleGroupId");
        if (roleGroupId == null) {
            logger.warn("findByRoleGroupId called with null roleGroupId");
            PagedResult<PageRoleGroupManagementPojo> emptyResult = new PagedResult<>(List.of(), 0, 1, 20);
            logger.debug("findByRoleGroupId return={}", emptyResult);
            return emptyResult;
        }

        List<PageRoleGroupManagementPojo> items = pageMapper.findByRoleGroupId(ParamWrapper.wrap("roleGroupId", roleGroupId));
        PagedResult<PageRoleGroupManagementPojo> result = new PagedResult<>(items, items.size(), 1, items.size());

        logger.debug("findByRoleGroupId return={}", result);
        return result;
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> findPageRoleGroups(Map<String, Object> params) {
        logger.debug("findPageRoleGroups START");
        logger.debug("findPageRoleGroups params={}", params);

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;
        params.put("offset", offset);
        params.put("limit", size);

        List<PageRoleGroupManagementPojo> items = pageMapper.findPageRoleGroups(params);
        long totalCount = pageMapper.countPageRoleGroups(params);
        PagedResult<PageRoleGroupManagementPojo> result = new PagedResult<>(items, totalCount, page, size);

        logger.debug("findPageRoleGroups return={}", result);
        return result;
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> searchPageRoleGroups(Map<String, Object> params) {
        logger.debug("searchPageRoleGroups START");
        logger.debug("searchPageRoleGroups params={}", params);

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;
        params.put("offset", offset);
        params.put("limit", size);

        List<PageRoleGroupManagementPojo> items = pageMapper.searchPageRoleGroups(params);
        long totalCount = pageMapper.countPageRoleGroups(params);
        PagedResult<PageRoleGroupManagementPojo> result = new PagedResult<>(items, totalCount, page, size);

        logger.debug("searchPageRoleGroups return={}", result);
        return result;
    }

    @Override
    public long countPageRoleGroups(Map<String, Object> params) {
        logger.debug("countPageRoleGroups START");
        logger.debug("countPageRoleGroups params={}", params);

        long count = pageMapper.countPageRoleGroups(params);

        logger.debug("countPageRoleGroups return={}", count);
        return count;
    }
}
